@echo off
echo 🚀 Iniciando API Firmador...
echo 📍 URL de prueba: http://localhost:8081/firma/firmardocumento/status

set JAVA_HOME=C:\Users\mario\.jdk\jdk-21.0.8
set PATH=C:\Users\mario\.jdk\jdk-21.0.8\bin;C:\Users\mario\.maven\maven-3.9.11\bin;%PATH%

cd /d "C:\Users\mario\Downloads\svfe-api-firmador\svfe-api-firmador"
mvn spring-boot:run

pause