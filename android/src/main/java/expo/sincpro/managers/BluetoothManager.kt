package expo.sincpro.managers

import android.content.Context
import android.util.Log
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import expo.sincpro.bixolon.BixolonQRPrinter

class BluetoothManager(private val context: Context) {
    private val TAG = "BluetoothManager"
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter

    fun isBluetoothEnabled(): Boolean {
        return try {
            val isEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled
            Log.d(TAG, "Bluetooth enabled: $isEnabled")
            isEnabled
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Bluetooth status: ${e.message}")
            false
        }
    }

    fun discoverBluetoothDevices(): List<Map<String, Any>> {
        try {
            Log.d(TAG, "Starting Bluetooth device discovery")
            
            if (bluetoothAdapter == null) {
                throw Exception("Bluetooth not supported on this device")
            }
            
            if (!bluetoothAdapter.isEnabled) {
                throw Exception("Bluetooth is not enabled")
            }
            
            val pairedDevices = bluetoothAdapter.bondedDevices
            val deviceList = mutableListOf<Map<String, Any>>()
            
            for (device in pairedDevices) {
                val deviceInfo = mapOf(
                    "name" to (device.name ?: "Unknown Device"),
                    "address" to device.address,
                    "type" to when (device.type) {
                        BluetoothDevice.DEVICE_TYPE_CLASSIC -> "CLASSIC"
                        BluetoothDevice.DEVICE_TYPE_LE -> "LE"
                        BluetoothDevice.DEVICE_TYPE_DUAL -> "DUAL"
                        else -> "UNKNOWN"
                    },
                    "isPrinter" to isPrinterDevice(device.name)
                )
                deviceList.add(deviceInfo)
            }
            
            Log.d(TAG, "Found ${deviceList.size} Bluetooth devices")
            return deviceList
            
        } catch (e: Exception) {
            Log.e(TAG, "Error discovering Bluetooth devices: ${e.message}")
            throw e
        }
    }

    fun startBluetoothDiscovery(): Boolean {
        try {
            Log.d(TAG, "Starting Bluetooth discovery")
            
            if (bluetoothAdapter == null) {
                throw Exception("Bluetooth not supported on this device")
            }
            
            if (!bluetoothAdapter.isEnabled) {
                throw Exception("Bluetooth is not enabled")
            }
            
            return if (bluetoothAdapter.startDiscovery()) {
                Log.d(TAG, "Bluetooth discovery started")
                true
            } else {
                throw Exception("Failed to start Bluetooth discovery")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Bluetooth discovery: ${e.message}")
            throw e
        }
    }

    fun stopBluetoothDiscovery(): Boolean {
        try {
            Log.d(TAG, "Stopping Bluetooth discovery")
            
            if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
                Log.d(TAG, "Bluetooth discovery stopped")
            }
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping Bluetooth discovery: ${e.message}")
            throw e
        }
    }

    fun connectBluetooth(bixolonQRPrinter: BixolonQRPrinter, address: String): Boolean {
        return try {
            Log.d(TAG, "Attempting Bluetooth connection to $address using Bixolon SDK")
            
            val success = bixolonQRPrinter.connectBluetooth(address)
            
            if (success) {
                Log.d(TAG, "Bluetooth connection established successfully using Bixolon SDK")
                true
            } else {
                Log.e(TAG, "Failed to connect using Bixolon SDK")
                false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting via Bluetooth: ${e.message}")
            false
        }
    }

    private fun isPrinterDevice(deviceName: String?): Boolean {
        if (deviceName == null) return false
        
        val printerKeywords = listOf(
            "printer", "print", "bixolon", "label", "thermal", "receipt",
            "impresora", "etiqueta", "termica", "ticket", "pos", "terminal"
        )
        
        return printerKeywords.any { keyword ->
            deviceName.lowercase().contains(keyword.lowercase())
        }
    }
}
