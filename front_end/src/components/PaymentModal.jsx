// src/components/PaymentModal.jsx
import React, { useState, useEffect } from 'react';
import { fetchDocumentTypes, fetchPaymentTerms, fetchPaymentForms } from '../services/api';
import OrderConfirmationModal from './OrderConfirmationModal';
import Toast from './Toast';

const PaymentModal = ({ isOpen, onClose, cartItems, orderNumber, total, ivaAmount, onConfirmPayment, selectedClient, selectedSalePoint }) => {
  const [selectedDocumentType, setSelectedDocumentType] = useState(1); // Default to ID 1
  const [documentTypes, setDocumentTypes] = useState([]);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [paymentTerms, setPaymentTerms] = useState([]);
  const [loadingDocumentTypes, setLoadingDocumentTypes] = useState(true);
  const [loadingPaymentForms, setLoadingPaymentForms] = useState(true);
  const [loadingPaymentTerms, setLoadingPaymentTerms] = useState(true);
  const [combinedPayment, setCombinedPayment] = useState(false);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState(null);
  const [selectedPaymentTerm, setSelectedPaymentTerm] = useState(1); // Default to first term (Contado)
  const [activeBottomButton, setActiveBottomButton] = useState(null);
  const [showOrderConfirmation, setShowOrderConfirmation] = useState(false);
  const [showToast, setShowToast] = useState(false);

  // Debug effect para showToast
  useEffect(() => {
    console.log('showToast changed:', showToast);
  }, [showToast]);

  // Load document types from API
  useEffect(() => {
    const loadDocumentTypes = async () => {
      try {
        setLoadingDocumentTypes(true);
        const types = await fetchDocumentTypes();
        setDocumentTypes(types);
        // Ensure default selection exists, otherwise use first available
        if (types.length > 0 && !types.find(t => t.id === 1)) {
          setSelectedDocumentType(types[0].id);
        }
      } catch (error) {
        console.error('Error loading document types:', error);
      } finally {
        setLoadingDocumentTypes(false);
      }
    };

    if (isOpen) {
      loadDocumentTypes();
    }
  }, [isOpen]);

  // Load payment forms from API
  useEffect(() => {
    const loadPaymentForms = async () => {
      try {
        setLoadingPaymentForms(true);
        const forms = await fetchPaymentForms();
        setPaymentMethods(forms);
        // Set first payment form as default if available
        if (forms.length > 0) {
          setSelectedPaymentMethod(forms[0].id);
        }
      } catch (error) {
        console.error('Error loading payment forms:', error);
      } finally {
        setLoadingPaymentForms(false);
      }
    };

    if (isOpen) {
      loadPaymentForms();
    }
  }, [isOpen]);

  // Load payment terms from API
  useEffect(() => {
    const loadPaymentTerms = async () => {
      try {
        setLoadingPaymentTerms(true);
        const terms = await fetchPaymentTerms();
        setPaymentTerms(terms);
        // Set first payment term as default if available
        if (terms.length > 0) {
          setSelectedPaymentTerm(terms[0].id);
        }
      } catch (error) {
        console.error('Error loading payment terms:', error);
      } finally {
        setLoadingPaymentTerms(false);
      }
    };

    if (isOpen) {
      loadPaymentTerms();
    }
  }, [isOpen]);

  if (!isOpen) return null;

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(amount);
  };

  const bottomButtons = [
    {
      id: 'informacion',
      label: 'Información Adicional',
      icon: 'ℹ️'
    },
    {
      id: 'impuesto',
      label: 'Impuesto Descuento',
      icon: '%'
    },
    {
      id: 'procesar',
      label: 'Procesar pedido',
      icon: '✓'
    }
  ];

  const handleBottomButtonClick = (buttonId) => {
    console.log('Button clicked:', buttonId, 'selectedClient:', selectedClient, 'selectedSalePoint:', selectedSalePoint);
    if (buttonId === 'procesar') {
      // Validar que exista cliente y punto de venta
      if (!selectedClient || !selectedSalePoint) {
        console.log('Showing toast - missing client or sale point');
        setShowToast(true);
        return;
      }
      setShowOrderConfirmation(true);
    } else {
      setActiveBottomButton(activeBottomButton === buttonId ? null : buttonId);
    }
  };

  const handleToastAccept = () => {
    console.log('Toast accepted, hiding toast');
    setShowToast(false);
  };

  const handleConfirmPayment = () => {
    // Aquí irá la lógica para procesar el pago y generar DTE
    console.log('Procesando pago:', {
      orderNumber,
      documentType: selectedDocumentType,
      paymentMethod: selectedPaymentMethod,
      combinedPayment,
      total,
      ivaAmount
    });
    
    // Llamar a la función de confirmación del padre
    if (onConfirmPayment) {
      onConfirmPayment({
        orderNumber,
        documentType: selectedDocumentType,
        paymentMethod: selectedPaymentMethod,
        combinedPayment,
        total,
        ivaAmount
      });
    }
    
    // Cerrar modal
    onClose();
  };

  const handleOrderConfirmation = () => {
    // Cerrar modal de confirmación y modal principal
    setShowOrderConfirmation(false);
    handleConfirmPayment();
  };

  const handleCloseOrderConfirmation = () => {
    setShowOrderConfirmation(false);
  };

  // Obtener métodos de pago seleccionados
  const getSelectedPaymentMethods = () => {
    if (!selectedPaymentMethod || paymentMethods.length === 0) {
      return [];
    }
    
    const selectedMethod = paymentMethods.find(method => method.id === selectedPaymentMethod);
    return selectedMethod ? [selectedMethod] : [];
  };

  // Obtener condición de pago seleccionada
  const getSelectedPaymentTerm = () => {
    if (!selectedPaymentTerm || paymentTerms.length === 0) {
      return null;
    }
    
    return paymentTerms.find(term => term.id === selectedPaymentTerm) || null;
  };

  return (
    <>
      <div 
        className="fixed right-0 top-0 z-50 bg-white border-l border-gray-100 overflow-y-auto flex flex-col"
        style={{
          width: '536px',
          height: '100vh',
          opacity: 1
        }}
      >
        {/* Header */}
        <div className="flex justify-between items-center px-6 py-4 border-b border-gray-100">
          <h2 className="text-xl font-semibold" style={{ color: '#0095FF' }}>
            Pago del pedido
          </h2>
          
          <button
            onClick={onClose}
            className="text-xl font-bold p-2 rounded-lg transition-colors"
            style={{ 
              color: '#4242428C',
              backgroundColor: '#FFFFFF',
              border: '1px solid #E5E5E5'
            }}
            onMouseEnter={(e) => {
              e.target.style.color = '#424242';
              e.target.style.backgroundColor = '#F8F8F8';
            }}
            onMouseLeave={(e) => {
              e.target.style.color = '#4242428C';
              e.target.style.backgroundColor = '#FFFFFF';
            }}
          >
            ×
          </button>
        </div>

        {/* Content */}
        <div className="px-6 py-4 space-y-6 flex-1">
          {/* Orden Number */}
          <div className="flex items-center gap-2">
            <span className="text-gray-600">Orden #</span>
            <span className="font-semibold text-lg" style={{ color: '#0095FF' }}>
              {orderNumber || 'POS-001'}
            </span>
          </div>

          {/* IVA y Total Box */}
          <div 
            className="border rounded-xl w-full max-w-md"
            style={{
              backgroundColor: '#F8F9FD',
              height: '94px',
              padding: '16px',
              borderRadius: '12px',
              gap: '8px',
              border: '1px solid #E5E7EB'
            }}
          >
            <div className="flex justify-between items-center h-full">
              <div className="space-y-2">
                <div className="flex justify-between items-center min-w-[200px]">
                  <span className="text-gray-600 text-sm">IVA (13%)</span>
                  <span className="font-medium">{formatCurrency(ivaAmount || 0)}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-gray-900 font-semibold">Monto total</span>
                  <span className="font-bold text-lg" style={{ color: '#0095FF' }}>
                    {formatCurrency(total || 0)}
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Tipo de documento dropdown */}
          <div className="space-y-2">
            <label className="block text-sm font-medium text-gray-700">
              Tipo de documento a emitir
            </label>
            {loadingDocumentTypes ? (
              <div className="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-100 flex items-center">
                <svg className="animate-spin -ml-1 mr-3 h-4 w-4 text-gray-400" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
                Cargando tipos de documento...
              </div>
            ) : (
              <select
                value={selectedDocumentType}
                onChange={(e) => setSelectedDocumentType(parseInt(e.target.value))}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                style={{ backgroundColor: '#F7FAFF' }}
                disabled={documentTypes.length === 0}
              >
                {documentTypes.map(type => (
                  <option key={type.id} value={type.id}>
                    {type.name}
                  </option>
                ))}
              </select>
            )}
          </div>

          {/* Checkbox métodos combinados */}
          <div className="flex items-center space-x-3">
            <input
              type="checkbox"
              id="combined-payment"
              checked={combinedPayment}
              onChange={(e) => setCombinedPayment(e.target.checked)}
              className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded focus:ring-blue-500 focus:ring-2"
            />
            <label htmlFor="combined-payment" className="text-sm text-gray-700">
              ¿Quieres combinar diferentes métodos de pago?
            </label>
          </div>

          {/* Condición de pago */}
          <div className="space-y-3">
            <label className="block text-sm font-medium text-gray-700">
              Condición de pago
            </label>
            <div className="flex flex-wrap gap-2">
              {loadingPaymentTerms ? (
                // Loading skeleton for payment terms
                Array.from({ length: 4 }).map((_, index) => (
                  <div 
                    key={index} 
                    className="px-4 py-2 rounded-lg bg-gray-200 animate-pulse"
                    style={{
                      border: '1.28px solid #ECEDEE',
                      boxShadow: '0px 5.1px 10.21px 0px #0000000A'
                    }}
                  >
                    <div className="h-4 w-16 bg-gray-300 rounded"></div>
                  </div>
                ))
              ) : (
                paymentTerms.map(term => (
                  <button
                    key={term.id}
                    onClick={() => setSelectedPaymentTerm(term.id)}
                    className={`px-4 py-2 rounded-lg transition-all duration-200 text-sm font-medium ${
                      selectedPaymentTerm === term.id
                        ? 'text-white'
                        : 'text-gray-700 hover:bg-gray-50'
                    }`}
                    style={{
                      backgroundColor: selectedPaymentTerm === term.id ? '#0095FF' : '#FFFFFF',
                      border: '1.28px solid #ECEDEE',
                      boxShadow: '0px 5.1px 10.21px 0px #0000000A'
                    }}
                  >
                    {term.displayText}
                    {term.paymentPeriod > 0 && (
                      <span className={`ml-1 text-xs ${
                        selectedPaymentTerm === term.id ? 'text-blue-100' : 'text-gray-500'
                      }`}>
                        ({term.paymentPeriod} días)
                      </span>
                    )}
                  </button>
                ))
              )}
            </div>
          </div>

          {/* Método de pago */}
          <div className="space-y-3">
            <label className="block text-sm font-medium text-gray-700">
              Método de pago
            </label>
            <div className="grid grid-cols-2 gap-3">
              {loadingPaymentForms ? (
                // Loading skeleton
                Array.from({ length: 4 }).map((_, index) => (
                  <div key={index} className="p-4 rounded-lg border-2 border-gray-200 bg-gray-50 animate-pulse">
                    <div className="h-8 w-8 bg-gray-300 rounded mx-auto mb-2"></div>
                    <div className="h-4 bg-gray-300 rounded w-20 mx-auto"></div>
                  </div>
                ))
              ) : (
                paymentMethods.map(method => (
                  <button
                    key={method.id}
                    onClick={() => setSelectedPaymentMethod(method.id)}
                    className={`p-4 rounded-lg border-2 transition-all duration-200 flex flex-col items-center gap-2 ${
                      selectedPaymentMethod === method.id 
                        ? 'border-blue-500' 
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                    style={{ backgroundColor: '#FFFFFF' }}
                  >
                    <span className="text-2xl">💳</span>
                    <span className={`text-sm font-medium ${
                      selectedPaymentMethod === method.id ? 'text-blue-700' : 'text-gray-700'
                    }`}>
                      {method.name}
                    </span>
                  </button>
                ))
              )}
            </div>
          </div>

          {/* Métodos de pago combinados (si está activado) */}
          {combinedPayment && (
            <div className="space-y-3 p-4 bg-gray-50 rounded-lg">
              <h4 className="text-sm font-medium text-gray-700">Distribución de pago</h4>
              <div className="space-y-2">
                {loadingPaymentForms ? (
                  <div className="text-sm text-gray-500">Cargando formas de pago...</div>
                ) : (
                  paymentMethods.map(method => (
                    <div key={method.id} className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span>💳</span>
                        <span className="text-sm">{method.name}</span>
                      </div>
                      <input
                        type="number"
                        placeholder="0.00"
                        className="w-20 px-2 py-1 text-sm border border-gray-300 rounded"
                        step="0.01"
                        min="0"
                      />
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        {/* Botones de acción inferiores */}
        <div className="px-6 py-6 bg-white border-t border-gray-100">
          <div className="grid grid-cols-3 gap-4">
            {bottomButtons.map(button => {
              const isDisabled = button.id === 'procesar' && (!selectedClient || !selectedSalePoint);
              
              return (
                <button
                  key={button.id}
                  onClick={() => handleBottomButtonClick(button.id)}
                  className="flex flex-col items-center justify-center font-medium transition-all duration-200 rounded-2xl border border-gray-200"
                  style={{
                    width: '150px',
                    height: '86px',
                    backgroundColor: activeBottomButton === button.id ? '#0095FF' : (isDisabled ? '#F5F5F5' : '#FFFFFF'),
                    color: activeBottomButton === button.id ? '#FFFFFF' : (isDisabled ? '#9CA3AF' : '#424242'),
                    borderRadius: '16px',
                    cursor: isDisabled ? 'not-allowed' : 'pointer',
                    opacity: isDisabled ? 0.6 : 1
                  }}
                  onMouseEnter={(e) => {
                    if (activeBottomButton !== button.id && !isDisabled) {
                      e.target.style.backgroundColor = '#F5F5F5';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (activeBottomButton !== button.id && !isDisabled) {
                      e.target.style.backgroundColor = '#FFFFFF';
                    }
                  }}
                >
                  <span className="text-2xl mb-1">{button.icon}</span>
                  <span className="text-xs text-center leading-tight px-2">
                    {button.label}
                  </span>
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Modal de confirmación de orden */}
      <OrderConfirmationModal
        isOpen={showOrderConfirmation}
        onClose={handleCloseOrderConfirmation}
        onConfirm={handleOrderConfirmation}
        cartItems={cartItems}
        orderNumber={orderNumber}
        subtotal={total / 1.13} // Subtotal sin IVA (precio original / 1.13)
        iva={total - (total / 1.13)} // IVA extraído (total - subtotal)
        total={total} // Total = precio original (NO sumar IVA encima)
        paymentMethods={getSelectedPaymentMethods()}
        paymentTerm={getSelectedPaymentTerm()}
        combinedPayment={combinedPayment}
        selectedClient={selectedClient}
        selectedSalePoint={selectedSalePoint}
        selectedDocumentType={selectedDocumentType}
        notes=""
      />

      {/* Toast para validación */}
      <Toast
        isOpen={showToast}
        onAccept={handleToastAccept}
        message="Selecciona un cliente o un punto de venta"
      />
    </>
  );
};

export default PaymentModal;