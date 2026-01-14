# ✅ Tu Código Kotlin Funcionando en Sample Java

## 🎯 Lo que configuré:

### 1. **Incluí tu módulo como dependencia**

```gradle
// sample/JavaSample/settings.gradle
include ':printer-core'
project(':printer-core').projectDir = new File('../../android')
```

### 2. **Agregué dependencias necesarias**

```gradle
// sample/JavaSample/app/build.gradle
implementation project(':printer-core')
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
```

### 3. **Creé Activity de prueba en Java**

`MyPrinterTestActivity.java` - Usa tu código Kotlin directamente

---

## 📦 Qué código se usa:

### ✅ SE USA (Clean Architecture Core):

- **Domain** - Interfaces (`IPrinterAdapter`, `IBluetoothProvider`) y entidades
- **Service** - `ConnectivityService`, `PrintService`, etc.
- **Adapter** - `BixolonPrinterAdapter`
- **Infrastructure** - `AndroidBluetoothProvider`, `EventBus`

### ❌ NO SE USA:

- **Entrypoint** - Solo es para Expo/React Native (no se necesita aquí)

---

## 🚀 Cómo testear:

### 1. Abrir en Android Studio:

```bash
cd sample/JavaSample
open -a "Android Studio" .
```

### 2. Esperar sincronización de Gradle

- Android Studio detectará `:printer-core`
- Descargará dependencias
- Indexará el código

### 3. Ejecutar

- Click en ▶️ Run
- O `Shift + F10`

### 4. En el launcher verás DOS apps:

- **🧪 Test Mi Código** ← Tu código (ESTE)
- **LabelPrinterSample** ← Sample original de Bixolon

---

## 📱 Funcionalidades de la app de prueba:

### 📡 Estado Bluetooth

Verifica si Bluetooth está soportado y habilitado

### 🔍 Escanear Dispositivos

Usa `ConnectivityService.getPairedBluetoothDevices()`

- Muestra todos los dispositivos emparejados
- Indica cuáles son impresoras
- Muestra MAC, nombre, tipo

### 🔌 Conectar

Usa `ConnectivityService.connect()`

- Conecta al primer dispositivo (o impresora si encuentra)
- Muestra resultado en tiempo real

### 🗑️ Limpiar Log

Limpia el log de la UI

---

## 📝 Ejemplo de uso desde Java:

```java
// 1. Inicializar (ORDEN IMPORTANTE según Clean Architecture)
EventBus eventBus = new EventBus();
AndroidBluetoothProvider bluetoothProvider = new AndroidBluetoothProvider(context);
BixolonPrinterAdapter printerAdapter = new BixolonPrinterAdapter(context);

// 2. Crear servicio con dependencias inyectadas
ConnectivityService connectivityService = new ConnectivityService(
    bluetoothProvider,  // IBluetoothProvider
    eventBus,           // EventBus
    printerAdapter      // IPrinterAdapter
);

// 3. Usar el servicio
Result<List<BluetoothDeviceInfo>> result =
    connectivityService.getPairedBluetoothDevices();

if (result.isSuccess()) {
    List<BluetoothDeviceInfo> devices = result.getOrNull();
    for (BluetoothDeviceInfo device : devices) {
        System.out.println(device.getName() + " - " + device.getAddress());
    }
}

// 4. Conectar (async con coroutines)
ConnectionConfig config = new ConnectionConfig(
    ConnectionType.BLUETOOTH,
    "00:11:22:33:44:55",
    9100,
    30000
);

BuildersKt.launch(
    GlobalScope.INSTANCE,
    Dispatchers.getIO(),
    null,
    (scope, continuation) -> {
        Result<Unit> connectResult =
            connectivityService.connect(config, continuation);

        if (connectResult.isSuccess()) {
            System.out.println("✅ Conectado!");
        }

        return Unit.INSTANCE;
    }
);
```

---

## 🏗️ Arquitectura usada:

```
┌─────────────────────────────────────────────┐
│  Java Activity (MyPrinterTestActivity)     │
│  ↓                                          │
│  SERVICE LAYER (ConnectivityService)       │
│  ↓                     ↓                    │
│  ADAPTER              INFRASTRUCTURE        │
│  (BixolonAdapter)     (BluetoothProvider)   │
│  ↓                                          │
│  DOMAIN (Interfaces + Entities)            │
└─────────────────────────────────────────────┘

❌ NO usa Entrypoint (es solo para Expo)
```

---

## 🔍 Ver logs en tiempo real:

```bash
adb logcat -c
adb logcat | grep "MyPrinterTest"
```

Verás:

```
D/MyPrinterTest: ✅ Módulo inicializado correctamente
D/MyPrinterTest: 🔍 Escaneando dispositivos Bluetooth...
D/MyPrinterTest: ✅ Encontrados 2 dispositivos:
D/MyPrinterTest: 🖨️ Impresora SPP-R310
D/MyPrinterTest:    MAC: 00:13:7B:56:62:21
```

---

## 💡 Ventajas de este enfoque:

### 1. **Testing aislado**

- Solo Android, sin JavaScript
- Compilación rápida
- Debugging nativo

### 2. **Mismas librerías**

- Usa el mismo SDK de Bixolon del sample
- Mismas `.so` files
- Ambiente ya probado

### 3. **Código real**

- Usas TU código exacto de `android/`
- Sin copiar/pegar
- Cambios se reflejan automáticamente

### 4. **Aprende el patrón**

- Ve cómo usar tu código desde Java
- Entiende la inicialización
- Patrón para otros proyectos

---

## 🔧 Agregar más funcionalidades:

### Para probar impresión:

```java
// En MyPrinterTestActivity.java

// Agregar botón
Button btnPrint = createButton("🖨️ Imprimir Test", btnParams);
btnPrint.setOnClickListener(v -> printTest());
mainLayout.addView(btnPrint);

// Agregar método
private void printTest() {
    log("");
    log("🖨️ Imprimiendo...");

    // Necesitas LowLevelPrintService
    LowLevelPrintService printService = new LowLevelPrintService(printerAdapter);

    BuildersKt.launch(
        GlobalScope.INSTANCE,
        Dispatchers.getIO(),
        null,
        (scope, continuation) -> {
            try {
                // Imprimir texto
                printService.drawText("¡Hola desde Java!", 0, 0, continuation);
                printService.printBuffer(continuation);

                runOnUiThread(() -> {
                    log("✅ Impreso correctamente");
                    Toast.makeText(this, "✅ Impreso!", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                runOnUiThread(() -> log("❌ Error: " + e.getMessage()));
            }

            return Unit.INSTANCE;
        }
    );
}
```

---

## 🐛 Solución de problemas:

### "Cannot resolve sincpro.expo.printer"

```bash
# En Android Studio:
File → Invalidate Caches → Invalidate and Restart
```

### "Module :printer-core not found"

```bash
cd sample/JavaSample
./gradlew clean
# Luego: File → Sync Project with Gradle Files
```

### "Permission denied Bluetooth"

- Ve a Settings del dispositivo
- Bluetooth → Empareja con impresora primero
- Luego escanea en la app

### App crashea

```bash
adb logcat | grep -A 30 "AndroidRuntime"
```

---

## ✅ Verificación:

```bash
# Desde sample/JavaSample/
./gradlew :app:dependencies | grep printer-core

# Debe mostrar:
# \--- project :printer-core
#      \--- org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3
```

---

## 🎉 Resumen:

✅ **Tu código Kotlin** (domain, service, adapter, infrastructure)  
✅ **Funcionando en Java** sin modificaciones  
✅ **Sin Entrypoint** (no se necesita Expo aquí)  
✅ **Testing nativo Android** rápido y efectivo

**¡Listo para testear!** 🚀

```bash
cd sample/JavaSample
open -a "Android Studio" .
# Click ▶️ Run
# Toca "🧪 Test Mi Código"
# Presiona "🔍 Escanear Dispositivos"
```
