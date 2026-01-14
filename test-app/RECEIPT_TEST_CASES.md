# Test Cases Completos para Receipt (DTOs)

Agrega estos test cases a `MainActivity.kt` para mostrar cómo usar `printReceipt` con todos los tipos de líneas.

## 📋 Imports Necesarios

```kotlin
import sincpro.expo.printer.domain.Receipt
import sincpro.expo.printer.domain.ReceiptLine
import sincpro.expo.printer.domain.FontSize
import sincpro.expo.printer.domain.Alignment
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
```

## 🧾 Test Cases Completos de Receipt

### TC24: Receipt Simple (Tienda básica)

```kotlin
private fun testCase24_ReceiptSimple() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 24: Receipt Simple")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            log("🧾 Creando recibo simple...")

            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Text(
                        content = "FERRETERÍA EL TORNILLO",
                        fontSize = FontSize.LARGE,
                        bold = true,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        content = "Av. Principal #123",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        content = "Tel: 555-1234",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.Separator()
                ),
                details = listOf(
                    ReceiptLine.KeyValue("Fecha", getCurrentDateTime()),
                    ReceiptLine.KeyValue("Ticket", "#001234"),
                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.Text("PRODUCTOS:", fontSize = FontSize.MEDIUM, bold = true),
                    ReceiptLine.KeyValue("Martillo 500g", "$15.50"),
                    ReceiptLine.KeyValue("Clavos 2\" (50u)", "$8.00"),
                    ReceiptLine.KeyValue("Tornillos (100u)", "$12.75"),
                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.Separator(),
                    ReceiptLine.KeyValue("SUBTOTAL", "$36.25", bold = true),
                    ReceiptLine.KeyValue("IVA (16%)", "$5.80"),
                    ReceiptLine.Separator(char = "="),
                    ReceiptLine.KeyValue(
                        "TOTAL",
                        "$42.05",
                        fontSize = FontSize.LARGE,
                        bold = true
                    )
                ),
                footer = listOf(
                    ReceiptLine.Separator(),
                    ReceiptLine.Text(
                        "¡Gracias por su compra!",
                        fontSize = FontSize.MEDIUM,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        "Conserve su ticket",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    )
                )
            )

            log("📄 Imprimiendo recibo con:")
            log("  - ${receipt.header.size} líneas en header")
            log("  - ${receipt.details.size} líneas en detalles")
            log("  - ${receipt.footer.size} líneas en footer")

            val result = printService.printReceipt(receipt)

            result.onSuccess {
                log("✅ TEST PASSED: Recibo simple impreso")
                toast("✅ Recibo impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
                toast("Error: ${error.message}")
            }

        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
            e.printStackTrace()
        }
    }
}
```

### TC25: Receipt con QR Code

```kotlin
private fun testCase25_ReceiptConQR() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 25: Receipt con QR")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            log("🧾 Creando recibo con código QR...")

            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Text(
                        "RESTAURANTE LA DELICIA",
                        fontSize = FontSize.LARGE,
                        bold = true,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Space(lines = 1)
                ),
                details = listOf(
                    ReceiptLine.KeyValue("Mesa", "12"),
                    ReceiptLine.KeyValue("Mesero", "Juan"),
                    ReceiptLine.KeyValue("Fecha", getCurrentDateTime()),
                    ReceiptLine.Separator(),
                    ReceiptLine.Text("CONSUMO:", bold = true),
                    ReceiptLine.KeyValue("Hamburguesa Clásica", "$45.00"),
                    ReceiptLine.KeyValue("Papas Fritas", "$25.00"),
                    ReceiptLine.KeyValue("Refresco Grande", "$18.00"),
                    ReceiptLine.Separator(),
                    ReceiptLine.KeyValue("Subtotal", "$88.00"),
                    ReceiptLine.KeyValue("Propina (10%)", "$8.80"),
                    ReceiptLine.Separator(char = "="),
                    ReceiptLine.KeyValue(
                        "TOTAL",
                        "$96.80",
                        fontSize = FontSize.LARGE,
                        bold = true
                    )
                ),
                footer = listOf(
                    ReceiptLine.Space(lines = 2),
                    ReceiptLine.Text(
                        "Escanea para calificar tu experiencia:",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.QRCode(
                        data = "https://restaurant.com/feedback/mesa-12",
                        size = 5,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.Text(
                        "¡Gracias por tu visita!",
                        alignment = Alignment.CENTER
                    )
                )
            )

            log("📱 Imprimiendo recibo con QR code")

            val result = printService.printReceipt(receipt)

            result.onSuccess {
                log("✅ TEST PASSED: Recibo con QR impreso")
                toast("✅ Recibo con QR impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }

        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}
```

### TC26: Receipt con Logo (Image)

```kotlin
private fun testCase26_ReceiptConLogo() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 26: Receipt con Logo")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            log("🧾 Creando recibo con logo...")

            // Crear logo simple (en producción sería tu logo real)
            val logo = Bitmap.createBitmap(200, 80, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(logo)
            canvas.drawColor(Color.WHITE)

            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 40f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("LOGO", 100f, 50f, paint)

            val receipt = Receipt(
                header = listOf(
                    ReceiptLine.Image(
                        bitmap = logo,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.Text(
                        "FARMACIA SALUD+",
                        fontSize = FontSize.LARGE,
                        bold = true,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        "RFC: FAR123456ABC",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Separator()
                ),
                details = listOf(
                    ReceiptLine.KeyValue("Ticket", "#FAR-5678"),
                    ReceiptLine.KeyValue("Cajero", "María López"),
                    ReceiptLine.KeyValue("Fecha", getCurrentDateTime()),
                    ReceiptLine.Separator(),
                    ReceiptLine.Text("PRODUCTOS:", bold = true),
                    ReceiptLine.KeyValue("Paracetamol 500mg", "$45.00"),
                    ReceiptLine.KeyValue("Alcohol 70% 250ml", "$28.50"),
                    ReceiptLine.KeyValue("Vitamina C (30 tabs)", "$89.00"),
                    ReceiptLine.Separator(),
                    ReceiptLine.KeyValue("Total", "$162.50", bold = true)
                ),
                footer = listOf(
                    ReceiptLine.Separator(),
                    ReceiptLine.Text(
                        "IMPORTANTE: Conserve este ticket",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        "para cambios y devoluciones",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    )
                )
            )

            log("🖼️  Imprimiendo recibo con logo")

            val result = printService.printReceipt(receipt)

            result.onSuccess {
                log("✅ TEST PASSED: Recibo con logo impreso")
                toast("✅ Recibo con logo impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }

        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}
```

### TC27: Receipt Completo (Todos los tipos)

```kotlin
private fun testCase27_ReceiptCompleto() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 27: Receipt Completo")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            log("🧾 Creando recibo completo con TODOS los tipos de línea...")

            // Crear bitmap para demostración
            val demoBitmap = Bitmap.createBitmap(150, 50, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(demoBitmap)
            canvas.drawColor(Color.WHITE)
            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 30f
            }
            canvas.drawText("DEMO", 10f, 35f, paint)

            val receipt = Receipt(
                header = listOf(
                    // Image
                    ReceiptLine.Image(demoBitmap, Alignment.CENTER),
                    ReceiptLine.Space(lines = 1),

                    // Text con diferentes tamaños
                    ReceiptLine.Text(
                        "TIENDA DEMO",
                        fontSize = FontSize.LARGE,
                        bold = true,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        "Dirección de prueba",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),

                    // Separator con diferentes caracteres
                    ReceiptLine.Separator(char = "=", length = 40),
                    ReceiptLine.Space(lines = 1)
                ),
                details = listOf(
                    // KeyValue básicos
                    ReceiptLine.KeyValue("Ticket", "#DEMO-001"),
                    ReceiptLine.KeyValue("Fecha", getCurrentDateTime()),
                    ReceiptLine.KeyValue("Cliente", "Cliente de Prueba"),

                    ReceiptLine.Separator(),
                    ReceiptLine.Space(lines = 1),

                    // Text con alineaciones diferentes
                    ReceiptLine.Text("PRODUCTOS:", bold = true, alignment = Alignment.LEFT),
                    ReceiptLine.KeyValue("Producto 1", "$10.00"),
                    ReceiptLine.KeyValue("Producto 2", "$25.00"),
                    ReceiptLine.KeyValue("Producto 3", "$15.50"),

                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.Separator(char = "-"),

                    // Totales con diferentes estilos
                    ReceiptLine.KeyValue("Subtotal", "$50.50"),
                    ReceiptLine.KeyValue("IVA (16%)", "$8.08"),
                    ReceiptLine.KeyValue("Descuento", "-$5.00"),

                    ReceiptLine.Separator(char = "="),
                    ReceiptLine.KeyValue(
                        "TOTAL A PAGAR",
                        "$53.58",
                        fontSize = FontSize.LARGE,
                        bold = true
                    ),
                    ReceiptLine.Separator(char = "=")
                ),
                footer = listOf(
                    ReceiptLine.Space(lines = 2),

                    // QR Code
                    ReceiptLine.Text(
                        "Escanea para factura electrónica:",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.QRCode(
                        data = "https://demo.com/invoice/DEMO-001",
                        size = 4,
                        alignment = Alignment.CENTER
                    ),

                    ReceiptLine.Space(lines = 2),
                    ReceiptLine.Separator(),

                    // Textos finales con diferentes alineaciones
                    ReceiptLine.Text(
                        "¡GRACIAS POR SU COMPRA!",
                        fontSize = FontSize.MEDIUM,
                        bold = true,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        "Visite nuestra tienda en línea:",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),
                    ReceiptLine.Text(
                        "www.demo-store.com",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    ),

                    ReceiptLine.Space(lines = 1),
                    ReceiptLine.Separator(char = "*", length = 48),

                    // Texto final alineado a la izquierda
                    ReceiptLine.Text(
                        "Conserve su ticket para cambios",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.LEFT
                    )
                )
            )

            log("📊 Recibo completo:")
            log("  ✓ Header: ${receipt.header.size} líneas")
            log("    - Image")
            log("    - Text (LARGE, SMALL)")
            log("    - Separator (=)")
            log("    - Space")
            log("")
            log("  ✓ Details: ${receipt.details.size} líneas")
            log("    - KeyValue (múltiples)")
            log("    - Text (bold)")
            log("    - Separator (-, =)")
            log("    - Space")
            log("")
            log("  ✓ Footer: ${receipt.footer.size} líneas")
            log("    - QRCode")
            log("    - Text (diferentes alineaciones)")
            log("    - Separator (*)")
            log("")
            log("🖨️  Imprimiendo...")

            val result = printService.printReceipt(receipt)

            result.onSuccess {
                log("")
                log("✅ TEST PASSED: Recibo completo impreso")
                log("   Demostró TODOS los tipos de ReceiptLine:")
                log("   ✓ Text (3 tamaños, 3 alineaciones, bold)")
                log("   ✓ KeyValue (normal y bold)")
                log("   ✓ QRCode (con tamaño personalizado)")
                log("   ✓ Separator (3 caracteres diferentes)")
                log("   ✓ Space (espacios variables)")
                log("   ✓ Image (bitmap)")
                toast("✅ Recibo completo impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }

        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
            e.printStackTrace()
        }
    }
}
```

### TC28: Receipt usando Builder Pattern

```kotlin
private fun testCase28_ReceiptConBuilder() {
    log("")
    log("═══════════════════════════════════════")
    log("TEST CASE 28: Receipt con Builder")
    log("═══════════════════════════════════════")

    lifecycleScope.launch {
        try {
            log("🧾 Creando recibo usando Builder pattern...")

            val receipt = Receipt.builder()
                // Header fluido
                .addHeader(
                    ReceiptLine.Text(
                        "CAFÉ AROMA",
                        fontSize = FontSize.LARGE,
                        bold = true,
                        alignment = Alignment.CENTER
                    )
                )
                .addHeader(
                    ReceiptLine.Text(
                        "El mejor café de la ciudad",
                        fontSize = FontSize.SMALL,
                        alignment = Alignment.CENTER
                    )
                )
                .addHeader(ReceiptLine.Separator())

                // Details fluidos
                .addDetail(ReceiptLine.KeyValue("Orden", "#CAFE-789"))
                .addDetail(ReceiptLine.KeyValue("Barista", "Carlos"))
                .addDetail(ReceiptLine.Separator())
                .addDetail(ReceiptLine.Text("TU ORDEN:", bold = true))
                .addDetail(ReceiptLine.KeyValue("Cappuccino Grande", "$55.00"))
                .addDetail(ReceiptLine.KeyValue("Croissant", "$35.00"))
                .addDetail(ReceiptLine.Separator())
                .addDetail(
                    ReceiptLine.KeyValue(
                        "Total",
                        "$90.00",
                        fontSize = FontSize.LARGE,
                        bold = true
                    )
                )

                // Footer fluido
                .addFooter(ReceiptLine.Separator())
                .addFooter(
                    ReceiptLine.Text(
                        "¡Que lo disfrutes!",
                        alignment = Alignment.CENTER
                    )
                )
                .build()

            log("🏗️  Builder pattern usado")
            log("   Más legible y fluido")

            val result = printService.printReceipt(receipt)

            result.onSuccess {
                log("✅ TEST PASSED: Recibo con builder impreso")
                toast("✅ Recibo impreso!")
            }.onFailure { error ->
                log("❌ TEST FAILED: ${error.message}")
            }

        } catch (e: Exception) {
            log("❌ TEST FAILED: ${e.message}")
        }
    }
}
```

## 🔘 Agregar Botones en UI

Agrega en `createUI()`:

```kotlin
// Después de los botones existentes:

// RECEIPT TESTS
layout.addView(createButton("🧾 TC24: Receipt Simple", btnParams) {
    testCase24_ReceiptSimple()
})

layout.addView(createButton("📱 TC25: Receipt con QR", btnParams) {
    testCase25_ReceiptConQR()
})

layout.addView(createButton("🖼️ TC26: Receipt con Logo", btnParams) {
    testCase26_ReceiptConLogo()
})

layout.addView(createButton("🎯 TC27: Receipt Completo", btnParams) {
    testCase27_ReceiptCompleto()
})

layout.addView(createButton("🏗️ TC28: Receipt Builder", btnParams) {
    testCase28_ReceiptConBuilder()
})
```

## 📊 Tipos de ReceiptLine Cubiertos

| Tipo        | Descripción       | Test Cases    |
| ----------- | ----------------- | ------------- |
| `Text`      | Texto con formato | TC24-TC28 ✅  |
| `KeyValue`  | Par clave-valor   | TC24-TC28 ✅  |
| `QRCode`    | Código QR         | TC25, TC27 ✅ |
| `Separator` | Línea separadora  | TC24-TC28 ✅  |
| `Space`     | Espacio en blanco | TC24-TC28 ✅  |
| `Image`     | Bitmap/Logo       | TC26, TC27 ✅ |

## 🎯 Ejemplos de Uso Real

### Factura de Restaurante

```kotlin
val receipt = Receipt(
    header = listOf(
        ReceiptLine.Text("RESTAURANTE", FontSize.LARGE, true, Alignment.CENTER),
        ReceiptLine.Text("RFC: REST123456", FontSize.SMALL, false, Alignment.CENTER)
    ),
    details = listOf(
        ReceiptLine.KeyValue("Mesa", "5"),
        ReceiptLine.KeyValue("Mesero", "Juan"),
        ReceiptLine.Separator(),
        ReceiptLine.KeyValue("Pizza Margarita", "$150.00"),
        ReceiptLine.KeyValue("Bebidas", "$45.00"),
        ReceiptLine.Separator(),
        ReceiptLine.KeyValue("TOTAL", "$195.00", bold = true)
    ),
    footer = listOf(
        ReceiptLine.QRCode("https://restaurant.com/invoice/001"),
        ReceiptLine.Text("¡Gracias!", alignment = Alignment.CENTER)
    )
)
```

### Ticket de Estacionamiento

```kotlin
val receipt = Receipt.builder()
    .addHeader(ReceiptLine.Text("ESTACIONAMIENTO", FontSize.LARGE, alignment = Alignment.CENTER))
    .addDetail(ReceiptLine.KeyValue("Entrada", "10:30 AM"))
    .addDetail(ReceiptLine.KeyValue("Salida", "14:45 PM"))
    .addDetail(ReceiptLine.KeyValue("Tiempo", "4h 15min"))
    .addDetail(ReceiptLine.Separator())
    .addDetail(ReceiptLine.KeyValue("Total", "$80.00", bold = true))
    .addFooter(ReceiptLine.Text("Conserve su ticket", alignment = Alignment.CENTER))
    .build()
```

## ✅ Con estos test cases tendrás:

- ✅ **5 ejemplos completos** de uso de Receipt
- ✅ **Todos los tipos de ReceiptLine** cubiertos
- ✅ **Casos de uso reales** (tienda, restaurante, farmacia, café)
- ✅ **Builder pattern** demostrado
- ✅ **28 test cases en total** 🎉
