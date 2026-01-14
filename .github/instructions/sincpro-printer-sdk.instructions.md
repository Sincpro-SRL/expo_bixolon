# Sincpro Printer SDK - Development Instructions

## Overview

The **sincpro-printer-sdk** is a Kotlin SDK for thermal printer control following **Clean Architecture** with **4-layer structure**. This document explains how to extend, modify, and maintain the SDK.

**Location:** `/sincpro-printer-sdk/`

---

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│  Dependencies ALWAYS point inward (toward Domain)           │
└─────────────────────────────────────────────────────────────┘

                    ┌──────────────┐
                    │    Domain    │  ← Pure Kotlin, NO Android
                    │  (Contracts) │
                    └──────────────┘
                          ▲
            ┌─────────────┴─────────────┐
            │                           │
    ┌───────┴───────┐           ┌───────┴───────┐
    │    Adapter    │           │Infrastructure │
    │  (Vendors)    │           │  (Platform)   │
    └───────────────┘           └───────────────┘
            ▲                           ▲
            │                           │
            └───────────┬───────────────┘
                        │
                ┌───────┴───────┐
                │    Service    │
                │  (Use Cases)  │
                └───────────────┘
```

### Layer Responsibilities

| Layer              | Location          | Purpose                                     | Can Import                      |
| ------------------ | ----------------- | ------------------------------------------- | ------------------------------- |
| **Domain**         | `domain/`         | Interfaces, entities, exceptions            | Nothing (pure Kotlin)           |
| **Adapter**        | `adapter/`        | Vendor SDK implementations (Bixolon, Zebra) | Domain only                     |
| **Infrastructure** | `infrastructure/` | Platform utilities (Base64, Bluetooth)      | Domain only                     |
| **Service**        | `service/`        | Business logic, orchestration               | Domain, Adapter, Infrastructure |

---

## Package Structure

```
src/main/kotlin/com/sincpro/printer/
├── domain/                          # Layer 1: Contracts
│   ├── IPrinter.kt                  # Main printer interface
│   ├── PrinterTypes.kt              # Enums (Alignment, FontSize, etc.)
│   ├── MediaConfig.kt               # Paper/label configuration
│   ├── Receipt.kt                   # Receipt entity
│   └── Exceptions.kt                # Domain exceptions
│
├── adapter/                         # Layer 2: Vendor Implementations
│   └── BixolonPrinterAdapter.kt     # Bixolon SDK wrapper
│
├── infrastructure/                  # Layer 3: Platform Utilities
│   └── BinaryConverter.kt           # Base64 ↔ Bitmap/ByteArray
│
├── service/                         # Layer 4: Business Logic
│   └── bixolon/
│       ├── BixolonConnectivityService.kt
│       ├── BixolonPrintService.kt
│       ├── BixolonLowLevelService.kt
│       └── PrintSessionManager.kt
│
└── SincproPrinterSdk.kt             # Public API entry point
```

---

## Layer 1: Domain (Contracts)

**Location:** `domain/`

**Rules:**

- ✅ Pure Kotlin only (NO `android.*` imports)
- ✅ Define interfaces that adapters implement
- ✅ Define data classes for entities
- ✅ Define domain exceptions
- ❌ NO implementation logic
- ❌ NO external dependencies

### Creating a Domain Interface

```kotlin
// domain/IPrinter.kt
package com.sincpro.printer.domain

/**
 * DOMAIN - Printer Contract
 *
 * All printer adapters must implement this interface.
 */
interface IPrinter {
    suspend fun connect(address: String): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    suspend fun drawText(text: String, x: Int, y: Int, style: TextStyle): Result<Unit>
    suspend fun drawQR(data: String, x: Int, y: Int, size: Int): Result<Unit>
    suspend fun drawBitmap(bitmap: Bitmap, x: Int, y: Int): Result<Unit>
    suspend fun beginTransaction(media: MediaConfig): Result<Unit>
    suspend fun endTransaction(copies: Int): Result<Unit>
}
```

### Creating Domain Entities

```kotlin
// domain/PrinterTypes.kt
package com.sincpro.printer.domain

/**
 * DOMAIN - Text alignment options
 */
enum class Alignment {
    LEFT, CENTER, RIGHT
}

/**
 * DOMAIN - Font size presets
 */
enum class FontSize(val width: Int, val height: Int) {
    SMALL(1, 1),
    MEDIUM(2, 2),
    LARGE(3, 3),
    XLARGE(4, 4)
}

/**
 * DOMAIN - Text styling configuration
 */
data class TextStyle(
    val fontSize: FontSize = FontSize.MEDIUM,
    val bold: Boolean = false,
    val fontType: FontType = FontType.FONT_A
)
```

### Creating Domain Exceptions

```kotlin
// domain/Exceptions.kt
package com.sincpro.printer.domain

/**
 * DOMAIN - Connection failed exception
 */
class ConnectionFailedException(
    address: String,
    cause: Throwable? = null
) : Exception("Failed to connect to printer at $address", cause)

/**
 * DOMAIN - Printer not connected exception
 */
class PrinterNotConnectedException : Exception("Printer is not connected")

/**
 * DOMAIN - Invalid media configuration
 */
class InvalidMediaConfigException(
    message: String
) : Exception(message)
```

---

## Layer 2: Adapter (Vendor Implementations)

**Location:** `adapter/`

**Rules:**

- ✅ Implement `IPrinter` interface
- ✅ Wrap vendor SDKs (Bixolon, Zebra, Epson, etc.)
- ✅ Return `Result<T>` for all operations
- ✅ Import from `domain/` only
- ❌ NO business logic
- ❌ NO service layer imports
- ❌ NO infrastructure imports (use parameters instead)

### Creating a New Printer Adapter

When adding support for a new printer brand (e.g., Zebra):

```kotlin
// adapter/ZebraPrinterAdapter.kt
package com.sincpro.printer.adapter

import android.content.Context
import android.graphics.Bitmap
import com.sincpro.printer.domain.*
// Import Zebra SDK
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.printer.ZebraPrinter

/**
 * ADAPTER - Zebra Printer Implementation
 *
 * Wraps Zebra SDK to implement IPrinter interface.
 */
class ZebraPrinterAdapter(private val context: Context) : IPrinter {

    private var connection: BluetoothConnection? = null
    private var printer: ZebraPrinter? = null

    override suspend fun connect(address: String): Result<Unit> {
        return try {
            connection = BluetoothConnection(address)
            connection?.open()
            printer = ZebraPrinterFactory.getInstance(connection)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(ConnectionFailedException(address, e))
        }
    }

    override suspend fun disconnect(): Result<Unit> {
        return try {
            connection?.close()
            connection = null
            printer = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun drawText(
        text: String,
        x: Int,
        y: Int,
        style: TextStyle
    ): Result<Unit> {
        return try {
            // Zebra-specific text drawing using ZPL
            val zpl = buildZplText(text, x, y, style)
            printer?.sendCommand(zpl)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun drawQR(
        data: String,
        x: Int,
        y: Int,
        size: Int
    ): Result<Unit> {
        return try {
            val zpl = "^FO$x,$y^BQN,2,$size^FDMA,$data^FS"
            printer?.sendCommand(zpl)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun drawBitmap(
        bitmap: Bitmap,
        x: Int,
        y: Int
    ): Result<Unit> {
        return try {
            // Convert bitmap to ZPL graphic
            val zplImage = convertBitmapToZpl(bitmap, x, y)
            printer?.sendCommand(zplImage)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun beginTransaction(media: MediaConfig): Result<Unit> {
        return try {
            val zpl = "^XA^PW${media.widthDots}^LL${media.lengthDots}"
            printer?.sendCommand(zpl)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun endTransaction(copies: Int): Result<Unit> {
        return try {
            val zpl = "^PQ$copies^XZ"
            printer?.sendCommand(zpl)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ============================================================
    // Zebra-specific private methods
    // ============================================================

    private fun buildZplText(text: String, x: Int, y: Int, style: TextStyle): String {
        val fontHeight = style.fontSize.height * 20
        return "^FO$x,$y^A0N,$fontHeight,$fontHeight^FD$text^FS"
    }

    private fun convertBitmapToZpl(bitmap: Bitmap, x: Int, y: Int): String {
        // Zebra-specific bitmap conversion
        // ...implementation...
    }
}
```

### Adapter Checklist

When creating a new adapter:

- [ ] Implement `IPrinter` interface completely
- [ ] Wrap all vendor SDK calls in try-catch
- [ ] Return `Result.success()` or `Result.failure()`
- [ ] Use domain exceptions (`ConnectionFailedException`, etc.)
- [ ] Add vendor-specific methods as extensions (not in interface)
- [ ] Document vendor SDK version requirements
- [ ] Add vendor AAR/JAR to `libs/` folder

---

## Layer 3: Infrastructure (Platform Utilities)

**Location:** `infrastructure/`

**Rules:**

- ✅ Platform-specific utilities (Android APIs)
- ✅ Binary conversions (Base64, Bitmap, ByteArray)
- ✅ Bluetooth discovery/management
- ✅ File I/O utilities
- ✅ Import from `domain/` only
- ❌ NO business logic
- ❌ NO adapter imports
- ❌ NO service imports

### Example: Binary Converter

```kotlin
// infrastructure/BinaryConverter.kt
package com.sincpro.printer.infrastructure

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * INFRASTRUCTURE - Binary data conversions
 *
 * Handles Base64 encoding/decoding for images and binary data.
 * Used by services when processing Base64 input from external sources.
 */
object BinaryConverter {

    /**
     * Decode Base64 string to Bitmap
     * @param base64 Base64 encoded image (with or without data URI prefix)
     * @return Bitmap or null if decoding fails
     */
    fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val cleanBase64 = stripDataUriPrefix(base64)
            val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Decode Base64 string to ByteArray
     */
    fun base64ToBytes(base64: String): ByteArray? {
        return try {
            val cleanBase64 = stripDataUriPrefix(base64)
            Base64.decode(cleanBase64, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Encode Bitmap to Base64 string
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 100): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Encode ByteArray to Base64 string
     */
    fun bytesToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Remove data URI prefix if present
     * e.g., "data:image/png;base64,ABC123" → "ABC123"
     */
    fun stripDataUriPrefix(base64: String): String {
        return if (base64.contains(",")) {
            base64.substringAfter(",")
        } else {
            base64
        }
    }
}
```

### When to Create Infrastructure

Create infrastructure utilities when:

1. **Binary handling** - Base64, Bitmap, ByteArray conversions
2. **Platform APIs** - Bluetooth adapter, permissions, file system
3. **External formats** - PDF rendering, image processing
4. **Shared utilities** - Used by multiple services

---

## Layer 4: Service (Business Logic)

**Location:** `service/`

**Rules:**

- ✅ Orchestrate adapter and infrastructure calls
- ✅ Implement use cases (printText, printReceipt, etc.)
- ✅ Return `Result<T>` for all operations
- ✅ Use `withContext(Dispatchers.IO)` for blocking operations
- ✅ Can import from domain, adapter, infrastructure
- ❌ NO direct vendor SDK calls (use adapter)
- ❌ NO direct Android API calls (use infrastructure)

### Service Structure

```kotlin
// service/bixolon/BixolonPrintService.kt
package com.sincpro.printer.service.bixolon

import com.sincpro.printer.domain.*
import com.sincpro.printer.adapter.BixolonPrinterAdapter
import com.sincpro.printer.infrastructure.BinaryConverter

/**
 * SERVICE - High-level print operations for Bixolon printers
 */
class BixolonPrintService(
    private val sessionManager: PrintSessionManager
) {

    /**
     * Print simple text with alignment
     */
    suspend fun printText(
        text: String,
        alignment: Alignment = Alignment.LEFT,
        fontSize: FontSize = FontSize.MEDIUM,
        bold: Boolean = false,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        val x = calculateX(alignment, getMedia().widthDots, estimateTextWidth(text, fontSize))
        getPrinter().drawText(text, x, 20, TextStyle(fontSize, bold))
    }

    /**
     * Print image from Base64 data
     */
    suspend fun printImageBase64(
        base64Data: String,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        // Use infrastructure for Base64 conversion
        val bitmap = BinaryConverter.base64ToBitmap(base64Data)
            ?: return Result.failure(Exception("Invalid base64 image"))
        return printImage(bitmap, alignment, media)
    }

    /**
     * Print QR code with alignment
     */
    suspend fun printQR(
        data: String,
        size: Int = 5,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        val qrWidth = size * 25  // Approximate QR width
        val x = calculateX(alignment, getMedia().widthDots, qrWidth)
        getPrinter().drawQR(data, x, 20, size)
    }

    // ============================================================
    // Private helpers
    // ============================================================

    private fun calculateX(alignment: Alignment, paperWidth: Int, contentWidth: Int): Int {
        return when (alignment) {
            Alignment.LEFT -> 0
            Alignment.CENTER -> (paperWidth - contentWidth) / 2
            Alignment.RIGHT -> paperWidth - contentWidth
        }
    }
}
```

---

## Adding a New Printer Brand

Follow these steps to add support for a new printer brand:

### Step 1: Add Vendor SDK

```bash
# Copy vendor AAR/JAR to libs folder
cp zebra-sdk.aar sincpro-printer-sdk/libs/
```

Update `build.gradle.kts`:

```kotlin
dependencies {
    implementation(files("libs/zebra-sdk.aar"))
}
```

### Step 2: Create Adapter

```kotlin
// adapter/ZebraPrinterAdapter.kt
class ZebraPrinterAdapter(context: Context) : IPrinter {
    // Implement all IPrinter methods
}
```

### Step 3: Create Services

```kotlin
// service/zebra/ZebraConnectivityService.kt
class ZebraConnectivityService(private val adapter: ZebraPrinterAdapter) {
    suspend fun connect(address: String): Result<Unit>
    suspend fun disconnect(): Result<Unit>
    fun isConnected(): Boolean
}

// service/zebra/ZebraPrintService.kt
class ZebraPrintService(private val sessionManager: ZebraSessionManager) {
    suspend fun printText(...): Result<Unit>
    suspend fun printQR(...): Result<Unit>
}
```

### Step 4: Create Brand Entry Point

```kotlin
// ZebraPrinter.kt
class ZebraPrinter(context: Context) {
    private val adapter = ZebraPrinterAdapter(context)

    val connectivity = ZebraConnectivityService(adapter)
    val print = ZebraPrintService(ZebraSessionManager(adapter))
    val lowLevel = ZebraLowLevelService(adapter)
}
```

### Step 5: Register in SDK

```kotlin
// SincproPrinterSdk.kt
class SincproPrinterSdk(context: Context) {
    val bixolon = BixolonPrinter(context)
    val zebra = ZebraPrinter(context)  // Add new brand
}
```

---

## Code Style Guidelines

### Imports

```kotlin
// ✅ GOOD: Explicit imports
import com.sincpro.printer.domain.Alignment
import com.sincpro.printer.domain.FontSize
import com.sincpro.printer.domain.MediaConfig

// ❌ BAD: Wildcard imports
import com.sincpro.printer.domain.*
```

### Error Handling

```kotlin
// ✅ GOOD: Return Result<T>
suspend fun connect(address: String): Result<Unit> {
    return try {
        adapter.connect(address)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(ConnectionFailedException(address, e))
    }
}

// ❌ BAD: Throw exceptions
suspend fun connect(address: String) {
    adapter.connect(address)  // Throws on failure
}

// ❌ BAD: Swallow exceptions
suspend fun connect(address: String): Boolean {
    return try {
        adapter.connect(address)
        true
    } catch (e: Exception) {
        false  // Error info lost!
    }
}
```

### Async Operations

```kotlin
// ✅ GOOD: Use Dispatchers.IO for blocking
suspend fun readFile(path: String): Result<ByteArray> {
    return withContext(Dispatchers.IO) {
        try {
            val bytes = File(path).readBytes()
            Result.success(bytes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Documentation

```kotlin
// ✅ GOOD: Single KDoc block with all info
/**
 * Print image from Base64 encoded data
 *
 * @param base64Data Base64 encoded image (supports data URI prefix)
 * @param alignment horizontal alignment on paper
 * @param media paper/label configuration
 * @return Result indicating success or failure
 */
suspend fun printImageBase64(
    base64Data: String,
    alignment: Alignment = Alignment.CENTER,
    media: MediaConfig = MediaConfig.continuous80mm()
): Result<Unit>

// ❌ BAD: Multiple comment blocks or no docs
// Print image
// from base64
suspend fun printImageBase64(base64Data: String): Result<Unit>
```

---

## Anti-Patterns to Avoid

### ❌ Violating Layer Dependencies

```kotlin
// BAD: Domain importing from Adapter
package com.sincpro.printer.domain

import com.sincpro.printer.adapter.BixolonPrinterAdapter  // ❌ NEVER!
```

### ❌ Business Logic in Adapter

```kotlin
// BAD: Adapter calculating alignment
class BixolonPrinterAdapter : IPrinter {
    override suspend fun drawText(text: String, alignment: Alignment) {
        val x = when (alignment) {  // ❌ This belongs in Service!
            Alignment.LEFT -> 0
            Alignment.CENTER -> (paperWidth - textWidth) / 2
            Alignment.RIGHT -> paperWidth - textWidth
        }
        // ...
    }
}
```

### ❌ Direct SDK Calls in Service

```kotlin
// BAD: Service calling Bixolon SDK directly
class BixolonPrintService {
    private val bxlPrinter = BixolonPrinter()  // ❌ Use adapter!

    suspend fun printText(text: String) {
        bxlPrinter.drawText(text, 0, 0)  // ❌ Direct SDK call!
    }
}
```

### ❌ Infrastructure Logic in Service

```kotlin
// BAD: Base64 decoding in service
class BixolonPrintService {
    suspend fun printImageBase64(base64: String) {
        // ❌ This belongs in Infrastructure!
        val cleanBase64 = base64.substringAfter(",")
        val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        // ...
    }
}

// GOOD: Use infrastructure
class BixolonPrintService {
    suspend fun printImageBase64(base64: String) {
        val bitmap = BinaryConverter.base64ToBitmap(base64)  // ✅
        // ...
    }
}
```

---

## Testing Checklist

Before submitting changes:

- [ ] Code compiles: `./gradlew assembleRelease`
- [ ] No wildcard imports
- [ ] All public methods return `Result<T>`
- [ ] Domain layer has NO Android imports
- [ ] Adapters implement `IPrinter` interface
- [ ] Services use infrastructure for binary conversions
- [ ] New features have corresponding test cases

---

## File Reference

### Domain Layer

- [IPrinter.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/domain/IPrinter.kt) - Printer interface
- [PrinterTypes.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/domain/PrinterTypes.kt) - Enums and data classes
- [MediaConfig.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/domain/MediaConfig.kt) - Paper configuration

### Adapter Layer

- [BixolonPrinterAdapter.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/adapter/BixolonPrinterAdapter.kt) - Bixolon implementation

### Infrastructure Layer

- [BinaryConverter.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/infrastructure/BinaryConverter.kt) - Base64 utilities

### Service Layer

- [BixolonConnectivityService.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/service/bixolon/BixolonConnectivityService.kt)
- [BixolonPrintService.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/service/bixolon/BixolonPrintService.kt)
- [BixolonLowLevelService.kt](sincpro-printer-sdk/src/main/kotlin/com/sincpro/printer/service/bixolon/BixolonLowLevelService.kt)
