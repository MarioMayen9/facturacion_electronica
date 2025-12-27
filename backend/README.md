# Backend POS - Autenticación con PostgreSQL

Backend Spring Boot para sistema POS con autenticación JWT y PostgreSQL.

## 🔧 Configuración

### Base de datos PostgreSQL
```properties
Database: pyme
Host: localhost:5432
Usuario: postgres
Password: password
```

### Puerto del backend
```
http://localhost:8081
```

## 🚀 Ejecutar

```bash
cd backend
mvn spring-boot:run
```

## 🔐 Endpoints de Autenticación

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "correo": "mario.mayen.castro@gmail.com",
  "password": "admin123"
}
```

### Validar Token
```http
POST /api/auth/validate-token
Authorization: Bearer <token>
```

### Información del Usuario
```http
GET /api/auth/me
Authorization: Bearer <token>
```

## 📦 Dependencias principales

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL Driver
- JWT (jsonwebtoken)
- BCrypt (password hashing)

## ✅ Estado

- ✅ PostgreSQL configurado
- ✅ H2 removido completamente
- ✅ JWT implementado
- ✅ CORS configurado para frontend
- ✅ Usuario admin creado automáticamente