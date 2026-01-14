# 🧪 Printer Test App

App de testing nativa en Kotlin para probar todos los casos de uso del módulo de impresora.

## ✅ Test Cases Implementados

### Actualmente en MainActivity.kt (8 básicos):

- TC1: Estado Bluetooth ✅
- TC2: Escanear dispositivos ✅
- TC3: Conectar ✅
- TC4: Desconectar ✅
- TC5: Estado impresora ✅
- TC6: Imprimir texto simple ✅
- TC7: Imprimir con formato ✅
- TC8: Limpiar buffer ✅

### 📋 15 Test Cases Adicionales Disponibles

Ver [ADDITIONAL_TEST_CASES.md](ADDITIONAL_TEST_CASES.md) para código completo:

**CONNECTIVITY SERVICE (5 adicionales):**

- TC4: getPairedPrinters
- TC5-TC7: Discovery (start/stop/isDiscovering)
- TC11-TC13: Connection status

**PRINT SERVICE (5 high-level):**

- TC14: printReceipt (con header/details/footer)
- TC15: printLines
- TC16: printQRCode
- TC17: printText
- TC18: printImage

**LOW LEVEL PRINT SERVICE (5):**

- TC19: drawTextDirect (posición X,Y)
- TC20: drawQRDirect
- TC21: drawBitmapDirect
- TC22: getPrinterStatus
- TC23: clearBuffer

**Total: 23 test cases** cubriendo todos los métodos públicos de los 3 servicios.

---

## 🚀 Setup

### 1. Abrir proyecto en Android Studio:

```bash
cd test-app
open -a "Android Studio" .
```

### 2. Sincronizar Gradle

- Espera que Android Studio sincronice
- Descargará dependencias automáticamente

### 3. Ejecutar

- Conecta dispositivo Android o inicia emulador
- Click en ▶️ Run
- Selecciona dispositivo

---

## 📱 Uso

### Permisos

La app solicita permisos de Bluetooth automáticamente al iniciar.

### Workflow recomendado:

1. **TC1: Estado Bluetooth** - Verifica que Bluetooth esté habilitado
2. **TC2: Escanear** - Lista dispositivos disponibles
3. **TC3: Conectar** - Conecta a impresora
4. **TC5: Estado** - Verifica estado de conexión
5. **TC6 o TC7: Imprimir** - Prueba impresión
6. **TC4: Desconectar** - Desconecta cuando termines

### Ejecutar todos los tests

- Presiona **▶️ EJECUTAR TODOS** para correr tests automáticos
- Tests de conexión/impresión requieren ejecución manual

---

## 🏗️ Arquitectura

```
TestApp (Kotlin)
  ↓
SERVICE LAYER
  ├─ ConnectivityService (conexión, scan)
  └─ LowLevelPrintService (impresión)
  ↓
ADAPTER LAYER
  └─ BixolonPrinterAdapter
  ↓
INFRASTRUCTURE LAYER
  ├─ AndroidBluetoothProvider
  └─ EventBus
  ↓
DOMAIN LAYER
  └─ Interfaces + Entidades
```

---

## 📊 Ver logs en tiempo real

```bash
adb logcat -c
adb logcat | grep "PrinterTest"
```

Ejemplo de output:

```
D/PrinterTest: 🏗️ Inicializando módulo de impresora...
D/PrinterTest: ✅ Módulo inicializado correctamente
D/PrinterTest: ═══════════════════════════════════════
D/PrinterTest: TEST CASE 2: Escanear Dispositivos
D/PrinterTest: ═══════════════════════════════════════
D/PrinterTest: ✅ Encontrados 2 dispositivos:
D/PrinterTest: 1. 🖨️ SPP-R310
D/PrinterTest:    MAC: 00:13:7B:56:62:21
D/PrinterTest:    Tipo: CLASSIC
D/PrinterTest:    Es impresora: Sí
D/PrinterTest: ✅ TEST PASSED: Scan completado
```

---

## 🔧 Compilar APK

```bash
cd test-app
./gradlew assembleDebug

# APK generada en:
# app/build/outputs/apk/debug/app-debug.apk
```

Instalar en dispositivo:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🐛 Solución de problemas

### "Module :printer-core not found"

```bash
# Verificar que existe
ls -la ../android/

# Sincronizar
./gradlew clean
# En Android Studio: File → Sync Project with Gradle Files
```

### Permisos denegados

- Ve a Settings → Apps → Printer Test App → Permissions
- Activa permisos de Bluetooth y Ubicación

### No detecta impresora

- Ve a Settings → Bluetooth
- Empareja la impresora primero
- Vuelve a la app y escanea

---

## 💡 Agregar más test cases

Edita `MainActivity.kt` y agrega:

```kotlin
private fun testCase9_MyNewTest() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 9: Mi Nuevo Test")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            // Tu código aquí
            log("✅ TEST PASSED")
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}
```

Agrega botón en `createUI()`:

```kotlin
layout.addView(createButton("🎯 TC9: Mi Test", btnParams) {
    testCase9_MyNewTest()
})
```

---

## ✅ Ventajas de esta app

1. **Nativa en Kotlin** - Sin problemas de compatibilidad con Java
2. **Test cases organizados** - Fácil de agregar/modificar
3. **UI simple** - Enfocada en testing, no en diseño
4. **Logs completos** - Ve exactamente qué pasa
5. **Usa tu código real** - Módulo como dependencia
6. **Sin Expo** - Testing puro Android
7. **Fácil de debuggear** - Breakpoints nativos

---

## 📚 Recursos

- **Tu módulo**: `../android/`
- **Configuración**: `settings.gradle.kts`
- **Dependencias**: `app/build.gradle.kts`
- **Activity principal**: `app/src/main/kotlin/com/sincpro/printer/testapp/MainActivity.kt`

---

## 🎯 Próximos pasos

1. [ ] Abrir en Android Studio
2. [ ] Ejecutar en dispositivo
3. [ ] Probar todos los test cases
4. [ ] Agregar test cases adicionales según necesites
5. [ ] Compartir APK con el equipo

**¡Lista para testear!** 🚀
