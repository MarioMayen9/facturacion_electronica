// src/components/Header.jsx
import { useState } from "react";

const Header = () => {
  const [isProfileOpen, setIsProfileOpen] = useState(false);
  
  return (
    <header className="bg-white shadow-sm border-b border-gray-200 fixed top-0 left-0 right-0 z-30 lg:left-32 lg:ml-4 lg:mr-8 lg:mt-6 lg:rounded-xl">
      <div className="flex items-center justify-end gap-3 sm:gap-4 px-4 sm:px-6 lg:px-8 py-3 sm:py-4">
        {/* Search Icon */}
        <button 
          className="p-2 sm:p-3 rounded-lg hover:bg-gray-200 transition-colors touch-manipulation min-h-[44px] min-w-[44px]"
          style={{ backgroundColor: '#F8F9FA' }}
          aria-label="Buscar"
        >
          <svg className="w-5 h-5 sm:w-6 sm:h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </button>

        {/* Notification Icon */}
        <button 
          className="relative p-2 sm:p-3 rounded-lg hover:bg-gray-200 transition-colors touch-manipulation min-h-[44px] min-w-[44px]"
          style={{ backgroundColor: '#F8F9FA' }}
          aria-label="Notificaciones"
        >
          <svg className="w-5 h-5 sm:w-6 sm:h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          {/* Notification badge */}
          <span className="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full flex items-center justify-center">
            <span className="w-2 h-2 bg-red-500 rounded-full"></span>
          </span>
        </button>

        {/* User Profile */}
        <div className="relative">
          <button
            onClick={() => setIsProfileOpen(!isProfileOpen)}
            className="flex items-center gap-2 sm:gap-3 p-2 sm:p-3 rounded-lg hover:bg-gray-200 transition-colors touch-manipulation min-h-[44px]"
            style={{ backgroundColor: '#F8F9FA' }}
            aria-label="Perfil de usuario"
          >
            {/* Avatar */}
            <div className="w-8 h-8 sm:w-10 sm:h-10 bg-gray-300 rounded-full flex items-center justify-center">
              <span className="text-xs sm:text-sm font-medium text-gray-600">A</span>
            </div>
            
            {/* User name - Hidden on mobile */}
            <span className="hidden sm:block text-sm font-medium text-gray-700">
              Alvaro
            </span>
            
            {/* Dropdown arrow */}
            <svg 
              className={`w-4 h-4 text-gray-500 transition-transform ${isProfileOpen ? 'rotate-180' : ''}`} 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
          </button>

          {/* Dropdown Menu */}
          {isProfileOpen && (
            <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-50 touch-manipulation">
              <div className="py-2">
                <div className="px-4 py-2 border-b border-gray-100">
                  <p className="text-sm font-medium text-gray-900">Alvaro</p>
                  <p className="text-xs text-gray-500">alvaro@empresa.com</p>
                </div>
                
                <button className="w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-gray-100 transition-colors touch-manipulation">
                  Mi Perfil
                </button>
                
                <button className="w-full text-left px-4 py-3 text-sm text-gray-700 hover:bg-gray-100 transition-colors touch-manipulation">
                  Configuración
                </button>
                
                <hr className="my-1" />
                
                <button className="w-full text-left px-4 py-3 text-sm text-red-600 hover:bg-red-50 transition-colors touch-manipulation">
                  Cerrar Sesión
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;