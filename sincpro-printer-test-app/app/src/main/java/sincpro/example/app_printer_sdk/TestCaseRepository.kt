package sincpro.example.app_printer_sdk

import android.content.Context
import android.util.Base64
import com.sincpro.printer.SincproPrinterSdk
import com.sincpro.printer.domain.*

class TestCaseRepository(
    private val sdk: SincproPrinterSdk,
    private val context: Context
) {

    fun getAllTestCases(): List<TestCase> = buildList {
        addAll(connectivityTests())
        addAll(printTextTests())
        addAll(printQrTests())
        addAll(printBarcodeTests())
        addAll(printReceiptTests())
        addAll(printBinaryTests())
        addAll(errorHandlingTests())
        addAll(configurationTests())
    }

    private fun connectivityTests() = listOf(
        TestCase(
            id = "conn_01",
            name = "Get Paired Devices",
            description = "List all paired Bluetooth devices",
            category = TestCategory.CONNECTIVITY,
            requiresConnection = false
        ) {
            sdk.bixolon.connectivity.getPairedDevices()
                .map { devices -> 
                    println("Found ${devices.size} devices")
                    devices.forEach { println("  - ${it.name}: ${it.address}") }
                }
        },

        TestCase(
            id = "conn_02",
            name = "Get Paired Printers",
            description = "List only paired printer devices",
            category = TestCategory.CONNECTIVITY,
            requiresConnection = false
        ) {
            sdk.bixolon.connectivity.getPairedPrinters()
                .map { printers -> 
                    println("Found ${printers.size} printers")
                    printers.forEach { println("  - ${it.name}: ${it.address}") }
                }
        },

        TestCase(
            id = "conn_03",
            name = "Check Connection Status",
            description = "Verify isConnected() returns correct state",
            category = TestCategory.CONNECTIVITY,
            requiresConnection = false
        ) {
            val connected = sdk.bixolon.connectivity.isConnected()
            println("Connection status: $connected")
            Result.success(Unit)
        },

        TestCase(
            id = "conn_04",
            name = "Get Printer Status",
            description = "Get paper and error status from printer",
            category = TestCategory.CONNECTIVITY
        ) {
            sdk.bixolon.connectivity.getStatus()
                .map { status ->
                    println("Connected: ${status.isConnected}")
                    println("Has Paper: ${status.hasPaper}")
                    println("Has Error: ${status.hasError}")
                }
        },

        TestCase(
            id = "conn_05",
            name = "Get Printer Info",
            description = "Get model, firmware, serial, DPI",
            category = TestCategory.CONNECTIVITY
        ) {
            sdk.bixolon.connectivity.getInfo()
                .map { info ->
                    println("Model: ${info.model}")
                    println("Firmware: ${info.firmware}")
                    println("Serial: ${info.serialNumber}")
                    println("DPI: ${info.dpi}")
                }
        },

        TestCase(
            id = "conn_06",
            name = "Disconnect",
            description = "Disconnect from printer",
            category = TestCategory.CONNECTIVITY
        ) {
            sdk.bixolon.connectivity.disconnect()
        }
    )

    private fun printTextTests() = listOf(
        TestCase(
            id = "text_01",
            name = "Print Simple Text",
            description = "Print 'Hello World' with default settings",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText("Hello World - SDK Test")
        },

        TestCase(
            id = "text_02",
            name = "Print Text - Left Aligned",
            description = "Print text aligned to left",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText(
                text = "LEFT ALIGNED TEXT",
                alignment = Alignment.LEFT
            )
        },

        TestCase(
            id = "text_03",
            name = "Print Text - Center Aligned",
            description = "Print text centered",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText(
                text = "CENTER ALIGNED",
                alignment = Alignment.CENTER
            )
        },

        TestCase(
            id = "text_04",
            name = "Print Text - Right Aligned",
            description = "Print text aligned to right",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText(
                text = "RIGHT ALIGNED",
                alignment = Alignment.RIGHT
            )
        },

        TestCase(
            id = "text_05",
            name = "Print Text - Small Font",
            description = "Print with SMALL font size",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText(
                text = "Small Font Text",
                fontSize = FontSize.SMALL
            )
        },

        TestCase(
            id = "text_06",
            name = "Print Text - Large Font",
            description = "Print with LARGE font size",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText(
                text = "Large Font",
                fontSize = FontSize.LARGE
            )
        },

        TestCase(
            id = "text_07",
            name = "Print Text - XLarge Font",
            description = "Print with XLARGE font size",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText(
                text = "XLarge",
                fontSize = FontSize.XLARGE
            )
        },

        TestCase(
            id = "text_08",
            name = "Print Text - Bold",
            description = "Print bold text",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printText(
                text = "BOLD TEXT",
                bold = true
            )
        },

        TestCase(
            id = "text_09",
            name = "Print Multiple Lines",
            description = "Print list of strings",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printTexts(
                texts = listOf(
                    "Line 1: First item",
                    "Line 2: Second item",
                    "Line 3: Third item",
                    "Line 4: Fourth item"
                )
            )
        },

        TestCase(
            id = "text_10",
            name = "Print Key-Value",
            description = "Print key-value pair (label: value)",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printKeyValue(
                key = "Total:",
                value = "$99.99"
            )
        },

        TestCase(
            id = "text_11",
            name = "Print Key-Value Bold",
            description = "Print bold key-value pair",
            category = TestCategory.PRINT_TEXT
        ) {
            sdk.bixolon.print.printKeyValue(
                key = "TOTAL:",
                value = "$199.99",
                bold = true
            )
        }
    )

    private fun printQrTests() = listOf(
        TestCase(
            id = "qr_01",
            name = "Print QR - URL",
            description = "Print QR code with URL",
            category = TestCategory.PRINT_QR
        ) {
            sdk.bixolon.print.printQR(
                data = "https://sincpro.com",
                size = 5
            )
        },

        TestCase(
            id = "qr_02",
            name = "Print QR - Small",
            description = "Print small QR code (size=3)",
            category = TestCategory.PRINT_QR
        ) {
            sdk.bixolon.print.printQR(
                data = "SMALL-QR-TEST",
                size = 3
            )
        },

        TestCase(
            id = "qr_03",
            name = "Print QR - Large",
            description = "Print large QR code (size=8)",
            category = TestCategory.PRINT_QR
        ) {
            sdk.bixolon.print.printQR(
                data = "LARGE-QR-TEST",
                size = 8
            )
        },

        TestCase(
            id = "qr_04",
            name = "Print QR - Left Aligned",
            description = "Print QR aligned to left",
            category = TestCategory.PRINT_QR
        ) {
            sdk.bixolon.print.printQR(
                data = "LEFT-QR",
                size = 4,
                alignment = Alignment.LEFT
            )
        },

        TestCase(
            id = "qr_05",
            name = "Print QR - Right Aligned",
            description = "Print QR aligned to right",
            category = TestCategory.PRINT_QR
        ) {
            sdk.bixolon.print.printQR(
                data = "RIGHT-QR",
                size = 4,
                alignment = Alignment.RIGHT
            )
        }
    )

    private fun printBarcodeTests() = listOf(
        TestCase(
            id = "barcode_01",
            name = "Print Barcode - CODE128",
            description = "Print CODE128 barcode",
            category = TestCategory.PRINT_BARCODE
        ) {
            sdk.bixolon.print.printBarcode(
                data = "ABC123456",
                type = BarcodeType.CODE128
            )
        },

        TestCase(
            id = "barcode_02",
            name = "Print Barcode - CODE39",
            description = "Print CODE39 barcode",
            category = TestCategory.PRINT_BARCODE
        ) {
            sdk.bixolon.print.printBarcode(
                data = "CODE39TEST",
                type = BarcodeType.CODE39
            )
        },

        TestCase(
            id = "barcode_03",
            name = "Print Barcode - EAN13",
            description = "Print EAN13 barcode (13 digits)",
            category = TestCategory.PRINT_BARCODE
        ) {
            sdk.bixolon.print.printBarcode(
                data = "5901234123457",
                type = BarcodeType.EAN13
            )
        },

        TestCase(
            id = "barcode_04",
            name = "Print Barcode - Tall",
            description = "Print tall barcode (height=100)",
            category = TestCategory.PRINT_BARCODE
        ) {
            sdk.bixolon.print.printBarcode(
                data = "TALL123",
                type = BarcodeType.CODE128,
                height = 100
            )
        },

        TestCase(
            id = "barcode_05",
            name = "Print Barcode - Short",
            description = "Print short barcode (height=30)",
            category = TestCategory.PRINT_BARCODE
        ) {
            sdk.bixolon.print.printBarcode(
                data = "SHORT123",
                type = BarcodeType.CODE128,
                height = 30
            )
        }
    )

    private fun printReceiptTests() = listOf(
        TestCase(
            id = "receipt_01",
            name = "Print Simple Receipt",
            description = "Print basic receipt with header/body/footer",
            category = TestCategory.PRINT_RECEIPT
        ) {
            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Text("SINCPRO STORE", FontSize.LARGE, true, Alignment.CENTER),
                    ReceiptLine.Text("Test Receipt", FontSize.MEDIUM, false, Alignment.CENTER),
                    ReceiptLine.Separator()
                ),
                body = listOf(
                    ReceiptLine.KeyValue("Item 1", "$10.00"),
                    ReceiptLine.KeyValue("Item 2", "$15.00"),
                    ReceiptLine.KeyValue("Item 3", "$25.00"),
                    ReceiptLine.Separator('-'),
                    ReceiptLine.KeyValue("TOTAL", "$50.00", FontSize.LARGE, true)
                ),
                footer = listOf(
                    ReceiptLine.Space(1),
                    ReceiptLine.Text("Thank you!", FontSize.MEDIUM, false, Alignment.CENTER),
                    ReceiptLine.Space(2)
                )
            )
            sdk.bixolon.print.printReceipt(receipt)
        },

        TestCase(
            id = "receipt_02",
            name = "Print Receipt with QR",
            description = "Print receipt including QR code",
            category = TestCategory.PRINT_RECEIPT
        ) {
            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Text("RECEIPT WITH QR", FontSize.LARGE, true, Alignment.CENTER),
                    ReceiptLine.Separator()
                ),
                body = listOf(
                    ReceiptLine.KeyValue("Order", "#12345"),
                    ReceiptLine.KeyValue("Total", "$99.99"),
                    ReceiptLine.Space(1),
                    ReceiptLine.QR("https://sincpro.com/order/12345", 5, Alignment.CENTER)
                ),
                footer = listOf(
                    ReceiptLine.Space(1),
                    ReceiptLine.Text("Scan for details", FontSize.SMALL, false, Alignment.CENTER),
                    ReceiptLine.Space(2)
                )
            )
            sdk.bixolon.print.printReceipt(receipt)
        },

        TestCase(
            id = "receipt_03",
            name = "Print Receipt with Barcode",
            description = "Print receipt including barcode",
            category = TestCategory.PRINT_RECEIPT
        ) {
            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Text("RECEIPT WITH BARCODE", FontSize.LARGE, true, Alignment.CENTER),
                    ReceiptLine.Separator()
                ),
                body = listOf(
                    ReceiptLine.KeyValue("Product", "Widget Pro"),
                    ReceiptLine.KeyValue("Price", "$29.99"),
                    ReceiptLine.Space(1),
                    ReceiptLine.Barcode("1234567890", BarcodeType.CODE128, 2, 60, Alignment.CENTER)
                ),
                footer = listOf(
                    ReceiptLine.Space(2)
                )
            )
            sdk.bixolon.print.printReceipt(receipt)
        },

        TestCase(
            id = "receipt_04",
            name = "Print Long Receipt",
            description = "Print receipt with many items",
            category = TestCategory.PRINT_RECEIPT
        ) {
            val items = (1..10).map { i ->
                ReceiptLine.KeyValue("Item $i", "$${i * 5}.00")
            }
            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Text("LONG RECEIPT TEST", FontSize.LARGE, true, Alignment.CENTER),
                    ReceiptLine.Text("10 Items", FontSize.SMALL, false, Alignment.CENTER),
                    ReceiptLine.Separator()
                ),
                body = items + listOf(
                    ReceiptLine.Separator('='),
                    ReceiptLine.KeyValue("TOTAL", "$275.00", FontSize.LARGE, true)
                ),
                footer = listOf(
                    ReceiptLine.Space(1),
                    ReceiptLine.Text("Thank you for shopping!", FontSize.MEDIUM, false, Alignment.CENTER),
                    ReceiptLine.Space(3)
                )
            )
            sdk.bixolon.print.printReceipt(receipt)
        },

        TestCase(
            id = "receipt_05",
            name = "Print Multiple Copies",
            description = "Print receipt with 2 copies",
            category = TestCategory.PRINT_RECEIPT
        ) {
            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Text("COPY TEST", FontSize.LARGE, true, Alignment.CENTER),
                    ReceiptLine.Separator()
                ),
                body = listOf(
                    ReceiptLine.Text("This should print 2 copies", FontSize.MEDIUM, false, Alignment.CENTER)
                ),
                footer = listOf(
                    ReceiptLine.Space(2)
                )
            )
            sdk.bixolon.print.printReceipt(receipt, copies = 2)
        }
    )

    private fun errorHandlingTests() = listOf(
        TestCase(
            id = "error_01",
            name = "Print Without Connection",
            description = "Try to print when disconnected (should fail)",
            category = TestCategory.ERROR_HANDLING,
            requiresConnection = false
        ) {
            if (sdk.bixolon.connectivity.isConnected()) {
                Result.failure(Exception("Test skipped: Already connected"))
            } else {
                val result = sdk.bixolon.print.printText("This should fail")
                if (result.isFailure) {
                    println("Expected error: ${result.exceptionOrNull()?.message}")
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Expected failure but got success"))
                }
            }
        },

        TestCase(
            id = "error_02",
            name = "Connect Invalid Address",
            description = "Try connecting to invalid MAC address",
            category = TestCategory.ERROR_HANDLING,
            requiresConnection = false
        ) {
            val result = sdk.bixolon.connectivity.connectBluetooth(
                address = "00:00:00:00:00:00",
                timeoutMs = 3000
            )
            if (result.isFailure) {
                println("Expected error: ${result.exceptionOrNull()?.message}")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Expected connection failure"))
            }
        },

        TestCase(
            id = "error_03",
            name = "Print Empty Text",
            description = "Print empty string",
            category = TestCategory.ERROR_HANDLING
        ) {
            sdk.bixolon.print.printText("")
        },

        TestCase(
            id = "error_04",
            name = "Print Very Long Text",
            description = "Print text that exceeds paper width",
            category = TestCategory.ERROR_HANDLING
        ) {
            sdk.bixolon.print.printText(
                "A".repeat(200)
            )
        },

        TestCase(
            id = "error_05",
            name = "Print Invalid Barcode Data",
            description = "Print EAN13 with wrong digit count",
            category = TestCategory.ERROR_HANDLING
        ) {
            sdk.bixolon.print.printBarcode(
                data = "123", // EAN13 requires 13 digits
                type = BarcodeType.EAN13
            )
        },

        TestCase(
            id = "error_06",
            name = "Double Disconnect",
            description = "Call disconnect twice",
            category = TestCategory.ERROR_HANDLING,
            requiresConnection = false
        ) {
            sdk.bixolon.connectivity.disconnect()
            sdk.bixolon.connectivity.disconnect()
        },

        TestCase(
            id = "error_07",
            name = "Print Special Characters",
            description = "Print text with special chars (UTF-8)",
            category = TestCategory.ERROR_HANDLING
        ) {
            sdk.bixolon.print.printText("Café ñoño €100 日本語")
        }
    )

    private fun configurationTests() = listOf(
        TestCase(
            id = "config_01",
            name = "Print on 58mm Paper",
            description = "Print using 58mm media config",
            category = TestCategory.CONFIGURATION
        ) {
            sdk.bixolon.print.printText(
                text = "58mm Paper Test",
                media = MediaConfig.continuous58mm()
            )
        },

        TestCase(
            id = "config_02",
            name = "Print on 80mm Paper",
            description = "Print using 80mm media config (default)",
            category = TestCategory.CONFIGURATION
        ) {
            sdk.bixolon.print.printText(
                text = "80mm Paper Test (Default)",
                media = MediaConfig.continuous80mm()
            )
        },

        TestCase(
            id = "config_03",
            name = "Get DPI",
            description = "Get printer DPI value",
            category = TestCategory.CONFIGURATION
        ) {
            val dpi = sdk.bixolon.connectivity.getDpi()
            println("Printer DPI: $dpi")
            Result.success(Unit)
        },

        TestCase(
            id = "config_04",
            name = "Print Image - Test Pattern",
            description = "Print a test pattern image",
            category = TestCategory.CONFIGURATION
        ) {
            // Create a simple test pattern bitmap
            val width = 200
            val height = 100
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565)
            val canvas = android.graphics.Canvas(bitmap)
            val paint = android.graphics.Paint()
            
            // Draw white background
            canvas.drawColor(android.graphics.Color.WHITE)
            
            // Draw black rectangle
            paint.color = android.graphics.Color.BLACK
            canvas.drawRect(10f, 10f, 190f, 90f, paint)
            
            // Draw white text area
            paint.color = android.graphics.Color.WHITE  
            canvas.drawRect(20f, 30f, 180f, 70f, paint)
            
            // Print using printImage API
            sdk.bixolon.print.printImage(bitmap, Alignment.CENTER)
        },

        TestCase(
            id = "config_05",
            name = "Print Image - Gradient",
            description = "Print gradient test pattern",
            category = TestCategory.CONFIGURATION
        ) {
            // Create a gradient test pattern
            val width = 300
            val height = 100
            val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.RGB_565)
            val canvas = android.graphics.Canvas(bitmap)
            
            // Draw gradient from white to black
            val paint = android.graphics.Paint()
            for (x in 0 until width) {
                val gray = 255 - (x * 255 / width)
                paint.color = android.graphics.Color.rgb(gray, gray, gray)
                canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), paint)
            }
            
            sdk.bixolon.print.printImage(bitmap, Alignment.LEFT)
        }
    )

    // ============================================================
    // Binary Tests (PNG/PDF from raw resources)
    // ============================================================
    
    private fun printBinaryTests() = listOf(
        // Test that decodes PNG manually like config_04/05 do (they work!)
        TestCase(
            id = "binary_00",
            name = "Print PNG - Manual Decode",
            description = "Decode PNG manually and draw on white canvas",
            category = TestCategory.PRINT_IMAGE
        ) {
            // Read raw bytes directly (not base64)
            val bytes = context.resources.openRawResource(R.raw.sincpro_simbolo).use { it.readBytes() }
            
            // Decode to bitmap
            val original = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (original == null) {
                return@TestCase Result.failure(Exception("Failed to decode PNG"))
            }
            
            android.util.Log.d("TEST", "Original: ${original.width}x${original.height}, config=${original.config}, hasAlpha=${original.hasAlpha()}")
            
            // Create RGB_565 bitmap with white background (like config_04 does)
            val printBitmap = android.graphics.Bitmap.createBitmap(
                original.width, 
                original.height, 
                android.graphics.Bitmap.Config.RGB_565
            )
            val canvas = android.graphics.Canvas(printBitmap)
            
            // WHITE background first!
            canvas.drawColor(android.graphics.Color.WHITE)
            
            // Draw original on top
            canvas.drawBitmap(original, 0f, 0f, null)
            
            original.recycle()
            
            android.util.Log.d("TEST", "Print bitmap: ${printBitmap.width}x${printBitmap.height}, config=${printBitmap.config}")
            
            // Print using printImage API
            sdk.bixolon.print.printImage(printBitmap, Alignment.CENTER)
        },

        TestCase(
            id = "binary_01",
            name = "Print PNG from Raw",
            description = "Print 'Sincpro Simbolo.png' from raw resources",
            category = TestCategory.PRINT_IMAGE
        ) {
            val base64 = readRawResourceAsBase64(R.raw.sincpro_simbolo)
            if (base64 != null) {
                println("PNG loaded, base64 length: ${base64.length}")
                sdk.bixolon.print.printImageBase64(
                    base64Data = base64,
                    alignment = Alignment.CENTER
                )
            } else {
                Result.failure(Exception("Failed to read PNG resource"))
            }
        },

        TestCase(
            id = "binary_02",
            name = "Print PNG - Left Aligned",
            description = "Print PNG image aligned to left",
            category = TestCategory.PRINT_IMAGE
        ) {
            val base64 = readRawResourceAsBase64(R.raw.sincpro_simbolo)
            if (base64 != null) {
                sdk.bixolon.print.printImageBase64(
                    base64Data = base64,
                    alignment = Alignment.LEFT
                )
            } else {
                Result.failure(Exception("Failed to read PNG resource"))
            }
        },

        TestCase(
            id = "binary_03",
            name = "Print PNG - Right Aligned",
            description = "Print PNG image aligned to right",
            category = TestCategory.PRINT_IMAGE
        ) {
            val base64 = readRawResourceAsBase64(R.raw.sincpro_simbolo)
            if (base64 != null) {
                sdk.bixolon.print.printImageBase64(
                    base64Data = base64,
                    alignment = Alignment.RIGHT
                )
            } else {
                Result.failure(Exception("Failed to read PNG resource"))
            }
        },

        TestCase(
            id = "binary_04",
            name = "Print PDF Page 1",
            description = "Print first page of 'comprobante' PDF",
            category = TestCategory.PRINT_PDF
        ) {
            val base64 = readRawResourceAsBase64(R.raw.comprobante_1767994755165)
            if (base64 != null) {
                println("PDF loaded, base64 length: ${base64.length}")
                
                // Get page count first
                val pageCount = sdk.bixolon.print.getPdfPageCount(base64)
                println("PDF has $pageCount pages")
                
                sdk.bixolon.print.printPdfBase64(
                    base64Data = base64,
                    page = 1
                )
            } else {
                Result.failure(Exception("Failed to read PDF resource"))
            }
        },

        TestCase(
            id = "binary_05",
            name = "Print PDF - All Pages",
            description = "Print all pages of PDF",
            category = TestCategory.PRINT_PDF
        ) {
            val base64 = readRawResourceAsBase64(R.raw.comprobante_1767994755165)
            if (base64 != null) {
                val pageCount = sdk.bixolon.print.getPdfPageCount(base64)
                println("Printing all $pageCount pages...")
                
                var lastResult: Result<Unit> = Result.success(Unit)
                for (page in 1..pageCount) {
                    println("Printing page $page/$pageCount")
                    lastResult = sdk.bixolon.print.printPdfBase64(
                        base64Data = base64,
                        page = page
                    )
                    if (lastResult.isFailure) break
                }
                lastResult
            } else {
                Result.failure(Exception("Failed to read PDF resource"))
            }
        },

        TestCase(
            id = "binary_06",
            name = "Print PDF - Page 2",
            description = "Print second page of PDF if exists",
            category = TestCategory.PRINT_PDF
        ) {
            val base64 = readRawResourceAsBase64(R.raw.comprobante_1767994755165)
            if (base64 != null) {
                val pageCount = sdk.bixolon.print.getPdfPageCount(base64)
                if (pageCount >= 2) {
                    sdk.bixolon.print.printPdfBase64(
                        base64Data = base64,
                        page = 2
                    )
                } else {
                    println("PDF only has $pageCount page(s), skipping page 2")
                    Result.success(Unit)
                }
            } else {
                Result.failure(Exception("Failed to read PDF resource"))
            }
        },

        TestCase(
            id = "binary_07",
            name = "Print PNG - 58mm Paper",
            description = "Print PNG on 58mm paper width",
            category = TestCategory.PRINT_IMAGE
        ) {
            val base64 = readRawResourceAsBase64(R.raw.sincpro_simbolo)
            if (base64 != null) {
                sdk.bixolon.print.printImageBase64(
                    base64Data = base64,
                    alignment = Alignment.CENTER,
                    media = MediaConfig.continuous58mm()
                )
            } else {
                Result.failure(Exception("Failed to read PNG resource"))
            }
        },

        // Diagnostic test
        TestCase(
            id = "binary_diag_01",
            name = "DIAGNOSTIC: PDF File Check",
            description = "Check PDF file creation and reading",
            category = TestCategory.PRINT_PDF
        ) {
            val base64 = readRawResourceAsBase64(R.raw.comprobante_1767994755165)
            if (base64 != null) {
                println("=== PDF DIAGNOSTIC ===")
                println("Base64 length: ${base64.length}")
                println("Base64 starts with: ${base64.take(50)}")
                
                // Decode to bytes
                val bytes = Base64.decode(base64, Base64.NO_WRAP)
                println("Decoded bytes length: ${bytes.size}")
                println("First 10 bytes: ${bytes.take(10).map { it.toInt() and 0xFF }}")
                
                // Check PDF header (should be %PDF)
                val header = String(bytes.take(8).toByteArray())
                println("PDF Header: '$header'")
                val isPdf = header.startsWith("%PDF")
                println("Is valid PDF: $isPdf")
                
                // Try to get page count
                val pageCount = sdk.bixolon.print.getPdfPageCount(base64)
                println("Page count: $pageCount")
                
                if (isPdf && pageCount > 0) {
                    println("=== PDF looks valid! ===")
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("PDF validation failed: isPdf=$isPdf, pages=$pageCount"))
                }
            } else {
                Result.failure(Exception("Failed to read PDF resource"))
            }
        },

        TestCase(
            id = "binary_diag_02",
            name = "DIAGNOSTIC: Image Check",
            description = "Check image decoding",
            category = TestCategory.PRINT_IMAGE
        ) {
            val base64 = readRawResourceAsBase64(R.raw.sincpro_simbolo)
            if (base64 != null) {
                println("=== IMAGE DIAGNOSTIC ===")
                println("Base64 length: ${base64.length}")
                
                // Decode to bytes
                val bytes = Base64.decode(base64, Base64.NO_WRAP)
                println("Decoded bytes length: ${bytes.size}")
                
                // Check PNG header (should be 0x89 PNG)
                val header = bytes.take(8).map { it.toInt() and 0xFF }
                println("First 8 bytes: $header")
                val isPng = header[0] == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47
                println("Is PNG: $isPng")
                
                // Try to decode bitmap
                val options = android.graphics.BitmapFactory.Options().apply {
                    inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
                }
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                
                if (bitmap != null) {
                    println("Bitmap decoded successfully!")
                    println("Bitmap size: ${bitmap.width} x ${bitmap.height}")
                    println("Bitmap config: ${bitmap.config}")
                    println("Has alpha: ${bitmap.hasAlpha()}")
                    
                    // Check a few pixels
                    val centerPixel = bitmap.getPixel(bitmap.width / 2, bitmap.height / 2)
                    println("Center pixel (ARGB): ${Integer.toHexString(centerPixel)}")
                    
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to decode bitmap"))
                }
            } else {
                Result.failure(Exception("Failed to read PNG resource"))
            }
        }
    )

    // ============================================================
    // Helper: Read raw resource as Base64
    // ============================================================
    
    private fun readRawResourceAsBase64(resourceId: Int): String? {
        return try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                val bytes = inputStream.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            println("Error reading raw resource: ${e.message}")
            null
        }
    }
}
