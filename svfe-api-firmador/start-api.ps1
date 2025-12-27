# Script simplificado para iniciar la API Firmador
# Configuración de variables de entorno (temporal para esta sesión)
$env:JAVA_HOME = "C:\Users\mario\.jdk\jdk-21.0.8"
$env:PATH = "C:\Users\mario\.jdk\jdk-21.0.8\bin;C:\Users\mario\.maven\maven-3.9.11\bin;$env:PATH"

Write-Host "🚀 Iniciando API Firmador..." -ForegroundColor Green
Write-Host "� URL de prueba: http://localhost:8081/firma/firmardocumento/status" -ForegroundColor Yellow
Write-Host ""

# Cambiar al directorio del proyecto
Set-Location "C:\Users\mario\Downloads\svfe-api-firmador\svfe-api-firmador"

# Iniciar la aplicación
mvn spring-boot:run