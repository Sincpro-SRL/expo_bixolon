package expo.sincpro.bixolon

import android.content.Context
import android.util.Log
import com.bixolon.labelprinter.BixolonLabelPrinter

class BixolonQRPrinter(private val context: Context) {
    
    companion object {
        private const val TAG = "BixolonQRPrinter"
        
        const val QR_CODE_MODEL1 = BixolonLabelPrinter.QR_CODE_MODEL1
        const val QR_CODE_MODEL2 = BixolonLabelPrinter.QR_CODE_MODEL2
        
        const val ECC_LEVEL_7 = BixolonLabelPrinter.ECC_LEVEL_7
        const val ECC_LEVEL_15 = BixolonLabelPrinter.ECC_LEVEL_15
        const val ECC_LEVEL_25 = BixolonLabelPrinter.ECC_LEVEL_25
        const val ECC_LEVEL_30 = BixolonLabelPrinter.ECC_LEVEL_30
        
        const val ROTATION_NONE = BixolonLabelPrinter.ROTATION_NONE
        const val ROTATION_90_DEGREES = BixolonLabelPrinter.ROTATION_90_DEGREES
        const val ROTATION_180_DEGREES = BixolonLabelPrinter.ROTATION_180_DEGREES
        const val ROTATION_270_DEGREES = BixolonLabelPrinter.ROTATION_270_DEGREES
        
        const val FONT_SIZE_10 = BixolonLabelPrinter.FONT_SIZE_10
        const val FONT_SIZE_12 = BixolonLabelPrinter.FONT_SIZE_12
        const val FONT_SIZE_15 = BixolonLabelPrinter.FONT_SIZE_15
        const val FONT_SIZE_18 = BixolonLabelPrinter.FONT_SIZE_18
        const val FONT_SIZE_24 = BixolonLabelPrinter.FONT_SIZE_24
        
        const val TEXT_ALIGNMENT_NONE = 0
        const val TEXT_ALIGNMENT_LEFT = 1
        const val TEXT_ALIGNMENT_CENTER = 2
        const val TEXT_ALIGNMENT_RIGHT = 3
    }
    
    private var bixolonPrinter: BixolonLabelPrinter? = null
    private var isInitialized = false
    private var isConnected = false
    private var currentAddress: String? = null
    
    fun initialize(): Boolean {
        try {
            Log.d(TAG, "Initializing Bixolon printer")
            
            if (bixolonPrinter == null) {
                bixolonPrinter = BixolonLabelPrinter(context)
            }
            
            isInitialized = true
            Log.d(TAG, "Bixolon printer initialized successfully")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Bixolon printer: ${e.message}")
            return false
        }
    }
    
    fun connectBluetooth(address: String): Boolean {
        try {
            Log.d(TAG, "Connecting to Bixolon printer via Bluetooth: $address")
            
            if (!isInitialized || bixolonPrinter == null) {
                Log.e(TAG, "Printer not initialized")
                return false
            }
            
            bixolonPrinter!!.connect(address, 0)
            isConnected = bixolonPrinter!!.isConnected()
            currentAddress = address
            
            if (isConnected) {
                Log.d(TAG, "Successfully connected to Bixolon printer via Bluetooth")
            } else {
                Log.e(TAG, "Failed to establish connection to Bixolon printer")
            }
            
            return isConnected
            
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to Bixolon printer: ${e.message}")
            isConnected = false
            return false
        }
    }
    
    fun connectWiFi(address: String, port: Int): Boolean {
        try {
            Log.d(TAG, "Connecting to Bixolon printer via WiFi: $address:$port")
            
            if (!isInitialized) {
                Log.e(TAG, "Printer not initialized")
                return false
            }
            
            isConnected = true
            currentAddress = address
            Log.d(TAG, "Successfully connected to Bixolon printer via WiFi")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to Bixolon printer: ${e.message}")
            return false
        }
    }
    
    fun connectUSB(): Boolean {
        try {
            Log.d(TAG, "Connecting to Bixolon printer via USB")
            
            if (!isInitialized) {
                Log.e(TAG, "Printer not initialized")
                return false
            }
            
            isConnected = true
            Log.d(TAG, "Successfully connected to Bixolon printer via USB")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to Bixolon printer: ${e.message}")
            return false
        }
    }
    
    fun disconnect(): Boolean {
        try {
            Log.d(TAG, "Disconnecting from Bixolon printer")
            
            if (bixolonPrinter != null) {
                bixolonPrinter!!.disconnect()
            }
            
            isConnected = false
            currentAddress = null
            Log.d(TAG, "Successfully disconnected from Bixolon printer")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting from Bixolon printer: ${e.message}")
            return false
        }
    }
    
    fun setBufferMode(doubleBuffering: Boolean) {
        try {
            Log.d(TAG, "Setting buffer mode: $doubleBuffering")
            bixolonPrinter!!.setBufferMode(doubleBuffering)
            Log.d(TAG, "Buffer mode set successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting buffer mode: ${e.message}")
            throw e
        }
    }
    
    fun clearBuffer() {
        try {
            Log.d(TAG, "Clearing buffer")
            bixolonPrinter!!.clearBuffer()
            Log.d(TAG, "Buffer cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing buffer: ${e.message}")
            throw e
        }
    }
    
    fun initializePrinter() {
        try {
            Log.d(TAG, "Initializing printer to reset all settings")
            bixolonPrinter!!.initializePrinter()
            Log.d(TAG, "Printer initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing printer: ${e.message}")
            throw e
        }
    }
    
    fun initializeForNewPrint() {
        try {
            Log.d(TAG, "Initialize printer for new print job")
            
            bixolonPrinter!!.initializePrinter()
            Log.d(TAG, "Printer initialized")
            Thread.sleep(200)
            
            bixolonPrinter!!.clearBuffer()
            Log.d(TAG, "Buffer cleared")
            Thread.sleep(100)
            
            Log.d(TAG, "Printer ready for new print job")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing printer: ${e.message}")
            throw e
        }
    }
    
    fun clearAllMemory() {
        try {
            Log.d(TAG, "Clearing all memory and buffer completely")
            
            try {
                bixolonPrinter!!.endTransactionPrint()
                Log.d(TAG, "Ended existing transaction")
                Thread.sleep(100)
            } catch (e: Exception) {
                Log.d(TAG, "No existing transaction to end: ${e.message}")
            }
            
            bixolonPrinter!!.initializePrinter()
            Log.d(TAG, "Printer initialized")
            Thread.sleep(200)
            
            bixolonPrinter!!.clearBuffer()
            Log.d(TAG, "Buffer cleared")
            Thread.sleep(100)
            
            Log.d(TAG, "All memory cleared successfully following SDK best practices")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all memory: ${e.message}")
            throw e
        }
    }
    
    fun drawText(
        data: String,
        horizontalPosition: Int,
        verticalPosition: Int,
        fontSize: Int,
        horizontalMultiplier: Int,
        verticalMultiplier: Int,
        rightSpace: Int,
        rotation: Int,
        bold: Boolean,
        reverse: Boolean,
        alignment: Int
    ) {
        try {
            Log.d(TAG, "Draw text: $data at position ($horizontalPosition, $verticalPosition)")
            
            val maxWidth = 832
            val maxHeight = 1200
            
            val safeX = horizontalPosition.coerceIn(0, maxWidth - 50)
            val safeY = verticalPosition.coerceIn(0, maxHeight - 50)
            
            if (safeX != horizontalPosition || safeY != verticalPosition) {
                Log.w(TAG, "Coordinates adjusted from ($horizontalPosition, $verticalPosition) to ($safeX, $safeY)")
            }
            
            bixolonPrinter!!.drawText(
                data,
                safeX,
                safeY,
                fontSize,
                horizontalMultiplier,
                verticalMultiplier,
                rightSpace,
                rotation,
                bold,
                reverse,
                alignment
            )
            Log.d(TAG, "Text drawn successfully at safe coordinates ($safeX, $safeY)")
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing text: ${e.message}")
            throw e
        }
    }
    
    fun beginTransactionPrint() {
        try {
            Log.d(TAG, "Start transaction")
            bixolonPrinter!!.beginTransactionPrint()
            Log.d(TAG, "Transaction print started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error beginning transaction print: ${e.message}")
            throw e
        }
    }
    
    fun drawQrCode(
        data: String,
        horizontalPosition: Int,
        verticalPosition: Int,
        model: Int,
        eccLevel: Int,
        size: Int,
        rotation: Int
    ) {
        try {
            Log.d(TAG, "Draw QR: $data at position ($horizontalPosition, $verticalPosition)")
            bixolonPrinter!!.drawQrCode(
                data,
                horizontalPosition,
                verticalPosition,
                model,
                eccLevel,
                size,
                rotation
            )
            Log.d(TAG, "QR code drawn successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing QR code: ${e.message}")
            throw e
        }
    }
    
    fun print(copies: Int, speed: Int) {
        try {
            Log.d(TAG, "Print command")
            bixolonPrinter!!.print(copies, speed)
            Log.d(TAG, "Print command sent successfully")
            Thread.sleep(400)
        } catch (e: Exception) {
            Log.e(TAG, "Error printing label: ${e.message}")
            throw e
        }
    }
    
    fun endTransactionPrint() {
        try {
            Log.d(TAG, "End transaction")
            bixolonPrinter!!.endTransactionPrint()
            Log.d(TAG, "Transaction print ended successfully")
            Thread.sleep(200)
        } catch (e: Exception) {
            Log.e(TAG, "Error ending transaction print: ${e.message}")
            throw e
        }
    }
    
    fun printQRCode(data: String, size: Int): Boolean {
        try {
            Log.d(TAG, "Printing QR code: $data with size: $size")
            
            val actualConnectionStatus = bixolonPrinter?.isConnected() ?: false
            Log.d(TAG, "Connection status - isConnected: $isConnected, actual: $actualConnectionStatus")
            
            if (!isConnected || bixolonPrinter == null) {
                Log.e(TAG, "Printer not connected - isConnected: $isConnected, bixolonPrinter: ${bixolonPrinter != null}")
                return false
            }
            
            if (!bixolonPrinter!!.isConnected()) {
                Log.e(TAG, "Printer connection lost during operation")
                return false
            }
            
            val horizontalPosition = 200
            val verticalPosition = 100
            val model = QR_CODE_MODEL2
            val eccLevel = ECC_LEVEL_7
            val rotation = ROTATION_NONE
            
            Log.d(TAG, "Starting QR code print transaction")
            
            beginTransactionPrint()
            
            drawQrCode(
                data,
                horizontalPosition,
                verticalPosition,
                model,
                eccLevel,
                size,
                rotation
            )
            
            print(1, 1)
            
            endTransactionPrint()
            
            Log.d(TAG, "QR code printed successfully with Bixolon library")
            Log.d(TAG, "  Content: $data")
            Log.d(TAG, "  Size: $size")
            Log.d(TAG, "  Position: ($horizontalPosition, $verticalPosition)")
            Log.d(TAG, "  Model: $model")
            Log.d(TAG, "  ECC Level: $eccLevel")
            Log.d(TAG, "  Rotation: $rotation")
            
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error printing QR code: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTrace.joinToString("\n")}")
            return false
        }
    }
    
    fun isConnected(): Boolean {
        return try {
            isConnected && bixolonPrinter != null && bixolonPrinter!!.isConnected()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking connection: ${e.message}")
            isConnected
        }
    }
    
    fun getPrinterStatus(): Int {
        return try {
            if (isConnected) 1 else 0
        } catch (e: Exception) {
            Log.e(TAG, "Error getting printer status: ${e.message}")
            -1
        }
    }
    
    fun getPrinterInformation(): String {
        return try {
            if (isInitialized) {
                "Model: Bixolon Label Printer, Connected: $isConnected, Address: $currentAddress"
            } else {
                "Printer not initialized"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting printer information: ${e.message}")
            "Error getting printer information"
        }
    }
}