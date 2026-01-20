// src/components/Toast.jsx
import React from 'react';

const Toast = ({ isOpen, onAccept, message }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center" style={{ backgroundColor: 'rgba(0, 0, 0, 0.5)', zIndex: 1000 }}>
      <div 
        className="bg-white rounded-lg shadow-xl p-6"
        style={{
          width: '400px',
          maxWidth: '90vw'
        }}
      >
        {/* Mensaje */}
        <div className="mb-6">
          <p className="text-center text-gray-800 text-base font-medium">
            {message}
          </p>
        </div>

        {/* Botón Aceptar */}
        <div className="flex justify-center">
          <button
            onClick={onAccept}
            className="px-8 py-3 rounded-lg text-white font-medium transition-all duration-200 hover:opacity-90"
            style={{
              backgroundColor: '#0095FF',
              minWidth: '120px'
            }}
            onMouseEnter={(e) => {
              e.target.style.backgroundColor = '#007ACC';
            }}
            onMouseLeave={(e) => {
              e.target.style.backgroundColor = '#0095FF';
            }}
          >
            Aceptar
          </button>
        </div>
      </div>
    </div>
  );
};

export default Toast;