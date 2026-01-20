import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { COLORS } from "../constants/theme";
import { useAuth } from "../context/AuthContext";

// Iconos
import dashboardIcon from "../assets/icons/dashboard.png";
import ayudaicon from "../assets/icons/ayuda.png";
import posIcon from "../assets/icons/posicon.png";
import catalogosIcon from "../assets/icons/catalogosicon.png";
import logoDecima from "../assets/icons/logoDecima.png";
import configuracionIcon from "../assets/icons/configuracionicon.png";

const Sidebar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();
  const { user, isLoading } = useAuth();

  const menuItems = [
    { id: "dashboard", label: "Dashboard", icon: dashboardIcon, path: "/dashboard" },
    { id: "catalogo", label: "Catálogos", icon: catalogosIcon, path: "/catalogo" },
    { id: "pos", label: "POS", icon: posIcon, path: "/pos" },
    { id: "ayuda", label: "Ayuda", icon: ayudaicon, path: "/ayuda" },
    { id: "configuracion", label: "Configuración", icon: configuracionIcon, path: "/configuracion" },
  ];

  // Verificar permisos basado en rol
  const canAccess = (itemId) => {
    // Si está cargando o no hay usuario, permitir acceso a rutas públicas
    if (isLoading || !user?.role?.id) {
      // Durante la carga, permitir acceso a rutas básicas
      return ['dashboard', 'pos', 'ayuda'].includes(itemId);
    }
    
    const isAdmin = user.role.id === 1;
    const isVendedor = user.role.id === 2;
    
    if (isAdmin) return true; // Admin tiene acceso a todo
    
    if (isVendedor) {
      // Vendedor solo puede acceder a: dashboard, pos, ayuda
      return ['dashboard', 'pos', 'ayuda'].includes(itemId);
    }
    
    return false; // Default: bloquear acceso
  };

  const handleNavigation = (e, item) => {
    if (!canAccess(item.id)) {
      e.preventDefault();
      return false;
    }
    setIsOpen(false);
  };

  return (
    <>
      {/* Botón menú móvil - Solo visible cuando el menú está cerrado */}
      {!isOpen && (
        <button
          onClick={() => setIsOpen(!isOpen)}
          className="lg:hidden fixed top-4 left-4 z-50 p-3 rounded-lg bg-white shadow-md border border-gray-200 touch-manipulation"
          aria-label="Toggle menu"
          style={{ backgroundColor: COLORS.cardBackground }}
        >
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
      )}

      {/* Barra lateral */}
      <aside
        className={`fixed left-0 bg-white shadow-md flex flex-col py-6 border-r border-gray-100 z-30 transition-all duration-300 ease-in-out
          ${isOpen ? 'translate-x-0' : '-translate-x-full'} 
          lg:translate-x-0 lg:fixed lg:top-0
          ${isOpen ? 'w-48' : 'w-20'} lg:w-24 lg:ml-6 lg:my-6 lg:h-[calc(100vh-3rem)] lg:rounded-xl
          ${isOpen ? 'items-start px-4' : 'items-center'} lg:items-center
          ${isOpen ? 'top-24 h-[calc(100vh-6rem)]' : 'top-0 h-full'}`}
        aria-label="Sidebar"
        style={{ backgroundColor: COLORS.cardBackground }}
      >
        {/* Botón X para cerrar - Esquina superior derecha independiente */}
        {isOpen && (
          <button
            onClick={() => setIsOpen(false)}
            className="lg:hidden absolute top-4 right-4 p-1 rounded-lg hover:bg-gray-100 transition-colors shadow-sm border border-gray-200"
            style={{ backgroundColor: COLORS.cardBackground }}
            aria-label="Cerrar menú"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        )}

        {/* Logo */}
        <div className={`w-full flex mb-8 mt-12 ${isOpen ? 'justify-start' : 'justify-center'} lg:justify-center lg:mt-0`}>
          <div className="flex items-center">
            <img src={logoDecima} alt="Logo Décima" className="w-6 h-6 lg:w-8 lg:h-8" />
            {isOpen && <span className="ml-3 font-semibold text-lg lg:hidden">DÉCIMA</span>}
          </div>
        </div>

        {/* Navegación */}
        <nav className={`flex flex-col gap-3 w-full ${isOpen ? 'px-0' : 'px-3'} lg:px-3`}>
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            const hasAccess = canAccess(item.id);
            
            return (
              <Link
                key={item.id}
                to={hasAccess ? item.path : '#'}
                onClick={(e) => handleNavigation(e, item)}
                aria-pressed={isActive}
                className={`flex items-center rounded-xl transition-colors focus:outline-none active:outline-none touch-manipulation
                  ${isOpen ? 'justify-start px-3 py-3' : 'justify-center w-12 h-12'} lg:justify-center lg:w-14 lg:h-14
                  ${!hasAccess ? 'cursor-not-allowed opacity-50' : ''}
                  ${isActive ? '' : (hasAccess ? 'hover:bg-blue-50 active:bg-blue-100' : '')}`}
                style={{
                  backgroundColor: isActive && hasAccess ? COLORS.primary : 'transparent',
                  outline: 'none',
                  border: 'none',
                  pointerEvents: hasAccess ? 'auto' : 'none'
                }}
              >
                <img
                  src={item.icon}
                  alt={`${item.label} icon`}
                  className={`object-contain ${isOpen ? 'w-5 h-5' : 'w-4 h-4'} lg:w-5 lg:h-5`}
                  aria-hidden="true"
                  style={{
                    filter: isActive && hasAccess ? 'brightness(0) invert(1)' : 
                           !hasAccess ? 'grayscale(100%) opacity(0.5)' : 'none'
                  }}
                />
                {isOpen && (
                  <span 
                    className={`ml-3 font-medium text-sm lg:hidden ${!hasAccess ? 'text-gray-400' : ''}`}
                    style={{ 
                      color: isActive && hasAccess ? 'white' : 
                             !hasAccess ? '#9CA3AF' : COLORS.textPrimary 
                    }}
                  >
                    {item.label}
                  </span>
                )}
              </Link>
            );
          })}
        </nav>
      </aside>
    </>
  );
};

export default Sidebar;