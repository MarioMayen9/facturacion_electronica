// src/pages/Login.jsx
import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { COLORS, STYLES } from "../constants/theme";

const Login = () => {
  const { login } = useAuth();
  const [formData, setFormData] = useState({
    email: "",
    password: ""
  });
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Limpiar errores cuando el usuario empiece a escribir
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ""
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    
    // Validaciones
    const newErrors = {};
    
    if (!formData.email || !formData.email.trim()) {
      newErrors.email = "El email es requerido";
    } else {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(formData.email)) {
        newErrors.email = "Por favor, ingrese un email válido";
      }
    }
    
    if (!formData.password || !formData.password.trim()) {
      newErrors.password = "La contraseña es requerida";
    } else if (formData.password.length < 6) {
      newErrors.password = "La contraseña debe tener al menos 6 caracteres";
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setIsLoading(true);
    
    try {
      const result = await login(formData.email, formData.password);
      
      if (!result.success) {
        setErrors({ general: result.message });
      }
      // Si el login es exitoso, el AuthContext se encargará de redirigir
    } catch (error) {
      console.error("Error en el login:", error);
      setErrors({ general: "Error al conectar con el servidor. Por favor, inténtelo de nuevo." });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div 
      style={{ 
        backgroundColor: COLORS.background,
        position: 'fixed',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '12px'
      }}
    >
      <div style={{ 
        width: '100%', 
        maxWidth: '400px'
      }}>
        {/* Logo/Header */}
        <div className="text-center mb-6">
          <div className="w-16 h-16 mx-auto mb-3 rounded-2xl flex items-center justify-center shadow-lg" 
               style={{ backgroundColor: COLORS.primary }}>
            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                    d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
          </div>
          <h1 className="text-2xl font-bold mb-2" style={{ color: COLORS.black }}>
            Bienvenido
          </h1>
          <p className="text-base" style={{ color: COLORS.textSecondary }}>
            Ingrese sus credenciales para continuar
          </p>
        </div>

        {/* Formulario de Login */}
        <div className={STYLES.card}>
          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Error general */}
            {errors.general && (
              <div className="p-2 rounded-lg border border-red-200 bg-red-50">
                <p className="text-sm text-red-600">{errors.general}</p>
              </div>
            )}

            {/* Campo Email */}
            <div>
              <label className="block text-sm font-medium mb-1" style={{ color: COLORS.textPrimary }}>
                Correo Electrónico
              </label>
              <div className="relative">
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="usuario@ejemplo.com"
                  className={`${STYLES.input} pl-10 ${errors.email ? 'border-red-300 focus:border-red-500' : ''}`}
                  style={{ padding: '8px 12px 8px 40px', fontSize: '14px' }}
                  required
                  disabled={isLoading}
                />
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg className="h-4 w-4" style={{ color: COLORS.textMuted }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                          d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                  </svg>
                </div>
              </div>
              {errors.email && (
                <p className="mt-1 text-sm text-red-600">{errors.email}</p>
              )}
            </div>

            {/* Campo Password */}
            <div>
              <label className="block text-sm font-medium mb-1" style={{ color: COLORS.textPrimary }}>
                Contraseña
              </label>
              <div className="relative">
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  placeholder="••••••••"
                  className={`${STYLES.input} pl-10 pr-10 ${errors.password ? 'border-red-300 focus:border-red-500' : ''}`}
                  style={{ padding: '8px 40px 8px 40px', fontSize: '14px' }}
                  required
                  disabled={isLoading}
                />
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <svg className="h-4 w-4" style={{ color: COLORS.textMuted }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                          d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                  </svg>
                </div>
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 pr-3 flex items-center touch-manipulation"
                  disabled={isLoading}
                >
                  <svg className="h-4 w-4" style={{ color: COLORS.textMuted }} fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    {showPassword ? (
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                            d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
                    ) : (
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                            d="M15 12a3 3 0 11-6 0 3 3 0 016 0z M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                    )}
                  </svg>
                </button>
              </div>
              {errors.password && (
                <p className="mt-1 text-sm text-red-600">{errors.password}</p>
              )}
            </div>

            {/* Recordar contraseña */}
            <div className="flex items-center justify-between">
              <label className="flex items-center gap-2 cursor-pointer">
                <input
                  type="checkbox"
                  className="w-4 h-4 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                  style={{ accentColor: COLORS.primary }}
                  disabled={isLoading}
                />
                <span className="text-sm" style={{ color: COLORS.textSecondary }}>
                  Recordar contraseña
                </span>
              </label>
              <button
                type="button"
                className="text-sm font-medium hover:opacity-80 transition px-2 py-1 rounded"
                style={{ color: COLORS.primary, backgroundColor: COLORS.lightBlue }}
                disabled={isLoading}
              >
                ¿Olvidó su contraseña?
              </button>
            </div>

            {/* Botón de Submit */}
            <button
              type="submit"
              disabled={isLoading}
              className={`w-full text-white font-semibold flex items-center justify-center gap-2 ${
                isLoading ? 'opacity-70 cursor-not-allowed' : ''
              }`}
              style={{ 
                backgroundColor: COLORS.primary,
                padding: '10px 16px',
                borderRadius: '8px',
                border: 'none',
                fontSize: '14px',
                minHeight: '40px'
              }}
            >
              {isLoading ? (
                <>
                  <svg className="animate-spin h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" className="opacity-25"></circle>
                    <path fill="currentColor" className="opacity-75" 
                          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z">
                    </path>
                  </svg>
                  Iniciando sesión...
                </>
              ) : (
                <>
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} 
                          d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                  </svg>
                  Iniciar Sesión
                </>
              )}
            </button>
          </form>

          {/* Información adicional */}
          <div className="mt-5 pt-4 border-t border-gray-100">
            <p className="text-center text-sm" style={{ color: COLORS.textMuted }}>
              Sistema de Punto de Venta
            </p>
            <p className="text-center text-xs mt-1" style={{ color: COLORS.textMuted }}>
              © 2024 - Todos los derechos reservados
            </p>
          </div>
        </div>

        {/* Enlaces de ayuda */}
        <div className="text-center mt-4">
          <button
            type="button"
            className="text-sm font-medium hover:opacity-80 transition px-3 py-2 rounded-lg"
            style={{ color: COLORS.textSecondary, backgroundColor: COLORS.lightBlue }}
          >
            ¿Necesita ayuda? Contacte al administrador
          </button>
        </div>
      </div>
    </div>
  );
};

export default Login;