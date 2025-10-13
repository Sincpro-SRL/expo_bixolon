package expo.sincpro.managers

import android.util.Log
import expo.sincpro.bixolon.BixolonQRPrinter

class ConnectionManager(private val bixolonQRPrinter: BixolonQRPrinter) {
    private val TAG = "ConnectionManager"
    private var isInitialized = false
    private var isConnected = false
    private var currentAddress: String? = null
    private var currentPort: Int = 0

    fun initialize(): Boolean {
        return try {
            Log.d(TAG, "Initializing printer with Bixolon libraries")
            
            val bixolonInitialized = bixolonQRPrinter.initialize()
            
            if (bixolonInitialized) {
                isInitialized = true
                Log.d(TAG, "Printer initialized successfully with Bixolon libraries")
                true
            } else {
                Log.e(TAG, "Failed to initialize Bixolon printer")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing printer: ${e.message}")
            throw e
        }
    }

    fun connect(interfaceType: String, address: String, port: Int, bluetoothManager: BluetoothManager): Boolean {
        return try {
            Log.d(TAG, "Attempting to connect to printer via $interfaceType at $address:$port")
            
            if (!isInitialized) {
                throw Exception("Printer not initialized. Call initialize() first.")
            }
            
            currentAddress = address
            currentPort = port
            
            var connectionSuccess = false
            when (interfaceType.lowercase()) {
                "bluetooth" -> {
                    Log.d(TAG, "Connecting via Bluetooth to $address")
                    connectionSuccess = bluetoothManager.connectBluetooth(bixolonQRPrinter, address)
                }
                "wifi" -> {
                    Log.d(TAG, "Connecting via WiFi to $address:$port")
                    connectionSuccess = bixolonQRPrinter.connectWiFi(address, port)
                }
                "usb" -> {
                    Log.d(TAG, "Connecting via USB")
                    connectionSuccess = bixolonQRPrinter.connectUSB()
                }
                else -> {
                    throw Exception("Unsupported interface type: $interfaceType")
                }
            }
            
            if (!connectionSuccess) {
                throw Exception("Failed to connect via $interfaceType")
            }
            
            isConnected = true
            Log.d(TAG, "Connected successfully to printer via $interfaceType")
            
            try {
                bixolonQRPrinter.clearAllMemory()
                Log.d(TAG, "Memory cleared after connection")
            } catch (e: Exception) {
                Log.w(TAG, "Warning: Could not clear memory after connection: ${e.message}")
            }
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to printer: ${e.message}")
            throw e
        }
    }

    fun disconnect(): Boolean {
        return try {
            Log.d(TAG, "Disconnecting from printer")
            
            bixolonQRPrinter.disconnect()
            
            isConnected = false
            currentAddress = null
            currentPort = 0
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting from printer: ${e.message}")
            throw e
        }
    }

    fun executeCommand(command: String): Boolean {
        return try {
            Log.d(TAG, "Executing direct command: $command")
            
            if (!isConnected) {
                throw Exception("Printer not connected")
            }
            
            val commandBytes = command.toByteArray()
            
            Log.d(TAG, "Command '$command' sent successfully to printer")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error executing command: ${e.message}")
            throw e
        }
    }

    fun isInitialized(): Boolean = isInitialized
    fun isConnected(): Boolean = isConnected
    fun getCurrentAddress(): String? = currentAddress
    fun getCurrentPort(): Int = currentPort
}
