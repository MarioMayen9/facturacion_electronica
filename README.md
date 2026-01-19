# Sistema POS - Estructura de Directorios

Este proyecto está organizado en 3 directorios principales:

##  Estructura del Proyecto

```
REPO GITHUB VE FE/
 front_end/              # Frontend React + Vite
    src/
    public/
    package.json
    vite.config.js

 backend/                 # Backend Spring Boot (Orquestador + Auth)
    src/
    pom.xml
    run-backend.bat

 svfe-api-firmador/       # Servicio Firmador
     src/
     pom.xml
     start-api.ps1
```

##  Cómo ejecutar

### Frontend (Puerto 5173/5174)
```bash
cd front_end
npm run dev
```

### Backend Orquestador (Puerto 8081)
```bash
cd backend
mvn spring-boot:run
# o ejecutar run-backend.bat
```

### Servicio Firmador (Puerto 8080)
```bash
cd svfe-api-firmador
./start-api.ps1
```

##  Credenciales de acceso
- **Email**: nuevouser@pos.com
- **Password**: password123

##  Base de datos
- **PostgreSQL**
- **Base de datos**: pyme
- **Usuario**: postgres
- **Password**: password


