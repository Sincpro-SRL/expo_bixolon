package com.bixolon.labelprintersample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;
import kotlin.Unit;

// Importar TU código Kotlin
import sincpro.expo.printer.adapter.bixolon.BixolonPrinterAdapter;
import sincpro.expo.printer.infrastructure.bluetooth.AndroidBluetoothProvider;
import sincpro.expo.printer.infrastructure.orchestration.EventBus;
import sincpro.expo.printer.service.ConnectivityService;
import sincpro.expo.printer.domain.BluetoothDeviceInfo;
import sincpro.expo.printer.domain.ConnectionConfig;
import sincpro.expo.printer.domain.ConnectionType;

/**
 * Activity para testear tu código Kotlin desde Java
 * 
 * Esta activity usa:
 * - Domain (interfaces y entidades)
 * - Service (ConnectivityService)
 * - Adapter (BixolonPrinterAdapter)
 * - Infrastructure (AndroidBluetoothProvider, EventBus)
 * 
 * NO usa Entrypoint (ese es solo para Expo)
 */
public class MyPrinterTestActivity extends AppCompatActivity {
    
    private static final String TAG = "MyPrinterTest";
    
    // Tu código Kotlin
    private BixolonPrinterAdapter printerAdapter;
    private AndroidBluetoothProvider bluetoothProvider;
    private EventBus eventBus;
    private ConnectivityService connectivityService;
    
    // UI
    private TextView tvLog;
    private StringBuilder logBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Crear UI
        setContentView(createUI());
        
        // Inicializar componentes
        logBuilder = new StringBuilder();
        tvLog = findViewById(android.R.id.text1);
        
        // ✅ Inicializar tu código (SIGUIENDO CLEAN ARCHITECTURE)
        initializePrinterModule();
        
        log("✅ Módulo inicializado correctamente");
        log("📦 Usando: Domain + Service + Adapter + Infrastructure");
        log("🚫 Sin Entrypoint (no se necesita Expo aquí)");
        log("");
        log("Presiona un botón para comenzar...");
    }
    
    /**
     * Inicializar el módulo siguiendo Clean Architecture
     * 
     * Orden de inicialización:
     * 1. Infrastructure (EventBus, BluetoothProvider)
     * 2. Adapter (BixolonPrinterAdapter)
     * 3. Service (ConnectivityService con dependencias inyectadas)
     */
    private void initializePrinterModule() {
        try {
            // 1. INFRASTRUCTURE LAYER
            eventBus = new EventBus();
            bluetoothProvider = new AndroidBluetoothProvider(this);
            
            // 2. ADAPTER LAYER
            printerAdapter = new BixolonPrinterAdapter(this);
            
            // 3. SERVICE LAYER (inyectando dependencias)
            connectivityService = new ConnectivityService(
                bluetoothProvider,  // IBluetoothProvider
                eventBus,           // EventBus
                printerAdapter      // IPrinterAdapter
            );
            
            log("🏗️ Arquitectura inicializada:");
            log("  └─ Infrastructure: EventBus, BluetoothProvider");
            log("  └─ Adapter: BixolonPrinterAdapter");
            log("  └─ Service: ConnectivityService");
            
        } catch (Exception e) {
            log("❌ Error inicializando: " + e.getMessage());
            Log.e(TAG, "Error en inicialización", e);
        }
    }
    
    /**
     * Escanear dispositivos Bluetooth
     */
    private void scanDevices() {
        log("");
        log("🔍 Escaneando dispositivos Bluetooth...");
        
        try {
            // Usar tu ConnectivityService
            var result = connectivityService.getPairedBluetoothDevices();
            
            if (result.isSuccess()) {
                List<BluetoothDeviceInfo> devices = 
                    (List<BluetoothDeviceInfo>) result.getOrNull();
                
                log("✅ Encontrados " + devices.size() + " dispositivos:");
                log("");
                
                for (BluetoothDeviceInfo device : devices) {
                    String isPrinter = device.isPrinter() ? "🖨️ Impresora" : "📱 Otro";
                    log(String.format("%s %s", isPrinter, device.getName()));
                    log(String.format("   MAC: %s", device.getAddress()));
                    log(String.format("   Tipo: %s", device.getType()));
                    log("");
                }
                
                Toast.makeText(this, 
                    "Encontrados: " + devices.size(), 
                    Toast.LENGTH_SHORT).show();
                    
            } else {
                Throwable error = result.exceptionOrNull();
                String msg = error != null ? error.getMessage() : "Error desconocido";
                log("❌ Error: " + msg);
                Log.e(TAG, "Error escaneando", error);
            }
            
        } catch (Exception e) {
            log("❌ Excepción: " + e.getMessage());
            Log.e(TAG, "Excepción en scan", e);
        }
    }
    
    /**
     * Conectar al primer dispositivo encontrado
     */
    private void connectToDevice() {
        log("");
        log("🔌 Buscando dispositivo para conectar...");
        
        try {
            var result = connectivityService.getPairedBluetoothDevices();
            
            if (result.isSuccess()) {
                List<BluetoothDeviceInfo> devices = 
                    (List<BluetoothDeviceInfo>) result.getOrNull();
                
                if (devices.isEmpty()) {
                    log("❌ No hay dispositivos emparejados");
                    log("💡 Ve a Settings → Bluetooth y empareja uno primero");
                    return;
                }
                
                // Buscar impresora
                BluetoothDeviceInfo targetDevice = null;
                for (BluetoothDeviceInfo device : devices) {
                    if (device.isPrinter()) {
                        targetDevice = device;
                        break;
                    }
                }
                
                if (targetDevice == null) {
                    targetDevice = devices.get(0);
                    log("⚠️ No se detectó impresora, usando primer dispositivo");
                }
                
                final String name = targetDevice.getName();
                final String address = targetDevice.getAddress();
                
                log("📍 Conectando a: " + name);
                log("   MAC: " + address);
                
                // Crear configuración de conexión
                ConnectionConfig config = new ConnectionConfig(
                    ConnectionType.BLUETOOTH,
                    address,
                    9100,
                    30000
                );
                
                // Conectar usando coroutines
                BuildersKt.launch(
                    kotlinx.coroutines.GlobalScope.INSTANCE,
                    Dispatchers.getIO(),
                    null,
                    (scope, continuation) -> {
                        try {
                            // Usar tu ConnectivityService
                            var connectResult = connectivityService.connect(config, continuation);
                            
                            if (connectResult == kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                                return connectResult;
                            }
                            
                            var finalResult = (kotlin.Result) connectResult;
                            
                            runOnUiThread(() -> {
                                if (finalResult.isSuccess()) {
                                    log("✅ Conectado exitosamente a " + name);
                                    Toast.makeText(this, "✅ Conectado!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Throwable error = finalResult.exceptionOrNull();
                                    String msg = error != null ? error.getMessage() : "Error";
                                    log("❌ Error conectando: " + msg);
                                    Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
                                }
                            });
                            
                        } catch (Exception e) {
                            Log.e(TAG, "Error en conexión", e);
                            runOnUiThread(() -> {
                                log("❌ Excepción: " + e.getMessage());
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                        
                        return Unit.INSTANCE;
                    }
                );
                
            } else {
                log("❌ No se pudieron obtener dispositivos");
            }
            
        } catch (Exception e) {
            log("❌ Excepción: " + e.getMessage());
            Log.e(TAG, "Excepción en connect", e);
        }
    }
    
    /**
     * Verificar estado de Bluetooth
     */
    private void checkBluetoothStatus() {
        log("");
        log("📡 Verificando estado de Bluetooth...");
        
        try {
            boolean supported = connectivityService.isBluetoothSupported();
            boolean enabled = connectivityService.isBluetoothEnabled();
            
            log("Hardware Bluetooth: " + (supported ? "✅ Soportado" : "❌ No soportado"));
            log("Bluetooth activo: " + (enabled ? "✅ Encendido" : "❌ Apagado"));
            
            if (supported && enabled) {
                log("✅ Todo listo para conectar");
            } else if (!enabled) {
                log("💡 Enciende Bluetooth en Settings");
            }
            
        } catch (Exception e) {
            log("❌ Error: " + e.getMessage());
            Log.e(TAG, "Error verificando Bluetooth", e);
        }
    }
    
    /**
     * Agregar línea al log
     */
    private void log(String message) {
        logBuilder.append(message).append("\n");
        tvLog.setText(logBuilder.toString());
        Log.d(TAG, message);
        
        // Scroll to bottom
        final ScrollView scrollView = (ScrollView) tvLog.getParent();
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
    
    /**
     * Crear UI programáticamente
     */
    private android.view.View createUI() {
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32);
        mainLayout.setBackgroundColor(0xFFF5F5F5);
        
        // Título
        TextView title = new TextView(this);
        title.setText("🧪 Test Tu Código Kotlin");
        title.setTextSize(24);
        title.setTextColor(0xFF1976D2);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.setMargins(0, 0, 0, 24);
        title.setLayoutParams(titleParams);
        mainLayout.addView(title);
        
        // Subtítulo
        TextView subtitle = new TextView(this);
        subtitle.setText("Domain + Service + Adapter + Infrastructure");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF666666);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.setMargins(0, 0, 0, 32);
        subtitle.setLayoutParams(subtitleParams);
        mainLayout.addView(subtitle);
        
        // Botones
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(0, 0, 0, 16);
        
        Button btnStatus = createButton("📡 Estado Bluetooth", btnParams);
        btnStatus.setOnClickListener(v -> checkBluetoothStatus());
        mainLayout.addView(btnStatus);
        
        Button btnScan = createButton("🔍 Escanear Dispositivos", btnParams);
        btnScan.setOnClickListener(v -> scanDevices());
        mainLayout.addView(btnScan);
        
        Button btnConnect = createButton("🔌 Conectar", btnParams);
        btnConnect.setOnClickListener(v -> connectToDevice());
        mainLayout.addView(btnConnect);
        
        Button btnClear = createButton("🗑️ Limpiar Log", btnParams);
        btnClear.setOnClickListener(v -> {
            logBuilder = new StringBuilder();
            tvLog.setText("");
            log("Log limpiado");
        });
        mainLayout.addView(btnClear);
        
        // ScrollView para el log
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        );
        scrollParams.setMargins(0, 24, 0, 0);
        scrollView.setLayoutParams(scrollParams);
        scrollView.setBackgroundColor(0xFFFFFFFF);
        scrollView.setPadding(16, 16, 16, 16);
        
        // TextView para log
        tvLog = new TextView(this);
        tvLog.setId(android.R.id.text1);
        tvLog.setTextSize(12);
        tvLog.setTextColor(0xFF212121);
        tvLog.setTypeface(android.graphics.Typeface.MONOSPACE);
        scrollView.addView(tvLog);
        
        mainLayout.addView(scrollView);
        
        return mainLayout;
    }
    
    /**
     * Crear botón con estilo
     */
    private Button createButton(String text, LinearLayout.LayoutParams params) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setLayoutParams(params);
        btn.setBackgroundColor(0xFF2196F3);
        btn.setTextColor(0xFFFFFFFF);
        btn.setAllCaps(false);
        return btn;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("");
        log("👋 Activity destruida");
    }
}
