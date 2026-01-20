// src/components/ProtectedRoute.jsx
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ children, requiredRole = null, allowedRoles = [] }) => {
  const { user, isAuthenticated, isLoading } = useAuth();

  // Mientras carga, permitir acceso temporalmente
  if (isLoading) {
    return children;
  }

  // Si no está autenticado, redirigir al login
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Si no hay usuario o rol, bloquear acceso
  if (!user?.role?.id) {
    return <Navigate to="/dashboard" replace />;
  }

  const userRoleId = user.role.id;

  // Verificar si tiene el rol específico requerido
  if (requiredRole && userRoleId !== requiredRole) {
    return <Navigate to="/dashboard" replace />;
  }

  // Verificar si está en la lista de roles permitidos
  if (allowedRoles.length > 0 && !allowedRoles.includes(userRoleId)) {
    return <Navigate to="/dashboard" replace />;
  }

  // Si pasa todas las validaciones, mostrar el contenido
  return children;
};

export default ProtectedRoute;