# Android Development

Este es un módulo Expo. Los archivos de configuración de Gradle no están incluidos en el repositorio.

## Para desarrollo con Android Studio

Crea estos archivos **localmente** (ya están en .gitignore):

**1. `settings.gradle`:**

```gradle
rootProject.name = "SincproPrinter"
```

**2. `gradle.properties`:**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

**3. Abrir Android Studio:**

```bash
open -a "Android Studio" .
```

## ¿Por qué no se commitean?

Los módulos Expo se compilan dentro de apps host. La app proporciona su propia configuración de Gradle.

## Estructura del módulo

```
android/
├── build.gradle          ← Commitear (configuración del módulo)
├── libs/                 ← Commitear (SDKs de Bixolon)
├── src/main/             ← Commitear (código Kotlin)
│   ├── AndroidManifest.xml
│   └── java/sincpro/expo/printer/
├── settings.gradle       ← NO commitear (solo local)
├── gradle.properties     ← NO commitear (solo local)
└── .gradle/, .idea/      ← Ignorados (generados)
```
