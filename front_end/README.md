# Sistema de Facturación Electrónica

Sistema web moderno de facturación electrónica desarrollado con React 19, Vite y Tailwind CSS.

## 🚀 Tecnologías

- **React 19** - Biblioteca de interfaz de usuario
- **React Router DOM 6.28** - Enrutamiento SPA
- **Vite 7.1** - Herramienta de desarrollo rápida
- **Tailwind CSS 4.1** - Framework CSS utility-first
- **JavaScript ES6+** - Sintaxis moderna

## 📁 Estructura del Proyecto

```
src/
├── components/          # Componentes reutilizables
│   ├── Header.jsx      # Cabecera de la aplicación
│   ├── Sidebar.jsx     # Barra lateral de navegación
│   ├── Table.jsx       # Componente de tabla
│   └── Modal.jsx       # Componente modal
├── pages/              # Páginas principales
│   ├── Configuracion.jsx  # Gestión de configuraciones
│   └── POS.jsx            # Punto de venta
├── constants/          # Constantes globales
│   └── theme.js        # Colores y estilos del tema
├── assets/             # Recursos estáticos
│   ├── icons/          # Iconos de navegación
│   └── img/            # Imágenes
└── App.jsx             # Componente principal
```

## 🎨 Sistema de Diseño

### Paleta de Colores
- **Primario**: `#0095FF` - Azul principal
- **Fondo**: `#F8FAFE` - Azul muy claro
- **Tarjetas**: `#FFFFFF` - Blanco
- **Acentos**: `#F7FAFF` - Azul claro

### Componentes Principales
- **Sidebar**: Navegación lateral responsive con iconos
- **Header**: Cabecera fija con información del usuario
- **POS**: Interfaz de punto de venta
- **Configuración**: Gestión de catálogos empresariales

## 📱 Características

- ✅ **Responsive Design** - Adaptable a móviles y desktop
- ✅ **SPA Navigation** - Navegación sin recarga de página
- ✅ **Modern UI** - Interfaz limpia y moderna
- ✅ **Touch Optimized** - Optimizado para dispositivos táctiles
- ✅ **Accessible** - Cumple estándares de accesibilidad

## 🛠️ Desarrollo

### Prerrequisitos
- Node.js 18+ 
- npm 9+

### Instalación
```bash
# Clonar repositorio
git clone [URL_DEL_REPO]
cd front_end

# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev
```

### Scripts Disponibles
```bash
npm run dev      # Servidor de desarrollo
npm run build    # Build de producción
npm run preview  # Preview del build
npm run lint     # Ejecutar ESLint
```

## 🔗 Rutas

- `/` → Redirige a `/dashboard`
- `/dashboard` → Panel principal
- `/catalogo` → Gestión de catálogos
- `/pos` → Punto de venta
- `/configuracion` → Configuraciones del sistema
- `/ayuda` → Centro de ayuda

## 🎯 Próximas Características

- [ ] Integración con API backend
- [ ] Autenticación de usuarios
- [ ] Gestión completa de productos
- [ ] Reportes y analytics
- [ ] Modo offline

## 👥 Contribución

1. Fork el proyecto
2. Crea una branch para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la branch (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.