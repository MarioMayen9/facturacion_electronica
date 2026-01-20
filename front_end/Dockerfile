# Multi-stage build para optimizar tamaño de imagen
# Stage 1: Build de la aplicación React
FROM node:22-alpine AS builder
# Establecer directorio de trabajo
WORKDIR /app
# Copiar archivos de configuración de dependencias
COPY package.json package-lock.json ./
# Instalar dependencias (incluyendo devDependencies para el build)
RUN npm ci --silent
# Copiar código fuente
COPY . .
# Construir la aplicación para producción
RUN npm run build
# Stage 2: Servidor de producción con nginx
FROM nginx:alpine
# Eliminar configuración por defecto de nginx
RUN rm -rf /usr/share/nginx/html/*
# Copiar archivos construidos desde el stage anterior
COPY --from=builder /app/dist /usr/share/nginx/html
# Copiar configuración personalizada de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Exponer puerto 80
EXPOSE 80
# Comando por defecto para iniciar nginx
CMD ["nginx", "-g", "daemon off;"]