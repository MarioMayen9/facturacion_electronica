// src/components/Modal.jsx
import React from 'react';

const Modal = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-lg p-4 sm:p-6 w-full max-w-sm sm:max-w-md mx-4 relative max-h-screen overflow-y-auto border border-gray-100">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-base sm:text-lg font-semibold" style={{ color: '#0095FF' }}>
            {title}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 text-xl font-bold touch-manipulation p-2 rounded-lg hover:bg-gray-100 transition-colors"
            aria-label="Cerrar modal"
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