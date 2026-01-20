import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { COLORS, STYLES } from "./constants/theme";
import { AuthProvider, useAuth } from "./context/AuthContext";
import Sidebar from "./components/Sidebar";
import Header from "./components/Header";
import ProtectedRoute from "./components/ProtectedRoute";
import Configuracion from "./pages/Configuracion";
import POS from "./pages/POS";
import Login from "./pages/Login";

// Páginas placeholder para desarrollo futuro
const Dashboard = () => (
  <div className="pt-28 lg:pt-32">
    <div className={`${STYLES.card} p-6 lg:p-8 max-w-none min-h-[400px] flex items-center justify-center`}>
      <div className="text-center text-gray-400">
        <div className="text-6xl mb-4">📊</div>
        <h2 className="text-xl font-semibold mb-2">Dashboard</h2>
        <p className="text-sm">Sección en Desarrollo</p>
      </div>
    </div>
  </div>
);

const Catalogo = () => (
  <div className="pt-28 lg:pt-32">
    <div className={`${STYLES.card} p-6 lg:p-8 max-w-none min-h-[400px] flex items-center justify-center`}>
      <div className="text-center text-gray-400">
        <div className="text-6xl mb-4">📋</div>
        <h2 className="text-xl font-semibold mb-2">Catálogo</h2>
        <p className="text-sm">Sección en Desarrollo</p>
      </div>
    </div>
  </div>
);

const Ayuda = () => (
  <div className="pt-28 lg:pt-32">
    <div className={`${STYLES.card} p-6 lg:p-8 max-w-none min-h-[400px] flex items-center justify-center`}>
      <div className="text-center text-gray-400">
        <div className="text-6xl mb-4">❓</div>
        <h2 className="text-xl font-semibold mb-2">Ayuda</h2>
        <p className="text-sm">Sección en Desarrollo</p>
      </div>
    </div>
  </div>
);

// Componente principal que maneja la lógica de autenticación
const AppContent = () => {
  const { isAuthenticated, user, isLoading, login, logout } = useAuth();

  // Mostrar pantalla de carga mientras verificamos la sesión
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center" style={{ backgroundColor: COLORS.background }}>
        <div className="text-center">
          <div className="w-20 h-20 mx-auto mb-4 rounded-2xl flex items-center justify-center shadow-lg animate-pulse" 
               style={{ backgroundColor: COLORS.primary }}>
            <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
          <p className="text-lg" style={{ color: COLORS.textSecondary }}>
            Cargando...
          </p>
        </div>
      </div>
    );
  }

  // Si no está autenticado, mostrar página de login
  if (!isAuthenticated) {
    return <Login />;
  }

  return (
    <BrowserRouter>
      <div className="min-h-screen" style={{ backgroundColor: COLORS.background }}>
        <Sidebar />
        <Header user={user} onLogout={logout} />
        
        {/* Área contenido principal con responsive margins */}
        <main className="lg:ml-28 lg:-mr-8 p-4 lg:p-6">
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route 
              path="/catalogo" 
              element={
                <ProtectedRoute allowedRoles={[1]}>
                  <Catalogo />
                </ProtectedRoute>
              } 
            />
            <Route path="/pos" element={<POS />} />
            <Route 
              path="/configuracion" 
              element={
                <ProtectedRoute allowedRoles={[1]}>
                  <Configuracion />
                </ProtectedRoute>
              } 
            />
            <Route path="/ayuda" element={<Ayuda />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
};

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
