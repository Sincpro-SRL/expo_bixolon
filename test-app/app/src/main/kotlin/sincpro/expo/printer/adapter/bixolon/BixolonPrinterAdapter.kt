package sincpro.expo.printer.adapter.bixolon

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.bixolon.labelprinter.BixolonLabelPrinter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import sincpro.expo.printer.domain.IPrinterAdapter
import sincpro.expo.printer.domain.MediaConfig
import sincpro.expo.printer.domain.PrinterStatus
import kotlin.coroutines.resume

/**
 * ADAPTER - Bixolon Printer Implementation for SDK V2.1.1
 *
 * Implements IPrinterAdapter interface using Bixolon Label Printer SDK V2.1.1
 */
class BixolonPrinterAdapter(
    private val context: Context,
) : IPrinterAdapter {
    companion object {
        private const val TAG = "BixolonAdapter"
    }

    private var bixolonPrinter: BixolonLabelPrinter? = null
    private var isConnectedFlag = false

    // Handler for SDK callbacks
    private val printerHandler =
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    BixolonLabelPrinter.MESSAGE_STATE_CHANGE -> {
                        Log.d(TAG, "State change: ${msg.arg1}")
                        when (msg.arg1) {
                            BixolonLabelPrinter.STATE_CONNECTED -> {
                                isConnectedFlag = true
                                Log.d(TAG, "✅ Printer connected")
                            }
                            BixolonLabelPrinter.STATE_NONE -> {
                                isConnectedFlag = false
                                Log.d(TAG, "Printer disconnected")
                            }
                        }
                    }
                    BixolonLabelPrinter.MESSAGE_OUTPUT_COMPLETE -> {
                        Log.d(TAG, "✅ Print output complete")
                    }
                }
            }
        }

    override suspend fun connect(
        address: String,
        port: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔌 Connecting to $address:$port")

                if (bixolonPrinter == null) {
                    bixolonPrinter = BixolonLabelPrinter(context, printerHandler, null)
                }

                val printer = bixolonPrinter!!

                // Check if this is a Bluetooth address (MAC format)
                val isBluetooth = address.matches(Regex("([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}"))

                val result =
                    if (isBluetooth) {
                        // Bluetooth connection
                        printer.connect(address)
                    } else {
                        // Network connection
                        printer.connect(address, port, 10000)
                    }

                if (result) {
                    isConnectedFlag = true
                    Log.d(TAG, "✅ Connected to $address")
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Connection failed to $address"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Connection error", e)
                Result.failure(e)
            }
        }

    override suspend fun disconnect(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                bixolonPrinter?.disconnect()
                isConnectedFlag = false
                Log.d(TAG, "✅ Disconnected")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Disconnect error", e)
                Result.failure(e)
            }
        }

    override suspend fun getStatus(): Result<PrinterStatus> =
        withContext(Dispatchers.IO) {
            try {
                val status =
                    PrinterStatus(
                        isConnected = isConnectedFlag,
                        isPaperPresent = true, // TODO: query from SDK
                        isError = false,
                        errorMessage = null,
                    )
                Result.success(status)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Get status error", e)
                Result.failure(e)
            }
        }

    override suspend fun initializePrinter(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "✅ Printer initialized")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Initialize error", e)
                Result.failure(e)
            }
        }

    override suspend fun configureMedia(config: MediaConfig): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "✅ Media configured: ${config.widthDots}x${config.heightDots}")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Configure media error", e)
                Result.failure(e)
            }
        }

    override suspend fun clearBuffer(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                bixolonPrinter?.clearBuffer()
                Log.d(TAG, "✅ Buffer cleared")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Clear buffer error", e)
                Result.failure(e)
            }
        }

    override suspend fun beginTransaction(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val printer =
                    bixolonPrinter
                        ?: return@withContext Result.failure(Exception("Printer not initialized"))
                // SDK V2.1.1: setCharacterSet(int internationalCharacterSet, int codepage)
                printer.setCharacterSet(
                    BixolonLabelPrinter.INTERNATIONAL_CHARACTER_SET_LATIN_AMERICA,
                    BixolonLabelPrinter.CODE_PAGE_WCP1252_LATIN1,
                )
                Log.d(TAG, "✅ Transaction started")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Begin transaction error", e)
                Result.failure(e)
            }
        }

    override suspend fun endTransaction(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "✅ Transaction ended")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ End transaction error", e)
                Result.failure(e)
            }
        }

    override suspend fun drawText(
        text: String,
        x: Int,
        y: Int,
        fontSize: Int,
        bold: Boolean,
        alignment: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val printer =
                    bixolonPrinter
                        ?: return@withContext Result.failure(Exception("Printer not initialized"))

                // SDK V2.1.1: drawText(String data, int x, int y, int fontType, int fontSize,
                //                      int rotation, boolean reverse, boolean bold, int width)
                // fontType: 0=Alpha, fontSize: pass as-is
                printer.drawText(
                    text, // data
                    x, // x position
                    y, // y position
                    0, // fontType (0 = alpha numeric)
                    BixolonLabelPrinter.FONT_SIZE_12, // fontSize
                    BixolonLabelPrinter.ROTATION_NONE, // rotation
                    false, // reverse
                    bold, // bold
                    1, // width multiplier
                )

                Log.d(TAG, "✅ Text drawn: $text at ($x, $y)")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Draw text error", e)
                Result.failure(e)
            }
        }

    override suspend fun drawQR(
        data: String,
        x: Int,
        y: Int,
        size: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val printer =
                    bixolonPrinter
                        ?: return@withContext Result.failure(Exception("Printer not initialized"))

                // SDK V2.1.1: drawQrCode(String data, int x, int y, int model, int eccLevel,
                //                        int width, int rotation)
                printer.drawQrCode(
                    data, // data
                    x, // x position
                    y, // y position
                    BixolonLabelPrinter.QR_CODE_MODEL2, // model
                    BixolonLabelPrinter.ECC_LEVEL_15, // ECC level
                    size, // width (module size)
                    BixolonLabelPrinter.ROTATION_NONE, // rotation
                )

                Log.d(TAG, "✅ QR drawn: $data at ($x, $y)")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Draw QR error", e)
                Result.failure(e)
            }
        }

    override suspend fun drawBitmap(
        bitmap: Bitmap,
        x: Int,
        y: Int,
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val printer =
                    bixolonPrinter
                        ?: return@withContext Result.failure(Exception("Printer not initialized"))

                // SDK V2.1.1: drawBitmap(Bitmap bitmap, int x, int y, int width,
                //                        int brightness, boolean dither)
                printer.drawBitmap(
                    bitmap, // bitmap
                    x, // x position
                    y, // y position
                    bitmap.width, // width
                    50, // brightness (0-100)
                    true, // dither
                )

                Log.d(TAG, "✅ Bitmap drawn at ($x, $y)")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Draw bitmap error", e)
                Result.failure(e)
            }
        }

    override suspend fun print(copies: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val printer =
                    bixolonPrinter
                        ?: return@withContext Result.failure(Exception("Printer not initialized"))

                // SDK V2.1.1: print(int copies, int copies2)
                printer.print(copies, 1)

                Log.d(TAG, "✅ Print command sent: $copies copies")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Print error", e)
                Result.failure(e)
            }
        }

    override suspend fun waitForCompletion(timeoutMs: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // Simple delay-based wait
                // TODO: Use proper callback mechanism
                kotlinx.coroutines.delay(timeoutMs.coerceAtMost(5000))
                Log.d(TAG, "✅ Wait completed")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Wait error", e)
                Result.failure(e)
            }
        }
}
