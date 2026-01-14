package com.sincpro.printer.service.bixolon

import android.graphics.Bitmap
import android.util.Log
import com.sincpro.printer.adapter.BixolonPrinterAdapter
import com.sincpro.printer.domain.*
import com.sincpro.printer.infrastructure.BinaryConverter

class BixolonLowLevelService(private val adapter: BixolonPrinterAdapter) {

    private val printer: IPrinter get() = adapter

    companion object {
        private const val TAG = "BixolonLowLevelService"
    }

    suspend fun configure(config: PrinterConfig): Result<Unit> {
        return printer.configure(config)
    }

    suspend fun begin(media: MediaConfig = MediaConfig.continuous80mm()): Result<Unit> {
        return printer.beginTransaction(media)
    }

    suspend fun text(text: String, x: Int, y: Int, style: TextStyle = TextStyle()): Result<Unit> {
        return printer.drawText(text, x, y, style)
    }

    suspend fun qr(data: String, x: Int, y: Int, size: Int = 5): Result<Unit> {
        return printer.drawQR(data, x, y, size)
    }

    suspend fun barcode(
        data: String, x: Int, y: Int,
        type: BarcodeType = BarcodeType.CODE128,
        width: Int = 2, height: Int = 60
    ): Result<Unit> {
        return printer.drawBarcode(data, x, y, type, width, height)
    }

    suspend fun bitmap(image: Bitmap, x: Int, y: Int): Result<Unit> {
        return printer.drawBitmap(image, x, y)
    }

    /**
     * Draw bitmap from Base64 string
     * Pre-processes image to remove alpha channel (transparency becomes white)
     * This is critical for thermal printers where transparency = black
     * 
     * @param base64Data Base64 encoded image (with or without data URI prefix)
     * @param x horizontal position in dots
     * @param y vertical position in dots
     * @param width width in dots (0 = original size)
     * @param brightness 0-100 (50 = normal)
     * @param dithering true for better quality
     */
    suspend fun bitmapBase64(
        base64Data: String,
        x: Int,
        y: Int,
        width: Int = 0,
        brightness: Int = 50,
        dithering: Boolean = true
    ): Result<Unit> {
        Log.d(TAG, "bitmapBase64: x=$x, y=$y, width=$width, brightness=$brightness")
        
        val bitmap = BinaryConverter.base64ToBitmap(base64Data)
            ?: return Result.failure(Exception("Failed to decode image from Base64"))
        
        Log.d(TAG, "bitmapBase64: bitmap ${bitmap.width}x${bitmap.height}, config=${bitmap.config}")
        
        return adapter.drawBitmapAdvanced(bitmap, x, y, width, brightness, dithering)
    }

    /**
     * Draw a page from a PDF (Base64 encoded)
     * @param base64Data Base64 encoded PDF data
     * @param x horizontal position in dots
     * @param y vertical position in dots
     * @param page page number (1-based)
     * @param width width in dots (0 = auto fit to paper)
     * @param brightness 0-100 (50 = normal)
     * @param dithering true for better quality
     * @param compress true to compress data sent to printer
     */
    suspend fun pdfBase64(
        base64Data: String,
        x: Int = 0,
        y: Int = 0,
        page: Int = 1,
        width: Int = 0,
        brightness: Int = 50,
        dithering: Boolean = true,
        compress: Boolean = true
    ): Result<Unit> {
        Log.d(TAG, "pdfBase64: page=$page, x=$x, y=$y, width=$width")
        return adapter.drawPdfBase64(base64Data, x, y, page, width, brightness, dithering, compress)
    }

    /**
     * Get number of pages in a PDF (Base64 encoded)
     */
    fun getPdfPageCountBase64(base64Data: String): Int {
        val count = adapter.getPdfPageCountBase64(base64Data)
        Log.d(TAG, "getPdfPageCountBase64: count=$count")
        return count
    }

    suspend fun end(copies: Int = 1): Result<Unit> {
        return printer.endTransaction(copies)
    }

    suspend fun feed(dots: Int): Result<Unit> {
        return printer.feedPaper(dots)
    }

    suspend fun cut(): Result<Unit> {
        return printer.cutPaper()
    }
}
