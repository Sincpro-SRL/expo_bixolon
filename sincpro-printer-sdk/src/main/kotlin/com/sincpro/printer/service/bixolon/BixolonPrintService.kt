package com.sincpro.printer.service.bixolon

import android.graphics.Bitmap
import com.sincpro.printer.domain.*
import com.sincpro.printer.infrastructure.BinaryConverter
import com.sincpro.printer.infrastructure.PrintSession
import com.sincpro.printer.infrastructure.PrintSessionManager

class BixolonPrintService(private val sessionManager: PrintSessionManager) {

    suspend fun printReceipt(
        receipt: Receipt,
        media: MediaConfig = MediaConfig.continuous80mm(),
        copies: Int = 1
    ): Result<Unit> = sessionManager.executeSession(media, copies) {
        var y = 20
        val allLines = receipt.header + receipt.body + receipt.footer
        for (line in allLines) {
            y = renderLine(line, y, getMedia().widthDots)
        }
    }

    suspend fun printText(
        text: String,
        fontSize: FontSize = FontSize.MEDIUM,
        alignment: Alignment = Alignment.LEFT,
        bold: Boolean = false,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        val x = calculateX(alignment, getMedia().widthDots, text.length * 10)
        getPrinter().drawText(text, x, 20, TextStyle(fontSize, bold, alignment))
    }

    suspend fun printTexts(
        texts: List<String>,
        fontSize: FontSize = FontSize.MEDIUM,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        var y = 20
        for (text in texts) {
            getPrinter().drawText(text, 10, y, TextStyle(fontSize))
            y += 30
        }
    }

    suspend fun printQR(
        data: String,
        size: Int = 5,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        val qrWidth = size * 25 + 50
        val x = calculateX(alignment, getMedia().widthDots, qrWidth)
        getPrinter().drawQR(data, x, 20, size)
    }

    suspend fun printBarcode(
        data: String,
        type: BarcodeType = BarcodeType.CODE128,
        height: Int = 60,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        val barcodeWidth = data.length * 10
        val x = calculateX(alignment, getMedia().widthDots, barcodeWidth)
        getPrinter().drawBarcode(data, x, 20, type, 2, height)
    }

    suspend fun printImage(
        bitmap: Bitmap,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        val x = calculateX(alignment, getMedia().widthDots, bitmap.width)
        getPrinter().drawBitmap(bitmap, x, 20)
    }

    /**
     * Print image from Base64 encoded data
     * Pre-processes image to remove alpha channel (transparency becomes white)
     * This is critical for thermal printers where transparency = black
     */
    suspend fun printImageBase64(
        base64Data: String,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val bitmap = BinaryConverter.base64ToBitmap(base64Data)
            ?: return Result.failure(Exception("Invalid base64 image"))
        
        return printImage(bitmap, alignment, media)
    }

    /**
     * Print a PDF page from Base64 encoded data
     * @param base64Data Base64 encoded PDF (with or without data URI prefix)
     * @param page page number to print (1-based, default 1)
     * @param media media configuration
     */
    suspend fun printPdfBase64(
        base64Data: String,
        page: Int = 1,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val adapter = sessionManager.getPrinterAdapter()
            ?: return Result.failure(Exception("PDF printing not supported"))
        
        return try {
            adapter.beginTransaction(media).getOrThrow()
            adapter.drawPdfBase64(
                base64Data = base64Data,
                x = 0,
                y = 0,
                page = page,
                width = media.widthDots,
                brightness = 50,
                dithering = true,
                compress = true
            ).getOrThrow()
            adapter.endTransaction(1).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun printKeyValue(
        key: String,
        value: String,
        bold: Boolean = false,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> = sessionManager.executeSession(media) {
        val width = getMedia().widthDots
        getPrinter().drawText(key, 10, 20, TextStyle(FontSize.MEDIUM, bold))
        getPrinter().drawText(value, width - 10 - (value.length * 10), 20, TextStyle(FontSize.MEDIUM, bold))
    }

    private suspend fun PrintSession.renderLine(line: ReceiptLine, y: Int, width: Int): Int {
        return when (line) {
            is ReceiptLine.Text -> {
                val x = calculateX(line.alignment, width, line.content.length * 10)
                val lineHeight = getLineHeight(line.fontSize)
                getPrinter().drawText(line.content, x, y, TextStyle(line.fontSize, line.bold, line.alignment))
                y + lineHeight
            }
            is ReceiptLine.KeyValue -> {
                val lineHeight = getLineHeight(line.fontSize)
                getPrinter().drawText(line.key, 10, y, TextStyle(line.fontSize, line.bold))
                getPrinter().drawText(line.value, width - 10 - (line.value.length * 10), y, TextStyle(line.fontSize, line.bold))
                y + lineHeight
            }
            is ReceiptLine.QR -> {
                val qrHeight = line.size * 25 + 50
                val x = calculateX(line.alignment, width, qrHeight)
                getPrinter().drawQR(line.data, x, y, line.size)
                y + qrHeight + 10
            }
            is ReceiptLine.Barcode -> {
                val barcodeWidth = line.data.length * 10
                val x = calculateX(line.alignment, width, barcodeWidth)
                getPrinter().drawBarcode(line.data, x, y, line.type, line.width, line.height)
                y + line.height + 40  // barcode + HRI text
            }
            is ReceiptLine.Separator -> {
                val sep = line.char.toString().repeat(line.length)
                getPrinter().drawText(sep, 10, y, TextStyle(FontSize.SMALL))
                y + 25
            }
            is ReceiptLine.Space -> y + (line.lines * 25)
            is ReceiptLine.Image -> {
                val x = calculateX(line.alignment, width, line.bitmap.width)
                getPrinter().drawBitmap(line.bitmap, x, y)
                y + line.bitmap.height + 10
            }
        }
    }

    private fun getLineHeight(fontSize: FontSize): Int {
        return when (fontSize) {
            FontSize.SMALL -> 20
            FontSize.MEDIUM -> 35
            FontSize.LARGE -> 55
            FontSize.XLARGE -> 80
        }
    }

    private fun calculateX(alignment: Alignment, width: Int, contentWidth: Int): Int {
        return when (alignment) {
            Alignment.LEFT -> 10
            Alignment.CENTER -> (width - contentWidth) / 2
            Alignment.RIGHT -> width - contentWidth - 10
        }
    }
}
