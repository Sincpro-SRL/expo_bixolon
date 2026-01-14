package com.sincpro.printer.testapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.launch
import sincpro.expo.printer.adapter.bixolon.BixolonPrinterAdapter
import sincpro.expo.printer.domain.ConnectionConfig
import sincpro.expo.printer.domain.ConnectionType
import sincpro.expo.printer.infrastructure.bluetooth.AndroidBluetoothProvider
import sincpro.expo.printer.infrastructure.orchestration.EventBus
import sincpro.expo.printer.service.ConnectivityService
import sincpro.expo.printer.service.PrintService

class MainActivity : AppCompatActivity() {

    private lateinit var printerAdapter: BixolonPrinterAdapter
    private lateinit var bluetoothProvider: AndroidBluetoothProvider
    private lateinit var eventBus: EventBus
    private lateinit var orchestrator: sincpro.expo.printer.infrastructure.orchestration.PrintJobOrchestrator
    private lateinit var connectivityService: ConnectivityService
    private lateinit var printService: PrintService

    private lateinit var logTextView: MaterialTextView
    private lateinit var scrollView: ScrollView
    private val logBuilder = StringBuilder()

    private companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val ui = createUI()
        setContentView(ui)
        
        if (hasBluetoothPermissions()) {
            initializeModule()
        } else {
            requestBluetoothPermissions()
        }
    }

    private fun initializeModule() {
        try {
            log("🏗️ Inicializando módulo...")
            
            // Infrastructure
            bluetoothProvider = AndroidBluetoothProvider(this)
            eventBus = EventBus()
            
            // Adapter
            printerAdapter = BixolonPrinterAdapter(this)
            
            // Orchestrator (necesita adapter y eventBus)
            orchestrator = sincpro.expo.printer.infrastructure.orchestration.PrintJobOrchestrator(
                adapter = printerAdapter,
                eventBus = eventBus
            )
            
            // Services
            connectivityService = ConnectivityService(
                bluetoothProvider = bluetoothProvider,
                eventBus = eventBus,
                printerAdapter = printerAdapter
            )
            
            printService = PrintService(
                printerAdapter = printerAdapter,
                orchestrator = orchestrator,
                eventBus = eventBus
            )
            
            log("✅ Módulo inicializado correctamente")
            
        } catch (e: Exception) {
            log("❌ Error inicializando: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun testCase1_BluetoothStatus() {
        log("")
        log("═══════════════════════════════════════")
        log("TEST CASE 1: Estado Bluetooth")
        log("═══════════════════════════════════════")
        
        try {
            val supported = connectivityService.isBluetoothSupported()
            val enabled = connectivityService.isBluetoothEnabled()
            
            log("Hardware Bluetooth: ${if (supported) "✅ Soportado" else "❌ No soportado"}")
            log("Bluetooth activo: ${if (enabled) "✅ Encendido" else "❌ Apagado"}")
            
            if (supported && enabled) {
                log("✅ TEST PASSED")
            } else {
                log("⚠️ TEST WARNING: Bluetooth no disponible")
            }
            
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }

    private fun testCase2_ScanDevices() {
        log("")
        log("═══════════════════════════════════════")
        log("TEST CASE 2: Scan Devices")
        log("═══════════════════════════════════════")
        
        lifecycleScope.launch {
            try {
                val result = connectivityService.getPairedPrinters()
                
                result.onSuccess { devices ->
                    log("✅ Dispositivos paired: ${devices.size}")
                    devices.forEach { device ->
                        log("  📱 ${device.name} - ${device.address}")
                    }
                }.onFailure { error ->
                    log("❌ TEST FAILED: ${error.message}")
                }
                
            } catch (e: Exception) {
                log("❌ TEST FAILED: ${e.message}")
            }
        }
    }

    private fun testCase3_Connect() {
        log("")
        log("═══════════════════════════════════════")
        log("TEST CASE 3: Conectar")
        log("═══════════════════════════════════════")
        log("⚠️ Ingresa la dirección MAC de tu impresora")
        log("Ejemplo: 00:11:22:AA:BB:CC")
        
        // Aquí deberías obtener la dirección del usuario o usar una hardcodeada
        val address = "00:00:00:00:00:00" // CAMBIAR POR TU MAC
        
        lifecycleScope.launch {
            try {
                log("🔄 Conectando a $address...")
                
                val config = ConnectionConfig(
                    type = ConnectionType.BLUETOOTH,
                    address = address
                )
                
                val result = connectivityService.connect(config)
                
                result.onSuccess {
                    log("✅ TEST PASSED: Conectado!")
                    toast("Conectado!")
                }.onFailure { error ->
                    log("❌ TEST FAILED: ${error.message}")
                    toast("Error: ${error.message}")
                }
                
            } catch (e: Exception) {
                log("❌ TEST FAILED: ${e.message}")
            }
        }
    }

    private fun testCase4_PrintText() {
        log("")
        log("═══════════════════════════════════════")
        log("TEST CASE 4: Imprimir Texto")
        log("═══════════════════════════════════════")
        
        lifecycleScope.launch {
            try {
                log("🖨️ Imprimiendo texto de prueba...")
                
                val result = printService.printText("HOLA MUNDO!\nTest desde Kotlin\n\n\n")
                
                result.onSuccess {
                    log("✅ TEST PASSED: Texto impreso")
                    toast("Texto impreso!")
                }.onFailure { error ->
                    log("❌ TEST FAILED: ${error.message}")
                }
                
            } catch (e: Exception) {
                log("❌ TEST FAILED: ${e.message}")
            }
        }
    }

    private fun createUI(): android.view.View {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        // Título
        layout.addView(MaterialTextView(this).apply {
            text = "🧪 Printer Test App"
            textSize = 24f
            setPadding(0, 0, 0, 32)
        })

        val btnParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, 16)
        }

        // Botones
        layout.addView(createButton("1️⃣ Estado Bluetooth", btnParams) {
            testCase1_BluetoothStatus()
        })

        layout.addView(createButton("2️⃣ Scan Devices", btnParams) {
            testCase2_ScanDevices()
        })

        layout.addView(createButton("3️⃣ Conectar", btnParams) {
            testCase3_Connect()
        })

        layout.addView(createButton("4️⃣ Imprimir Texto", btnParams) {
            testCase4_PrintText()
        })

        layout.addView(createButton("🔄 Limpiar Log", btnParams) {
            logBuilder.clear()
            logTextView.text = ""
            log("Log limpiado")
        })

        // ScrollView para log
        scrollView = ScrollView(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
            ).apply {
                setMargins(0, 32, 0, 0)
            }
        }

        logTextView = MaterialTextView(this).apply {
            textSize = 12f
            setTextIsSelectable(true)
            typeface = android.graphics.Typeface.MONOSPACE
        }

        scrollView.addView(logTextView)
        layout.addView(scrollView)

        return layout
    }

    private fun createButton(text: String, params: android.widget.LinearLayout.LayoutParams, onClick: () -> Unit): MaterialButton {
        return MaterialButton(this).apply {
            this.text = text
            layoutParams = params
            setOnClickListener { onClick() }
        }
    }

    private fun log(message: String) {
        logBuilder.append("$message\n")
        logTextView.text = logBuilder.toString()
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
        android.util.Log.d("PrinterTest", message)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestBluetoothPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                initializeModule()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_LONG).show()
            }
        }
    }
}
