# Comandos Docker para el Sistema POS - Frontend

## 🔧 Configuración actualizada para mcmp2022/pymes

### Variables configuradas:
- Usuario DockerHub: mcmp2022
- Repositorio: pymes
- Tag Frontend: frontend-latest

## 🚀 Comandos para ejecutar:

### 1. Login a DockerHub
```bash
docker login
# Usar credenciales de mcmp2022
```

### 2. Construir imagen (Opción A - Script automatizado)
```powershell
# En PowerShell desde front_end/:
.\build-and-push.ps1
```

### 3. Construir imagen (Opción B - Manual)
```bash
# Construir imagen
docker build -t mcmp2022/pymes:frontend-latest .

# Subir a DockerHub
docker push mcmp2022/pymes:frontend-latest
```

### 4. Verificar imagen en DockerHub
```bash
# La imagen estará disponible en:
# https://hub.docker.com/r/mcmp2022/pymes

# Para descargar:
docker pull mcmp2022/pymes:frontend-latest
```

### 5. Ejecutar imagen localmente (prueba)
```bash
# Ejecutar contenedor local
docker run -d -p 80:80 --name pos-frontend mcmp2022/pymes:frontend-latest

# Verificar en: http://localhost
```

### 6. Para uso en servidor Linux (Digital Ocean)
```bash
# En el droplet:
docker pull mcmp2022/pymes:frontend-latest
docker run -d -p 80:80 --name pos-frontend mcmp2022/pymes:frontend-latest

# La aplicación estará en: http://tu-droplet-ip
```

## 📦 Próximos pasos para backend:
- Backend Node.js: mcmp2022/pymes:backend-latest  
- Spring Boot: mcmp2022/pymes:firmador-latest
- Todo en el mismo repositorio con tags diferentes