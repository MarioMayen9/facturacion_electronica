-- Script de inicialización de datos para la base de datos
-- Insertar roles básicos

INSERT INTO role (id, name, description, created_at, updated_at) VALUES 
(1, 'ADMIN', 'Administrador del sistema', NOW(), NOW()),
(2, 'VENDEDOR', 'Vendedor del sistema', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Crear usuario administrador por defecto
INSERT INTO usuario (id, username, email, password, active, role_id, created_at, updated_at) VALUES 
(1, 'admin', 'admin@pymes.com', '$2a$10$N.RHo9JZT5WhKjF6S6nfC.Y.Qw4H1a3qZ1z2Z1o2Z1z2Z1z2Z1z2Z1', true, 1, NOW(), NOW()),
(2, 'vendedor', 'vendedor@pymes.com', '$2a$10$N.RHo9JZT5WhKjF6S6nfC.Y.Qw4H1a3qZ1z2Z1o2Z1z2Z1z2Z1z2Z1', true, 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Crear organización por defecto
INSERT INTO organization (id, name, description, active, created_at, updated_at) VALUES 
(1, 'PYMES Demo', 'Organización de demostración', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;