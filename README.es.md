# Binnacle - Sistema de Gestión de Seguridad

> 🌐 **Leer en:** [English](README.md)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5+-green.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Binnacle** es una aplicación móvil moderna desarrollada con Jetpack Compose para la gestión de seguridad en urbanizaciones. La aplicación permite a los guardias de seguridad realizar rondas, registrar incidentes y mantener un control de asistencia, mientras notifica en tiempo real a los administradores sobre eventos importantes.

## Resumen

Binnacle optimiza la gestión de seguridad en comunidades residenciales mediante funcionalidades de seguimiento en tiempo real, reporte de incidentes y control de asistencia. Los guardias pueden documentar eventos con fotografías, realizar rondas con seguimiento GPS y registrar sus horarios de entrada y salida, mientras los administradores reciben notificaciones instantáneas de cualquier novedad.

## Características Principales

### Para Guardias de Seguridad

- **Rondas con Seguimiento GPS**: Realiza rondas de seguridad con visualización en tiempo real de la posición del guardia en el mapa durante el recorrido por el sector
- **Reporte de Eventos**: Notifica incidentes o eventos que ocurren en el día a día de la urbanización con soporte para fotografías
- **Control de Asistencia**: Registra automáticamente el ingreso y salida diaria de los guardias
- **Interfaz Intuitiva**: Diseño moderno con Material 3 optimizado para uso en campo

### Para Administradores

- **Notificaciones en Tiempo Real**: Recibe alertas inmediatas cuando los guardias reportan eventos o incidentes
- **Monitoreo de Rondas**: Visualiza la ubicación en tiempo real de los guardias durante sus rondas
- **Gestión de Personal**: Consulta los registros de entrada y salida del personal de seguridad
- **Historial de Eventos**: Accede al registro completo de incidentes reportados

## Capturas de Pantalla

<p align="center">
  <img src="capturas/images.png" alt="Binnacle App Screenshots" width="100%"/>
</p>

La aplicación incluye:
- **Splash Screen & Onboarding**: Pantalla de bienvenida con animación de logo y selección de urbanización
- **Autenticación**: Login para guardias (cédula) y administradores (email/password)
- **Panel Principal**: Navegación intuitiva con acceso a todas las funcionalidades
- **Rondas con GPS**: Sistema de rutas con seguimiento en tiempo real y cronómetro
- **Reportes de Eventos**: Creación de reportes con descripción, fotografías y verificación
- **Gestión de Sesión**: Diálogos de confirmación para finalizar rondas y cerrar sesión

## Tecnologías Utilizadas

| Categoría | Tecnología |
|-----------|-----------|
| **Lenguaje** | Kotlin |
| **Arquitectura** | MVVM + StateFlow |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Inyección de Dependencias** | Hilt / Dagger |
| **Navegación** | Navigation Compose |
| **Google APIs** | Maps SDK + Geocoding API |
| **Networking** | Retrofit + OkHttp |
| **Base de Datos Local** | Room |
| **Carga de Imágenes** | Coil v3 |
| **Tareas en Background** | WorkManager |

## Estructura del Proyecto

El proyecto sigue los principios de Clean Architecture con separación clara de responsabilidades:
```
app/src/main/java/com/clerodri/binnacle/
├── addreport/              # Módulo de reportes de eventos
│   ├── data/              # Repositorios, API clients, DTOs
│   ├── domain/            # Casos de uso, modelos de dominio
│   └── presentation/      # ViewModels, Screens, UI components
├── authentication/         # Módulo de autenticación
│   ├── data/              # Fuentes de datos locales y remotas
│   ├── domain/            # Lógica de negocio de autenticación
│   └── presentation/      # Pantallas de login (admin/guard)
├── home/                  # Módulo principal
│   ├── data/              # Gestión de rondas, check-in/out
│   │   └── datasource/
│   │       ├── local/     # Room database
│   │       └── network/   # API services
│   ├── domain/            # Casos de uso de home
│   └── presentation/      # Pantalla principal y componentes
├── core/                  # Componentes compartidos
│   ├── components/        # UI components reutilizables
│   └── di/               # Módulos de inyección de dependencias
├── app/                   # Configuración de la app
│   └── navigation/        # Sistema de navegación
└── ui/theme/             # Tema y estilos Material 3
```

### Arquitectura por Capas

Cada módulo funcional sigue Clean Architecture:

- **Presentation**: ViewModels, Screens (Jetpack Compose), UI Events/States
- **Domain**: Use Cases, Modelos de negocio, Interfaces de repositorios
- **Data**: Implementaciones de repositorios, Data Sources (local/remote), DTOs

## Requisitos Previos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 17 o superior
- Android SDK API 24+ (Android 7.0) como mínimo
- Android SDK API 34 (recomendado)
- Cuenta de Google Cloud con Maps SDK y Geocoding API habilitadas

## Instalación

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/binnacle.git
cd binnacle_kotlin
```

### 2. Configurar las API Keys

Crea o edita los siguientes archivos en la raíz del proyecto:

#### `local.properties`
```properties
BASE_URL=https://tu-backend-url.com/api/
```

#### `secret.properties`
```properties
MAPS_API_KEY=tu-google-api-key
```

**Importante**: Estos archivos están en `.gitignore` y no se deben versionar por seguridad.

### 3. Obtener las API Keys necesarias

#### Google Maps API Key
1. Ve a [Google Cloud Console](https://console.cloud.google.com/)
2. Crea un nuevo proyecto o selecciona uno existente
3. Habilita las siguientes APIs:
   - Maps SDK for Android
   - Geocoding API
4. Ve a "Credenciales" y crea una API Key
5. Restringe la key para uso solo en Android (recomendado)

#### Backend API Key
Contacta al administrador del backend para obtener las credenciales de acceso.

### 4. Sincronizar y Ejecutar

1. Abre el proyecto en Android Studio
2. Espera a que Gradle sincronice las dependencias
3. Conecta un dispositivo Android o inicia un emulador
4. Ejecuta la aplicación presionando Run (▶️) o usando `Shift + F10`

## Características Técnicas Destacadas

### Gestión de Estado
- StateFlow para flujos de datos reactivos
- UiState patterns para estados de UI predecibles
- Eventos de un solo uso (SingleLiveEvent pattern)

### Persistencia de Datos
- Room para almacenamiento local de rutas y cache
- DataStore para preferencias de usuario
- WorkManager para sincronización en background

### Integración con Google Maps
- Visualización de mapas en tiempo real
- Tracking de ubicación del guardia durante rondas
- Geocodificación de direcciones

### Manejo de Imágenes
- Captura de fotos con CameraX
- Compresión y optimización automática
- Upload asíncrono con WorkManager
- Cache eficiente con Coil

## Licencia

Este proyecto está bajo licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

---

**Desarrollado con ❤️ usando Jetpack Compose**
```
