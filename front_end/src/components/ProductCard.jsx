import React from 'react';
import { COLORS } from '../constants/theme';

const ProductCard = ({ product, onAddToCart }) => {
  const handleAddToCart = () => {
    onAddToCart(product);
  };

  // Color placeholder dinámico basado en ID
  const getPlaceholderColor = (id) => {
    const colors = [
      'bg-blue-100', 'bg-green-100', 'bg-yellow-100', 
      'bg-purple-100', 'bg-pink-100', 'bg-indigo-100'
    ];
    return colors[id % colors.length];
  };

  return (
    <div className="rounded-lg p-4 hover:shadow-md hover:scale-105 transition-all cursor-pointer relative"
         style={{ backgroundColor: '#FFFFFF' }}
         onClick={handleAddToCart}>
      {/* Container imagen producto */}
      <div className="aspect-square mb-3 bg-gray-50 rounded-lg flex items-center justify-center overflow-hidden relative">
        {product.image ? (
          <img 
            src={product.image} 
            alt={product.name}
            className="w-full h-full object-cover"
            onError={(e) => {
              // Fallback a placeholder si imagen falla
              e.target.style.display = 'none';
              e.target.nextSibling.style.display = 'flex';
            }}
          />
        ) : null}
        
        {/* Placeholder fallback imagen */}
        <div className={`w-full h-full ${getPlaceholderColor(product.id)} rounded-lg flex flex-col items-center justify-center ${
          product.image ? 'hidden' : 'flex'
        }`}>
          <svg className="w-8 h-8 text-gray-400 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                  d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
          <span className="text-xs text-gray-500 text-center px-2">
            {product.category}
          </span>
        </div>

        {/* Badge stock bajo - ELIMINADO */}
      </div>

      {/* Info producto */}
      <div className="space-y-2">
        <h3 className="font-medium text-gray-900 text-sm leading-tight line-clamp-2 min-h-[2.5rem]">
          {product.name}
        </h3>
        
        {/* SKU/Referencia interna */}
        {product.sku && (
          <div className="text-xs text-gray-500">
            SKU: {product.sku}
          </div>
        )}
        
        {/* Brand */}
        {product.brand && product.brand !== 'Sin marca' && (
          <div className="text-xs text-blue-600 font-medium">
            {product.brand}
          </div>
        )}
        
        {/* Stock information */}
        <div className="flex items-center justify-between text-xs">
          <span className={`${product.stock > 0 ? 'text-green-600' : 'text-red-500'}`}>
            Stock: {product.stock}
            {product.unit_symbol ? ` ${product.unit_symbol}` : ''}
          </span>
          {product.category && (
            <span className="text-gray-500">
              {product.category}
            </span>
          )}
        </div>
        
        <div className="flex justify-center">
          <div className="text-center">
            <span className="text-lg font-semibold" style={{ color: COLORS.primary }}>
              ${product.price?.toFixed(2)}
            </span>
            {product.offer_price > 0 && product.offer_price !== product.price && (
              <div>
                <span className="text-xs text-gray-500 line-through">
                  ${product.retail_price?.toFixed(2)}
                </span>
                <span className="text-xs text-red-500 ml-1">
                  ({product.discount}% desc.)
                </span>
              </div>
            )}
          </div>
        </div>

        {/* Badge adicionales */}
        <div className="flex flex-wrap gap-1 mt-2">
          {product.is_scannable && (
            <span className="text-xs bg-green-100 text-green-800 px-2 py-1 rounded">
              Escaneable
            </span>
          )}
          {product.guarantee && (
            <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
              Garantía: {product.guarantee}
            </span>
          )}
          {!product.is_active && (
            <span className="text-xs bg-red-100 text-red-800 px-2 py-1 rounded">
              Inactivo
            </span>
          )}
        </div>

        {/* Botón agregar hover - ELIMINADO */}
      </div>

      {/* Overlay agotado - ELIMINADO */}
    </div>
  );
};

export default ProductCard;