# 🏪 Sistema POS - Backend Spring Boot

Backend del sistema de punto de venta con facturación electrónica desarrollado en Spring Boot.

## 📋 Características

- **Framework**: Spring Boot 3.2.0
- **Java**: OpenJDK 17
- **Base de Datos**: PostgreSQL
- **Autenticación**: JWT
- **Dockerizado**: Listo para despliegue en contenedores

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
Desarrollo: http://localhost:8081
Producción: http://104.248.215.77:8080
```

## 🚀 Ejecución

### Desarrollo Local
```bash
cd backend
mvn spring-boot:run
```

### Con Docker
```bash
# Construir imagen
docker build -t mcmp2022/pymes:backend-latest .

# Ejecutar contenedor
docker run -d \
  --name pos-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  mcmp2022/pymes:backend-latest
```

### Con Docker Compose
```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f backend
```

## 🐳 Despliegue en Producción

### 1. Construcción y Push
```powershell
# Ejecutar script automatizado
.\build-and-push.ps1
```

### 2. Despliegue en Servidor (104.248.215.77)
```bash
# Conectarse al servidor
ssh root@104.248.215.77

# Descargar y ejecutar imagen
docker pull mcmp2022/pymes:backend-latest
docker run -d \
  --name pos-backend \
  --restart=unless-stopped \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC" \
  mcmp2022/pymes:backend-latest
```

### 3. Verificación
```bash
# Health Check
curl http://104.248.215.77:8080/actuator/health

# Ver estado del contenedor
docker ps -f name=pos-backend
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