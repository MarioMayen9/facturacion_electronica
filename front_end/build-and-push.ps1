# Script PowerShell para construir y subir imagen Docker del frontend a DockerHub
# Asegurate de estar logueado en Docker: docker login

# Variables
$IMAGE_NAME = "pymes"
$DOCKER_USERNAME = "mcmp2022"
$VERSION = "frontend-latest"
$FULL_IMAGE_NAME = "$DOCKER_USERNAME/$IMAGE_NAME:$VERSION"

Write-Host "🔨 Construyendo imagen Docker..." -ForegroundColor Yellow
docker build -t $FULL_IMAGE_NAME .

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Imagen construida exitosamente" -ForegroundColor Green
    
    Write-Host "🚀 Subiendo imagen a DockerHub..." -ForegroundColor Yellow
    docker push $FULL_IMAGE_NAME
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Imagen subida exitosamente a DockerHub" -ForegroundColor Green
        Write-Host "📦 Imagen disponible en: docker pull $FULL_IMAGE_NAME" -ForegroundColor Cyan
        Write-Host "🌐 Para ejecutar: docker run -p 80:80 $FULL_IMAGE_NAME" -ForegroundColor Cyan
    } else {
        Write-Host "❌ Error al subir imagen a DockerHub" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ Error al construir imagen" -ForegroundColor Red
    exit 1
}