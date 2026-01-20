// src/components/Modal.jsx
import React from 'react';

const Modal = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;

  return (
    <div 
      className="fixed inset-0 flex items-center justify-center z-50 p-4 backdrop-blur-sm"
      style={{ backgroundColor: '#42424259' }}
    >
      <div 
        className="rounded-xl shadow-lg p-4 sm:p-6 w-full max-w-sm sm:max-w-md mx-4 relative max-h-screen overflow-y-auto border border-gray-100"
        style={{ backgroundColor: '#FFFFFF' }}
      >
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-base sm:text-lg font-semibold" style={{ color: '#0095FF' }}>
            {title}
          </h2>
          <button
            onClick={onClose}
            className="text-xl font-bold touch-manipulation p-2 rounded-lg transition-colors"
            style={{ 
              color: '#42424280',
              backgroundColor: 'transparent'
            }}
            aria-label="Cerrar modal"
            onMouseEnter={(e) => {
              e.target.style.color = '#424242';
              e.target.style.backgroundColor = '#F5F5F5';
            }}
            onMouseLeave={(e) => {
              e.target.style.color = '#42424280';
              e.target.style.backgroundColor = 'transparent';
            }}
          >
            ×
          </button>
        </div>
        {children}
      </div>
    </div>
  );
};

export default Modal;