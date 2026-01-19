# Multi-stage build para optimizar imagen Spring Boot
# Stage 1: Build de la aplicación con Maven
FROM eclipse-temurin:17-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven curl

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests

# Stage 2: Imagen de producción
FROM eclipse-temurin:17-jre-alpine

# Instalar curl para healthcheck
RUN apk add --no-cache curl

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Crear directorio de aplicación
WORKDIR /app

# Copiar JAR desde stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Cambiar propietario del archivo
RUN chown spring:spring app.jar

# Cambiar a usuario no-root
USER spring

# Exponer puerto de la aplicación
EXPOSE 8080

# Variables de entorno para producción
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Health check optimizado para producción
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]