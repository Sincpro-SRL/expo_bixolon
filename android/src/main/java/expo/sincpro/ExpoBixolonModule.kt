package expo.sincpro

import android.util.Log
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.sincpro.bixolon.BixolonQRPrinter
import expo.sincpro.managers.BluetoothManager
import expo.sincpro.managers.ConnectionManager
import expo.sincpro.managers.PermissionManager
import expo.sincpro.managers.PrinterManager

class ExpoBixolonModule : Module() {
    private val TAG = "ExpoBixolonModule"
    
    private var bixolonQRPrinter: BixolonQRPrinter? = null
    private var bluetoothManager: BluetoothManager? = null
    private var connectionManager: ConnectionManager? = null
    private var printerManager: PrinterManager? = null
    private var permissionManager: PermissionManager? = null

    override fun definition() = ModuleDefinition {
        Name("ExpoBixolon")

        AsyncFunction("initializePrinter") { 
            try {
                Log.d(TAG, "Initializing printer with Bixolon libraries")
                
                if (bixolonQRPrinter == null) {
                    bixolonQRPrinter = BixolonQRPrinter(appContext.reactContext!!)
                    connectionManager = ConnectionManager(bixolonQRPrinter!!)
                    printerManager = PrinterManager(bixolonQRPrinter!!)
                }
                
                return@AsyncFunction connectionManager!!.initialize()
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing printer: ${e.message}")
                throw e
            }
        }

        AsyncFunction("connectPrinter") { interfaceType: String, address: String, port: Int ->
            try {
                if (connectionManager == null || !connectionManager!!.isInitialized()) {
                    throw Exception("Printer not initialized. Call initializePrinter() first.")
                }
                
                if (bluetoothManager == null) {
                    bluetoothManager = BluetoothManager(appContext.reactContext!!)
                }
                
                return@AsyncFunction connectionManager!!.connect(interfaceType, address, port, bluetoothManager!!)
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting to printer: ${e.message}")
                throw e
            }
        }

        AsyncFunction("disconnectPrinter") {
            try {
                if (connectionManager == null) {
                    return@AsyncFunction true
                }
                
                return@AsyncFunction connectionManager!!.disconnect()
            } catch (e: Exception) {
                Log.e(TAG, "Error disconnecting from printer: ${e.message}")
                throw e
            }
        }

        AsyncFunction("executeCommand") { command: String ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                return@AsyncFunction connectionManager!!.executeCommand(command)
            } catch (e: Exception) {
                Log.e(TAG, "Error executing command: ${e.message}")
                throw e
            }
        }

        AsyncFunction("testPlainText") { text: String ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                return@AsyncFunction printerManager!!.printPlainText(text)
            } catch (e: Exception) {
                Log.e(TAG, "Error sending plain text: ${e.message}")
                throw e
            }
        }

        AsyncFunction("printInvoice") { invoiceText: String ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                return@AsyncFunction printerManager!!.printInvoice(invoiceText)
            } catch (e: Exception) {
                Log.e(TAG, "Error printing invoice: ${e.message}")
                throw e
            }
        }

        AsyncFunction("printQRCode") { text: String, size: Int ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                return@AsyncFunction printerManager!!.printQRCode(text, size)
            } catch (e: Exception) {
                Log.e(TAG, "Error printing QR code: ${e.message}")
                throw e
            }
        }

        AsyncFunction("printQRCodeAdvanced") { 
            data: String, 
            horizontalPosition: Int, 
            verticalPosition: Int, 
            model: String, 
            eccLevel: String, 
            size: Int, 
            rotation: String 
        ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                return@AsyncFunction printerManager!!.printQRCodeAdvanced(data, size)
            } catch (e: Exception) {
                Log.e(TAG, "Error printing QR code: ${e.message}")
                throw e
            }
        }

        AsyncFunction("printFormattedText") { text: String, fontSize: Int? ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                val actualFontSize = fontSize ?: 10
                return@AsyncFunction printerManager!!.printFormattedText(text, actualFontSize)
            } catch (e: Exception) {
                Log.e(TAG, "Error printing formatted text: ${e.message}")
                throw e
            }
        }

        AsyncFunction("printTextSimple") { text: String ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                return@AsyncFunction printerManager!!.printTextSimple(text)
            } catch (e: Exception) {
                Log.e(TAG, "Error printing simple text: ${e.message}")
                throw e
            }
        }

        AsyncFunction("printTextInPages") { text: String ->
            try {
                if (connectionManager == null || !connectionManager!!.isConnected()) {
                    throw Exception("Printer not connected")
                }
                
                return@AsyncFunction printerManager!!.printTextInPages(text)
            } catch (e: Exception) {
                Log.e(TAG, "Error printing text in pages: ${e.message}")
                throw e
            }
        }


        AsyncFunction("requestBluetoothPermissions") {
            try {
                if (permissionManager == null) {
                    permissionManager = PermissionManager(appContext.reactContext!!)
                }
                
                return@AsyncFunction permissionManager!!.requestBluetoothPermissions(appContext.currentActivity)
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting Bluetooth permissions: ${e.message}")
                return@AsyncFunction false
            }
        }

        AsyncFunction("checkBluetoothPermissions") {
            try {
                if (permissionManager == null) {
                    permissionManager = PermissionManager(appContext.reactContext!!)
                }
                
                return@AsyncFunction permissionManager!!.checkBluetoothPermissions()
            } catch (e: Exception) {
                Log.e(TAG, "Error checking Bluetooth permissions: ${e.message}")
                throw e
            }
        }

        AsyncFunction("discoverBluetoothDevices") {
            try {
                if (bluetoothManager == null) {
                    bluetoothManager = BluetoothManager(appContext.reactContext!!)
                }
                
                if (permissionManager == null) {
                    permissionManager = PermissionManager(appContext.reactContext!!)
                }
                
                if (!permissionManager!!.hasRequiredPermissions()) {
                    throw Exception("Required permissions not granted. Call requestBluetoothPermissions() first.")
                }
                
                return@AsyncFunction bluetoothManager!!.discoverBluetoothDevices()
            } catch (e: Exception) {
                Log.e(TAG, "Error discovering Bluetooth devices: ${e.message}")
                throw e
            }
        }

        AsyncFunction("startBluetoothDiscovery") {
            try {
                if (bluetoothManager == null) {
                    bluetoothManager = BluetoothManager(appContext.reactContext!!)
                }
                
                return@AsyncFunction bluetoothManager!!.startBluetoothDiscovery()
            } catch (e: Exception) {
                Log.e(TAG, "Error starting Bluetooth discovery: ${e.message}")
                throw e
            }
        }

        AsyncFunction("stopBluetoothDiscovery") {
            try {
                if (bluetoothManager == null) {
                    bluetoothManager = BluetoothManager(appContext.reactContext!!)
                }
                
                return@AsyncFunction bluetoothManager!!.stopBluetoothDiscovery()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping Bluetooth discovery: ${e.message}")
                throw e
            }
        }

        AsyncFunction("isBluetoothEnabled") {
            try {
                if (bluetoothManager == null) {
                    bluetoothManager = BluetoothManager(appContext.reactContext!!)
                }
                
                return@AsyncFunction bluetoothManager!!.isBluetoothEnabled()
            } catch (e: Exception) {
                Log.e(TAG, "Error checking Bluetooth status: ${e.message}")
                throw e
            }
        }
    }
}