# Script de despliegue del backend en el servidor de producción
# IP del servidor: 104.248.215.77

# Variables
$SERVER_IP = "104.248.215.77"
$IMAGE_NAME = "mcmp2022/pymes:backend-latest"
$CONTAINER_NAME = "pos-backend"
$PORT = "8080"

Write-Host "🚀 Desplegando Backend Spring Boot en el servidor de producción..." -ForegroundColor Yellow
Write-Host "📍 Servidor: $SERVER_IP" -ForegroundColor Cyan

# Comandos que se ejecutarán en el servidor remoto
$DEPLOY_COMMANDS = @"
# Detener y remover contenedor existente si existe
docker stop $CONTAINER_NAME 2>/dev/null
docker rm $CONTAINER_NAME 2>/dev/null

# Limpiar imagen anterior
docker rmi $IMAGE_NAME 2>/dev/null

# Descargar imagen actualizada
docker pull $IMAGE_NAME

# Ejecutar nuevo contenedor
docker run -d \
  --name $CONTAINER_NAME \
  --restart=unless-stopped \
  -p $PORT`:$PORT \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC" \
  -e TZ=America/El_Salvador \
  $IMAGE_NAME

# Verificar que el contenedor esté ejecutándose
sleep 10
docker ps -f name=$CONTAINER_NAME

# Verificar logs
docker logs $CONTAINER_NAME --tail 20
"@

Write-Host "📜 Comandos a ejecutar en el servidor:" -ForegroundColor Green
Write-Host $DEPLOY_COMMANDS -ForegroundColor White

Write-Host "`n📋 Para conectarse al servidor ejecute:" -ForegroundColor Yellow
Write-Host "ssh root@$SERVER_IP" -ForegroundColor Cyan

Write-Host "`n🌐 Una vez desplegado, la API estará disponible en:" -ForegroundColor Yellow
Write-Host "http://$SERVER_IP`:$PORT" -ForegroundColor Cyan
Write-Host "http://$SERVER_IP`:$PORT/actuator/health" -ForegroundColor Cyan

Write-Host "`n⚠️ NOTA: Recuerde configurar las variables de entorno para la base de datos" -ForegroundColor Yellow