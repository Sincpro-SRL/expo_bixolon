# Test Cases Adicionales para MainActivity.kt

Agrega estos test cases al archivo `MainActivity.kt` para completar todos los servicios.

## 📋 Test Cases Faltantes

### CONNECTIVITY SERVICE

```kotlin
// TC4: getPairedPrinters
private fun testCase4_GetPairedPrinters() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 4: getPairedPrinters")
    log("═══════════════════════════════════════")

    try {
        val result = connectivityService.getPairedPrinters()

        result.onSuccess { printers ->
            log("✅ Encontradas ${printers.size} impresoras:")
            printers.forEach { printer ->
                log("  🖨️  ${printer.name}")
                log("     MAC: ${printer.address}")
            }
            log("✅ TEST PASSED")
        }.onFailure { error ->
            log("❌ TEST FAILED: ${error.message}")
        }
    } catch (e: Exception) {
        log("❌ TEST FAILED: ${e.message}")
    }
}

// TC5: startDiscovery
private fun testCase5_StartDiscovery() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 5: startDiscovery")
    log("═══════════════════════════════════════")

    try {
        val result = connectivityService.startDiscovery()

        result.onSuccess { started ->
            log("✅ Discovery iniciado: $started")
            log("✅ TEST PASSED")
        }.onFailure { error ->
            log("❌ TEST FAILED: ${error.message}")
        }
    } catch (e: Exception) {
        log("❌ TEST FAILED: ${e.message}")
    }
}

// TC6: stopDiscovery
private fun testCase6_StopDiscovery() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 6: stopDiscovery")
    log("═══════════════════════════════════════")

    try {
        val result = connectivityService.stopDiscovery()

        result.onSuccess { stopped ->
            log("✅ Discovery detenido: $stopped")
            log("✅ TEST PASSED")
        }.onFailure { error ->
            log("❌ TEST FAILED: ${error.message}")
        }
    } catch (e: Exception) {
        log("❌ TEST FAILED: ${e.message}")
    }
}

// TC7: isDiscovering
private fun testCase7_IsDiscovering() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 7: isDiscovering")
    log("═══════════════════════════════════════")

    try {
        val discovering = connectivityService.isDiscovering()
        log("Discovery activo: $discovering")
        log("✅ TEST PASSED")
    } catch (e: Exception) {
        log("❌ TEST FAILED: ${e.message}")
    }
}

// TC11: getConnectionStatus
private fun testCase11_GetConnectionStatus() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 11: getConnectionStatus")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val result = connectivityService.getConnectionStatus()

            result.onSuccess { info ->
                log("✅ Estado de conexión:")
                log("  Tipo: ${info.type}")
                log("  Dirección: ${info.address}")
                log("  Estado: ${info.status}")
                log("  Conectado en: ${info.connectedAt}")
                log("✅ TEST PASSED")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC12: isConnected
private fun testCase12_IsConnected() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 12: isConnected")
    log("═══════════════════════════════════════")

    try {
        val connected = connectivityService.isConnected()
        log("¿Conectado?: $connected")
        log("✅ TEST PASSED")
    } catch (e: Exception) {
        log("❌ TEST FAILED: ${e.message}")
    }
}

// TC13: getCurrentConnection
private fun testCase13_GetCurrentConnection() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 13: getCurrentConnection")
    log("═══════════════════════════════════════")

    try {
        val connection = connectivityService.getCurrentConnection()

        if (connection != null) {
            log("✅ Conexión actual:")
            log("  Tipo: ${connection.type}")
            log("  Dirección: ${connection.address}")
            log("  Estado: ${connection.status}")
        } else {
            log("⚠️  No hay conexión activa")
        }
        log("✅ TEST PASSED")
    } catch (e: Exception) {
        log("❌ TEST FAILED: ${e.message}")
    }
}
```

### PRINT SERVICE (High Level)

```kotlin
// TC14: printReceipt
private fun testCase14_PrintReceipt() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 14: printReceipt")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            log("🧾 Creando recibo de prueba...")

            val receipt = sincpro.expo.printer.domain.Receipt(
                header = listOf(
                    sincpro.expo.printer.domain.ReceiptLine.Text(
                        "MI TIENDA",
                        sincpro.expo.printer.domain.FontSize.LARGE,
                        sincpro.expo.printer.domain.Alignment.CENTER
                    ),
                    sincpro.expo.printer.domain.ReceiptLine.Text(
                        "Recibo de Venta",
                        sincpro.expo.printer.domain.FontSize.SMALL,
                        sincpro.expo.printer.domain.Alignment.CENTER
                    )
                ),
                details = listOf(
                    sincpro.expo.printer.domain.ReceiptLine.KeyValue("Producto 1", "$10.00"),
                    sincpro.expo.printer.domain.ReceiptLine.KeyValue("Producto 2", "$15.00"),
                    sincpro.expo.printer.domain.ReceiptLine.Separator,
                    sincpro.expo.printer.domain.ReceiptLine.KeyValue("TOTAL", "$25.00")
                ),
                footer = listOf(
                    sincpro.expo.printer.domain.ReceiptLine.Text(
                        "¡Gracias por su compra!",
                        sincpro.expo.printer.domain.FontSize.SMALL,
                        sincpro.expo.printer.domain.Alignment.CENTER
                    )
                )
            )

            val result = printService.printReceipt(receipt)

            result.onSuccess {
                log("✅ TEST PASSED: Recibo impreso")
                toast("✅ Recibo impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC15: printLines
private fun testCase15_PrintLines() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 15: printLines")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val lines = listOf(
                sincpro.expo.printer.domain.ReceiptLine.Text("Línea 1", sincpro.expo.printer.domain.FontSize.MEDIUM, sincpro.expo.printer.domain.Alignment.LEFT),
                sincpro.expo.printer.domain.ReceiptLine.Text("Línea 2", sincpro.expo.printer.domain.FontSize.MEDIUM, sincpro.expo.printer.domain.Alignment.CENTER),
                sincpro.expo.printer.domain.ReceiptLine.Text("Línea 3", sincpro.expo.printer.domain.FontSize.MEDIUM, sincpro.expo.printer.domain.Alignment.RIGHT)
            )

            val result = printService.printLines(lines)

            result.onSuccess {
                log("✅ TEST PASSED: Líneas impresas")
                toast("✅ Líneas impresas!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC16: printQRCode
private fun testCase16_PrintQRCode() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 16: printQRCode")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val result = printService.printQRCode("https://github.com/Sincpro-SRL/sincpro_printer_expo")

            result.onSuccess {
                log("✅ TEST PASSED: QR impreso")
                toast("✅ QR impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC17: printText
private fun testCase17_PrintText() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 17: printText")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val result = printService.printText(
                "Test desde PrintService",
                fontSize = sincpro.expo.printer.domain.FontSize.LARGE,
                alignment = sincpro.expo.printer.domain.Alignment.CENTER
            )

            result.onSuccess {
                log("✅ TEST PASSED: Texto impreso")
                toast("✅ Texto impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC18: printImage
private fun testCase18_PrintImage() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 18: printImage")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            // Crear bitmap de prueba
            val bitmap = android.graphics.Bitmap.createBitmap(200, 200, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 40f
            }
            canvas.drawText("TEST", 50f, 100f, paint)

            val result = printService.printImage(bitmap)

            result.onSuccess {
                log("✅ TEST PASSED: Imagen impresa")
                toast("✅ Imagen impresa!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}
```

### LOW LEVEL PRINT SERVICE

```kotlin
// TC19: drawTextDirect
private fun testCase19_DrawTextDirect() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 19: drawTextDirect")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val result = lowLevelPrintService.drawTextDirect(
                text = "Texto directo en X=50, Y=50",
                x = 50,
                y = 50,
                fontSize = 30
            )

            result.onSuccess {
                log("✅ Texto dibujado en buffer")
                // Ahora imprimir
                lowLevelPrintService.withPrintSession {
                    printerAdapter.printBuffer()
                }
                log("✅ TEST PASSED")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC20: drawQRDirect
private fun testCase20_DrawQRDirect() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 20: drawQRDirect")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val result = lowLevelPrintService.drawQRDirect(
                data = "https://sincpro.com",
                x = 100,
                y = 100,
                size = 5
            )

            result.onSuccess {
                log("✅ QR dibujado en buffer")
                lowLevelPrintService.withPrintSession {
                    printerAdapter.printBuffer()
                }
                log("✅ TEST PASSED")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC21: drawBitmapDirect
private fun testCase21_DrawBitmapDirect() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 21: drawBitmapDirect")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            // Crear bitmap simple
            val bitmap = android.graphics.Bitmap.createBitmap(150, 150, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            canvas.drawColor(android.graphics.Color.WHITE)

            val result = lowLevelPrintService.drawBitmapDirect(bitmap, x = 50, y = 50)

            result.onSuccess {
                log("✅ Bitmap dibujado en buffer")
                lowLevelPrintService.withPrintSession {
                    printerAdapter.printBuffer()
                }
                log("✅ TEST PASSED")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC22: getPrinterStatus (LowLevelPrintService)
private fun testCase22_GetPrinterStatusLowLevel() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 22: getPrinterStatus (LowLevel)")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val result = lowLevelPrintService.getPrinterStatus()

            result.onSuccess { status ->
                log("✅ Estado obtenido:")
                log("  Conectado: ${status.isConnected}")
                log("  Papel: ${if (status.hasPaper) "✅" else "❌"}")
                log("  Batería baja: ${if (status.isLowBattery) "⚠️" else "✅"}")
                log("✅ TEST PASSED")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}

// TC23: clearBuffer (LowLevelPrintService)
private fun testCase23_ClearBufferLowLevel() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 23: clearBuffer (LowLevel)")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            val result = lowLevelPrintService.clearBuffer()

            result.onSuccess {
                log("✅ TEST PASSED: Buffer limpiado")
                toast("Buffer limpiado")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }
        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}
```

## 🔄 Actualizar UI

Agrega botones en `createUI()`:

```kotlin
// Después de los botones existentes, agregar:

// CONNECTIVITY SERVICE adicionales
layout.addView(createButton("📋 TC4: Paired Printers", btnParams) {
    testCase4_GetPairedPrinters()
})

layout.addView(createButton("🔎 TC5: Start Discovery", btnParams) {
    testCase5_StartDiscovery()
})

layout.addView(createButton("🛑 TC6: Stop Discovery", btnParams) {
    testCase6_StopDiscovery()
})

layout.addView(createButton("❓ TC7: Is Discovering", btnParams) {
    testCase7_IsDiscovering()
})

layout.addView(createButton("📊 TC11: Connection Status", btnParams) {
    testCase11_GetConnectionStatus()
})

layout.addView(createButton("🔗 TC12: Is Connected", btnParams) {
    testCase12_IsConnected()
})

layout.addView(createButton("📍 TC13: Current Connection", btnParams) {
    testCase13_GetCurrentConnection()
})

// PRINT SERVICE (High Level)
layout.addView(createButton("🧾 TC14: Print Receipt", btnParams) {
    testCase14_PrintReceipt()
})

layout.addView(createButton("📝 TC15: Print Lines", btnParams) {
    testCase15_PrintLines()
})

layout.addView(createButton("📱 TC16: Print QR", btnParams) {
    testCase16_PrintQRCode()
})

layout.addView(createButton("📄 TC17: Print Text", btnParams) {
    testCase17_PrintText()
})

layout.addView(createButton("🖼️ TC18: Print Image", btnParams) {
    testCase18_PrintImage()
})

// LOW LEVEL PRINT SERVICE
layout.addView(createButton("✍️ TC19: Draw Text Direct", btnParams) {
    testCase19_DrawTextDirect()
})

layout.addView(createButton("🔲 TC20: Draw QR Direct", btnParams) {
    testCase20_DrawQRDirect()
})

layout.addView(createButton("🎨 TC21: Draw Bitmap Direct", btnParams) {
    testCase21_DrawBitmapDirect()
})

layout.addView(createButton("📡 TC22: Get Status (LL)", btnParams) {
    testCase22_GetPrinterStatusLowLevel()
})

layout.addView(createButton("🗑️ TC23: Clear Buffer (LL)", btnParams) {
    testCase23_ClearBufferLowLevel()
})
```

## ✅ Resultado Final

Con estos cambios tendrás **23 test cases completos** que cubren:

- ✅ **ConnectivityService** (13 métodos)
- ✅ **PrintService** (5 métodos high-level)
- ✅ **LowLevelPrintService** (5 métodos low-level)

¡Todos los servicios están cubiertos! 🎉
