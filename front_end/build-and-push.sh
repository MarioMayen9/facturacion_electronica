#!/bin/bash

# Script para construir y subir imagen Docker del frontend a DockerHub
# Asegurate de estar logueado en Docker: docker login

# Variables
IMAGE_NAME="pymes"
DOCKER_USERNAME="mcmp2022"
VERSION="frontend-latest"
FULL_IMAGE_NAME="$DOCKER_USERNAME/$IMAGE_NAME:$VERSION"

echo "🔨 Construyendo imagen Docker..."
docker build -t $FULL_IMAGE_NAME .

if [ $? -eq 0 ]; then
    echo "✅ Imagen construida exitosamente"
    
    echo "🚀 Subiendo imagen a DockerHub..."
    docker push $FULL_IMAGE_NAME
    
    if [ $? -eq 0 ]; then
        echo "✅ Imagen subida exitosamente a DockerHub"
        echo "📦 Imagen disponible en: docker pull $FULL_IMAGE_NAME"
        echo "🌐 Para ejecutar: docker run -p 80:80 $FULL_IMAGE_NAME"
    else
        echo "❌ Error al subir imagen a DockerHub"
        exit 1
    fi
else
    echo "❌ Error al construir imagen"
    exit 1
fi