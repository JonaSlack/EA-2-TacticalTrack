# TacticalTrack

Proyecto semestral DSY1105 - Desarrollo de Aplicaciones Móviles.

## Configuración local antes de ejecutar el proyecto

Después de clonar el repositorio y abrir el proyecto en Android Studio, es posible que Git detecte archivos o carpetas modificadas automáticamente por el entorno de desarrollo.

Esto **no corresponde a errores del código de TacticalTrack**. Android Studio y Gradle generan archivos locales para funcionar en cada computador, por ejemplo:

- `.gradle/`
- `.idea/`
- `.kotlin/`
- `app/build/`
- `local.properties`

Estas configuraciones no deben interferir con el trabajo colaborativo ni generar cambios innecesarios en Git.

### macOS / Linux

Desde la raíz del proyecto, ejecutar una sola vez:

```bash
printf ".gradle/\n.idea/\n.kotlin/\napp/build/\n" >> .git/info/exclude
```

## Objetivo
MVP móvil para registrar y verificar visitas en terreno asociadas a vitrinas freezer y conservadoras,
utilizando evidencia digital y una arquitectura mantenible.

## Tecnologías base
- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Arquitectura MVVM
- GitHub
- Trello

## Estado actual
Repositorio inicial con estructura base. Las funcionalidades se desarrollarán progresivamente mediante
ramas `feature/*` y Pull Requests.

## Convención de trabajo
- No trabajar directamente sobre `main`.
- Crear una rama por funcionalidad.
- Realizar commits técnicos y descriptivos.
- Abrir Pull Request antes de integrar cambios.
- Mantener Trello actualizado con tareas reales.
