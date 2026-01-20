// src/components/OrderConfirmationModal.jsx
import React, { useState } from 'react';
import { createSale, processElectronicInvoice } from '../services/api';

const OrderConfirmationModal = ({ 
  isOpen, 
  onClose, 
  onConfirm, 
  cartItems, 
  orderNumber, 
  subtotal, 
  iva, 
  total, 
  paymentMethods, 
  paymentTerm, 
  notes = "",
  // Nuevas props necesarias para la venta
  selectedClient,
  selectedSalePoint,
  selectedDocumentType,
  combinedPayment,
  paymentDistribution
}) => {
  const [isProcessing, setIsProcessing] = useState(false);
  const [processingStatus, setProcessingStatus] = useState('');

  if (!isOpen) return null;

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(amount);
  };

  // USAR LAS PROPS DIRECTAMENTE desde PaymentModal (ya calculadas correctamente)
  const calculatedTotal = total; // Total = precio original con IVA incluido  
  const calculatedSubtotal = subtotal || (total / 1.13); // Subtotal sin IVA
  const calculatedIva = iva || (total - (total / 1.13)); // IVA extraído
  
  // Valores por defecto para subcargos y descuentos
  const surcharge = 0;
  const discount = 0;

  // Función para crear estructura de venta
  const buildSaleData = () => {
    const now = new Date();
    const emissionDate = now.toISOString().split('T')[0]; // YYYY-MM-DD
    const emissionTime = now.toTimeString().split(' ')[0]; // HH:mm:ss

    // Construir detalles de productos
    const details = cartItems.map(item => {
      const priceWithVat = item.price; // El precio ya incluye IVA
      const price = priceWithVat / 1.13; // Precio sin IVA
      const subjectAmount = price * item.quantity;
      const subjectAmountWithVat = priceWithVat * item.quantity;

      return {
        quantity: item.quantity,
        price: Math.round(price * 100) / 100, // Redondear a 2 decimales
        price_with_vat: priceWithVat,
        price_without_discount: Math.round(price * 100) / 100, // Agregar precio sin descuento
        retail_price: priceWithVat,
        discount: 0,                          // Agregar descuento
        cost: 0,                             // Agregar costo
        not_subject_amount: 0,
        exempt_amount: 0,
        subject_amount: Math.round(subjectAmount * 100) / 100,
        subject_amount_with_vat: Math.round(subjectAmountWithVat * 100) / 100,
        alternative_name: item.name,
        remark: null,                        // Agregar remark
        position: 0,                         // Agregar posición
        article_id: item.id
      };
    });

    // Calcular totales correctamente
    const totalSubjectAmount = details.reduce((sum, detail) => sum + detail.subject_amount, 0);
    const totalSubjectAmountWithVat = details.reduce((sum, detail) => sum + detail.subject_amount_with_vat, 0);
    const totalTaxAmount = totalSubjectAmountWithVat - totalSubjectAmount;

    // ✅ LÓGICA AUTOMÁTICA DE TIPO DE DOCUMENTO SEGÚN CLIENTE
    // Si el cliente tiene NRC = Contribuyente IVA = Crédito Fiscal
    // Si no tiene NRC = Consumidor Final = Factura de Consumidor Final
    const isIVAContributor = selectedClient?.nrc && selectedClient.nrc.trim() !== '';
    const autoDocumentType = isIVAContributor ? 2 : 1; // 2 = Crédito Fiscal, 1 = Factura de Consumidor Final
    const finalDocumentType = selectedDocumentType || autoDocumentType;

    // Construir impuestos
    const taxes = [{
      own_exempt_amount_total: 0,
      own_not_subject_amount_total: 0,
      own_subject_amount_total: Math.round(totalSubjectAmount * 100) / 100,
      own_subject_amount_tax_total: Math.round(totalTaxAmount * 100) / 100,
      third_party_not_subject_amount_total: 0,    // Agregar campos de terceros
      third_party_exempt_amount_total: 0,
      third_party_subject_amount_total: 0,
      third_party_subject_amount_tax_total: 0,
      date: null,                                 // Agregar campos adicionales
      serial_number: null,
      document_number: null,
      remark: null,
      additional_amount: 0,
      tax_id: 1 // IVA
    }];

    // Construir pagos
    let payments;
    if (combinedPayment && paymentDistribution) {
      payments = paymentDistribution
        .filter(dist => dist.amount > 0)
        .map(dist => ({
          amount: dist.amount,
          payment_form_id: dist.payment_form_id
        }));
    } else {
      // Si no hay pago combinado, usar el primer método de pago disponible
      const firstPaymentMethod = paymentMethods && paymentMethods.length > 0 ? paymentMethods[0] : null;
      payments = [{
        amount: Math.round(totalSubjectAmountWithVat * 100) / 100, // Usar el total real con IVA
        payment_form_id: firstPaymentMethod ? firstPaymentMethod.id : 1 // Fallback a ID 1
      }];
    }

    return {
      data: {
        type: 'sale',
        attributes: {
          setting: '',
          document_number: Math.floor(Date.now() / 1000), // Número único basado en timestamp
          emission_date: emissionDate,
          emission_time: emissionTime,
          registration_date: emissionDate, // Agregar fecha de registro
          collection_date: emissionDate,   // Agregar fecha de cobro
          payment_date: emissionDate,      // Agregar fecha de pago
          remark: 'Venta POS',
          operation_type: '1',             // Tipo de operación 1
          income_type: null,
          transmission_type: '1',          // Tipo de transmisión 1
          type: 'O',                       // Tipo O (Original)
          export_item_type: null,
          exemption_type: null,
          export_type: null,
          export_item_type_code: null,
          fiscal_enclosure_code: null,
          fiscal_enclosure_name: null,
          export_regime_code: null,
          export_regime_name: null,
          incoterm_code: null,
          incoterm_name: null,
          goods_dispatch_title: null,
          local: null,
          carrier: null,
          site: null,
          shipments: null,
          other: null,
          status: 'P',                     // Status pendiente
          
          // ✅ CAMPOS OBLIGATORIOS PARA HACIENDA - Totales del resumen
          not_subject_amount_sum: 0,
          exempt_amount_sum: 0,
          subject_amount_sum: Math.round(totalSubjectAmount * 100) / 100, // Subtotal sin IVA
          collected_tax_amount_sum: Math.round(totalTaxAmount * 100) / 100, // Total IVA
          withheld_tax_amount_sum: 0,
          vat_amount: 0,
          cashback_amount: 0,
          points_amount: 0,
          other_discounts_amount: 0,
          sales_total: Math.round(totalSubjectAmount * 100) / 100, // Total ventas sin IVA
          origin_total: 0,
          advanced_paid_total: 0,
          paid_total: 0,
          initial_total_amount_due: 0,
          credited_cashback: 0,
          
          client_id: selectedClient?.id || 1,
          document_type_id: finalDocumentType,
          sale_point_id: selectedSalePoint?.id || 1,
          sale_point_document_type_id: finalDocumentType,
          payment_term_id: paymentTerm?.id || 1,
          payment_form_id: combinedPayment ? null : (paymentMethods?.[0]?.id || 1),
          details,
          taxes,
          payments
        }
      }
    };
  };

  const handleConfirmSale = async () => {
    setIsProcessing(true);
    setProcessingStatus('Creando venta...');

    try {
      // Crear venta
      const saleData = buildSaleData();
      
      const saleResponse = await createSale(saleData);
      
      const saleId = saleResponse.data.id;
      setProcessingStatus(`Venta creada (ID: ${saleId}). Generando DTE...`);

      // Procesar DTE
      const dteResponse = await processElectronicInvoice(saleId);
      
      setProcessingStatus('¡Venta completada exitosamente!');
      
      // Llamar función de confirmación original
      if (onConfirm) {
        onConfirm({
          saleId,
          saleResponse,
          dteResponse
        });
      }
      
      setTimeout(() => {
        setIsProcessing(false);
        setProcessingStatus('');
        onClose();
      }, 2000);
      
    } catch (error) {
      console.error('Error procesando venta:', error);
      setProcessingStatus(`Error: ${error.message}`);
      
      setTimeout(() => {
        setIsProcessing(false);
        setProcessingStatus('');
      }, 3000);
    }
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center z-50" style={{ backdropFilter: 'blur(8px)', backgroundColor: 'rgba(0, 0, 0, 0.4)' }}>
      <div 
        className="bg-white rounded-lg overflow-hidden shadow-xl"
        style={{
          width: '704px',
          maxHeight: '90vh',
          opacity: 1
        }}
      >
        {/* Header */}
        <div 
          className="border-b flex justify-between items-center"
          style={{
            paddingTop: '24px',
            paddingRight: '24px',
            paddingBottom: '16px',
            paddingLeft: '24px',
            gap: '8px',
            borderBottomWidth: '1px'
          }}
        >
          <h2 className="text-xl font-semibold text-gray-900">
            Orden de confirmación
          </h2>
          <button
            onClick={onClose}
            className="text-2xl font-bold transition-colors duration-200 hover:opacity-80"
            style={{
              backgroundColor: '#FFFFFF',
              color: '#0095FF',
              border: 'none',
              padding: '4px 8px',
              borderRadius: '4px'
            }}
          >
            ×
          </button>
        </div>

        <div className="p-6 max-h-[calc(90vh-120px)] overflow-y-auto">
          <div className="mb-6">
            <p className="text-sm text-gray-600 mb-4">
              Por favor, confirme el pedido a continuación para completar el pago:
            </p>

            {/* Tabla de productos */}
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="text-sm font-medium text-gray-700 border-b">
                    <th className="text-left py-3">ARTÍCULO</th>
                    <th className="text-center py-3">CANT</th>
                    <th className="text-right py-3">PRECIO (c/IVA)</th>
                    <th className="text-right py-3">IVA</th>
                    <th className="text-right py-3">TOTAL</th>
                  </tr>
                </thead>
                <tbody>
                  {cartItems?.map((item, index) => {
                    const itemTotal = item.price * item.quantity; // Total con IVA (correcto)
                    const itemSubtotalWithoutVat = itemTotal / 1.13; // Sin IVA  
                    const itemIva = itemTotal - itemSubtotalWithoutVat; // IVA real
                    
                    return (
                      <tr key={index} className="border-b border-gray-100">
                        <td className="py-3 flex items-center">
                          <span className="text-yellow-500 mr-2">🧊</span>
                          <span className="text-sm">{item.name}</span>
                        </td>
                        <td className="text-center py-3 text-sm">{item.quantity}</td>
                        <td className="text-right py-3 text-sm">{formatCurrency(item.price)}</td>
                        <td className="text-right py-3 text-sm">{formatCurrency(itemIva)}</td>
                        <td className="text-right py-3 text-sm font-medium">{formatCurrency(itemTotal)}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>

            {/* Sección inferior con notas y totales */}
            <div className="grid grid-cols-2 gap-6 mt-6">
              {/* Notas */}
              <div>
                <h3 className="text-sm font-medium text-gray-700 mb-2">NOTAS</h3>
                <div 
                  className="p-3 rounded text-sm text-gray-600"
                  style={{
                    backgroundColor: '#E4E4E4',
                    border: '1px solid #E4E4E4'
                  }}
                >
                  {notes || "Yummy has been the industry's standard dummy text ever since the 1500s when an unknown printer took a galley of type and scrambled it. When has a type!"}
                </div>
              </div>

              {/* Totales */}
              <div>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">SUBTOTAL</span>
                    <span className="font-medium">{formatCurrency(calculatedSubtotal)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">SUBCARGADO</span>
                    <span className="font-medium">{formatCurrency(surcharge)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">DESCUENTO EN PEDIDO</span>
                    <span className="font-medium">{formatCurrency(discount)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">IVA</span>
                    <span className="font-medium">{formatCurrency(calculatedIva)}</span>
                  </div>
                  <div className="flex justify-between border-t pt-2 font-bold">
                    <span>TOTAL</span>
                    <span style={{ color: '#0095FF' }}>{formatCurrency(calculatedTotal)}</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Métodos de pago */}
            <div className="mt-6">
              <h3 className="text-sm font-medium text-gray-700 mb-3">Métodos de pago confirmados</h3>
              <div className="space-y-3">
                {/* Condición de pago */}
                {paymentTerm && (
                  <div className="flex items-center gap-3">
                    <span className="text-sm font-medium text-gray-600">Condición:</span>
                    <div 
                      className="flex items-center gap-2 px-3 py-2 rounded-lg border"
                      style={{ backgroundColor: '#FFFFFF' }}
                    >
                      <span className="text-sm font-medium">{paymentTerm.displayText}</span>
                      {paymentTerm.paymentPeriod > 0 && (
                        <span className="text-xs text-gray-500">({paymentTerm.paymentPeriod} días)</span>
                      )}
                    </div>
                  </div>
                )}
                
                {/* Métodos de pago */}
                <div className="flex items-center gap-3">
                  <span className="text-sm font-medium text-gray-600">Método:</span>
                  <div className="flex gap-3">
                    {paymentMethods?.map((method, index) => (
                      <div 
                        key={index}
                        className="flex items-center gap-2 px-3 py-2 rounded-lg border"
                        style={{ backgroundColor: '#FFFFFF' }}
                      >
                        <span className="text-sm">💳</span>
                        <span className="text-sm font-medium">{method.name}</span>
                      </div>
                    )) || (
                      <div className="text-sm text-gray-500">No se seleccionó método de pago</div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer con botones */}
        <div 
          className="border-t px-6 py-4 flex justify-end gap-3"
          style={{ backgroundColor: '#FFFFFF' }}
        >
          <button
            onClick={onClose}
            disabled={isProcessing}
            className="px-6 py-2 rounded-lg text-sm font-medium transition-colors duration-200 hover:opacity-80"
            style={{
              backgroundColor: '#FFFFFF',
              color: '#0095FF',
              border: '1px solid #E5E7EB',
              opacity: isProcessing ? 0.5 : 1
            }}
          >
            Cancelar
          </button>
          <button
            onClick={handleConfirmSale}
            disabled={isProcessing}
            className="px-6 py-2 rounded-lg text-white transition-colors min-w-[120px]"
            style={{ 
              backgroundColor: isProcessing ? '#9CA3AF' : '#0095FF',
              cursor: isProcessing ? 'not-allowed' : 'pointer'
            }}
            onMouseEnter={(e) => {
              if (!isProcessing) {
                e.target.style.backgroundColor = '#007ACC';
              }
            }}
            onMouseLeave={(e) => {
              if (!isProcessing) {
                e.target.style.backgroundColor = '#0095FF';
              }
            }}
          >
            {isProcessing ? '...' : 'Confirmar'}
          </button>
        </div>
        
        {/* Status de procesamiento */}
        {processingStatus && (
          <div className="px-6 py-2 bg-blue-50 border-t border-blue-200">
            <p className="text-sm text-blue-700 text-center">{processingStatus}</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default OrderConfirmationModal;