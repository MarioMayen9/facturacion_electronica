// src/components/Sidebar.jsx
import { useState } from "react";
import dashboardIcon from "../assets/icons/dashboard.png";
import ayudaicon from "../assets/icons/ayuda.png";
import posIcon from "../assets/icons/posicon.png";
import catalogosIcon from "../assets/icons/catalogosicon.png";
import logoDecima from "../assets/icons/logoDecima.png";





const Sidebar = () => {
  const [active, setActive] = useState("configuracion");
  const [isOpen, setIsOpen] = useState(false);

  const menuItems = [
    { id: "dashboard", label: "Dashboard", icon: dashboardIcon  },
    { id: "catalogo", label: "Catalogo", icon: catalogosIcon },
    { id: "pos", label: "POS", icon: posIcon },
    { id: "ayuda", label: "Ayuda", icon: ayudaicon},
    { id: "configuracion", label: "Configuración", icon: ayudaicon },
  ];

  return (
    <>
      {/* Mobile menu button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="lg:hidden fixed top-4 left-4 z-50 p-3 rounded-lg bg-white shadow-md border border-gray-200 touch-manipulation"
        aria-label="Toggle menu"
      >
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>

      {/* Backdrop for mobile */}
      {isOpen && (
        <div 
          className="lg:hidden fixed inset-0 bg-black bg-opacity-50 z-40"
          onClick={() => setIsOpen(false)}
        />
      )}      {/* Sidebar */}
      <aside
        className={`fixed left-0 top-0 h-full bg-white shadow-md flex flex-col items-center py-6 border-r border-gray-100 z-30 transition-transform duration-300 ease-in-out
          ${isOpen ? 'translate-x-0' : '-translate-x-full'} 
          lg:translate-x-0 lg:fixed
          w-20 lg:w-24 lg:ml-6 lg:my-6 lg:h-[calc(100vh-3rem)] lg:rounded-xl`}
        aria-label="Sidebar"
        style={{ backgroundColor: '#FFFFFF' }}
      >
        <div className="w-full flex justify-center mb-8">
          <img src={logoDecima} alt="Logo Décima" className="w-8 h-8 lg:w-10 lg:h-10" />
        </div>

        <nav className="flex flex-col gap-3 w-full px-3">
          {menuItems.map((item) => {
            const isActive = active === item.id;
            return (
              <button
                key={item.id}
                onClick={() => {
                  setActive(item.id);
                  setIsOpen(false); // Close mobile menu when item is selected
                }}
                aria-pressed={isActive}
                className={`flex items-center justify-center rounded-xl transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-300 touch-manipulation
                  w-12 h-12 lg:w-14 lg:h-14
                  ${isActive ? '' : 'hover:bg-blue-50 active:bg-blue-100'}`}
                style={{
                  backgroundColor: isActive ? '#0095FF' : 'transparent'
                }}
              >
                <img
                  src={item.icon}
                  alt={`${item.label} icon`}
                  className="w-7 h-7 lg:w-9 lg:h-9 object-contain"
                  aria-hidden="true"
                />
              </button>
            );
          })}
        </nav>
      </aside>
    </>
  );
};

export default Sidebar;