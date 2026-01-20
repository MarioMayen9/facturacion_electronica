import React from 'react';

const MenuCalculadora = ({ 
  cartItems = [], 
  onUpdateQuantity, 
  onRemoveItem, 
  onClearCart,
  onProcessSale,
  customerInfo,
  onCustomerInfoChange,
  selectedClient,
  onSelectClientClick 
}) => {
  
  // Cálculo correcto - precios YA incluyen IVA (como imagen 3)
  const total = cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0); // Precio original
  const subtotal = total / 1.13; // Subtotal sin IVA  
  const iva = total - subtotal; // IVA extraído del precio original
  // TOTAL = precio original (NO sumar IVA encima)

  return (
    <div className="min-h-screen flex flex-col"
         style={{
           backgroundColor: '#FFFFFF',
           width: '339px',
           height: '1057px',
           borderRadius: '24px',
           opacity: 1
         }}>
      {/* Header sección cliente */}
      <div className="px-6 py-5 border-b border-gray-100">
        <div className="flex items-center justify-between mb-5">
          <h3 className="text-lg font-semibold text-gray-900">
            Información del cliente
          </h3>
         
        </div>

        {/* Cliente seleccionado o botón agregar cliente */}
        {selectedClient ? (
          /* Mostrar cliente seleccionado */
          <div className="flex items-center gap-3 p-3 rounded-lg border border-gray-200" style={{ backgroundColor: '#ffffffff' }}>
            {/* Avatar usuario */}
            <div className="w-12 h-12 rounded-full bg-gradient-to-br from-blue-400 to-purple-600 flex items-center justify-center flex-shrink-0">
              <span className="text-white font-semibold text-sm">
                {selectedClient.name ? selectedClient.name.split(' ').map(word => word[0]).join('').substring(0, 2).toUpperCase() : '?'}
              </span>
            </div>
            
            {/* Información del cliente */}
            <div className="flex-1 min-w-0">
              <h4 className="font-medium text-sm truncate" style={{ color: '#0095FF' }}>
                {selectedClient.name}
              </h4>
              {selectedClient.email && (
                <p className="text-xs truncate" style={{ color: '#424242' }}>
                  {selectedClient.email}
                </p>
              )}
              {selectedClient.phone && (
                <p className="text-xs truncate" style={{ color: '#424242' }}>
                  {selectedClient.phone}
                </p>
              )}
              {selectedClient.city && (
                <p className="text-xs truncate" style={{ color: '#424242' }}>
                  {selectedClient.city}{selectedClient.state ? `, ${selectedClient.state}` : ''}
                </p>
              )}
            </div>
            
            {/* Botón cambiar cliente */}
            <button 
              onClick={onSelectClientClick}
              className="rounded-full flex items-center justify-center flex-shrink-0 transition-colors hover:opacity-80 focus:outline-none"
              style={{ 
                backgroundColor: '#E4E4E4',
                width: '32px',
                height: '32px',
                minWidth: '32px',
                minHeight: '32px',
                borderRadius: '50%',
                border: 'none',
                outline: 'none'
              }}>
              <span className="text-gray-600 font-medium">✎</span>
            </button>
          </div>
        ) : (
          /* Botón agregar cliente */
          <button 
            onClick={onSelectClientClick}
            className="w-full flex items-center justify-center gap-3 p-4 rounded-lg border border-gray-200 hover:border-blue-400 hover:bg-blue-50 transition-all"
            style={{ backgroundColor: '#FFFFFF' }}
          >
            <div className="w-8 h-8 rounded-full bg-gray-100 flex items-center justify-center">
              <svg className="w-4 h-4 text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
              </svg>
            </div>
            <span className="text-gray-700 font-medium">Agregar cliente</span>
          </button>
        )}

        {/* Botón agregar nota */}
        <button className="mt-4 text-sm font-medium flex items-center gap-2 px-3 py-2 rounded-lg transition-colors"
                style={{ 
                  color: '#0095FF',
                  backgroundColor: '#1890FF17'
                }}>
          <span className="text-lg">+</span>
          Agregar nota
        </button>
      </div>

      {/* Sección orden/carrito */}
      <div className="px-6 py-5 border-b border-gray-100">
        <div className="flex items-center justify-between mb-5">
          <h3 className="text-lg font-semibold text-gray-900">
            Detalle de la orden
          </h3>
          <span className="text-sm font-medium" style={{ color: '#0095FF' }}>
            ver más
          </span>
        </div>

        {/* Items carrito con scroll */}
        <div className="space-y-0 max-h-80 overflow-y-auto">
          {cartItems.length === 0 ? (
            <p className="text-gray-500 text-sm text-center py-8">
              No hay productos en el carrito
            </p>
          ) : (
            cartItems.map((item, index) => (
              <div key={item.id}>
                <div className="flex items-start gap-3 py-4">
                  {/* Imagen producto */}
                  <div className="w-12 h-12 bg-gray-100 rounded-lg flex items-center justify-center overflow-hidden flex-shrink-0">
                    {item.image ? (
                      <img src={item.image} alt={item.name} className="w-full h-full object-cover" />
                    ) : (
                      <div className="w-6 h-6 bg-gray-300 rounded"></div>
                    )}
                  </div>

                  {/* Info producto */}
                  <div className="flex-1 min-w-0">
                    <h4 className="font-medium text-gray-900 text-sm leading-tight mb-1">
                      {item.name}
                    </h4>
                    <p className="text-sm font-semibold" style={{ color: '#0095FF' }}>
                      ${item.price?.toFixed(2)}
                    </p>
                  </div>

                  {/* Controles cantidad */}
                  <div className="flex items-center gap-0 flex-shrink-0">
                    <button
                      onClick={() => onUpdateQuantity?.(item.id, Math.max(0, item.quantity - 1))}
                      className="flex items-center justify-center text-gray-700 hover:bg-gray-300 transition-colors"
                      style={{ 
                        backgroundColor: '#E4E4E4',
                        borderRadius: '50%',
                        width: '32px',
                        height: '32px',
                        border: 'none',
                        outline: 'none'
                      }}
                    >
                      <span className="text-base font-bold leading-none">−</span>
                    </button>
                    
                    <span className="w-10 text-center text-sm font-medium text-gray-900">
                      {item.quantity}
                    </span>
                    
                    <button
                      onClick={() => onUpdateQuantity?.(item.id, item.quantity + 1)}
                      className="flex items-center justify-center text-white hover:opacity-90 transition-opacity"
                      style={{ 
                        backgroundColor: '#0095FF',
                        borderRadius: '50%',
                        width: '32px',
                        height: '32px',
                        border: 'none',
                        outline: 'none'
                      }}
                    >
                      <span className="text-base font-bold leading-none">+</span>
                    </button>
                  </div>
                </div>
                
                {/* Línea separadora */}
                {index < cartItems.length - 1 && (
                  <div className="h-px" style={{ backgroundColor: '#E4E4E4' }}></div>
                )}
              </div>
            ))
          )}
        </div>
      </div>

      {/* Resumen totales */}
      <div className="px-6 py-5 border-b border-gray-100">
        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">Sub Total:</span>
            <span className="text-sm font-semibold text-gray-900">${subtotal.toFixed(2)}</span>
          </div>
          
          <div className="flex justify-between items-center">
            <span className="text-sm text-gray-600">IVA (13%):</span>
            <span className="text-sm font-semibold text-gray-900">${iva.toFixed(2)}</span>
          </div>
          
          <div className="flex justify-between items-center pt-3 border-t border-gray-200">
            <span className="text-lg font-semibold text-gray-900">Total:</span>
            <span className="text-lg font-bold" style={{ color: '#0095FF' }}>
              ${total.toFixed(2)}
            </span>
          </div>
        </div>
      </div>

      {/* Botón checkout */}
      <div className="px-6 py-6 mt-auto">
        <button
          onClick={onProcessSale}
          disabled={cartItems.length === 0}
          className="w-full py-4 px-6 rounded-lg font-semibold text-white text-base transition-all disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-lg"
          style={{ 
            backgroundColor: cartItems.length > 0 ? '#0095FF' : '#9CA3AF' 
          }}
        >
          Pagar ahora
        </button>
      </div>
    </div>
  );
};

export default MenuCalculadora;