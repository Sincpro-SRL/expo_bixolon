package com.sincpro.printer.service.bixolon

import android.graphics.Bitmap
import com.sincpro.printer.domain.Alignment
import com.sincpro.printer.domain.BarcodeType
import com.sincpro.printer.domain.FontSize
import com.sincpro.printer.domain.IPrinter
import com.sincpro.printer.domain.MediaConfig
import com.sincpro.printer.domain.PrintElement
import com.sincpro.printer.domain.Receipt
import com.sincpro.printer.domain.ReceiptLine
import com.sincpro.printer.domain.TextStyle
import com.sincpro.printer.infrastructure.BinaryConverter
import com.sincpro.printer.infrastructure.PdfRenderer

class BixolonPrintService(private val printer: IPrinter) {

    suspend fun printReceipt(
        receipt: Receipt,
        media: MediaConfig = MediaConfig.continuous80mm(),
        copies: Int = 1
    ): Result<Unit> {
        val elements = receipt.toElements(media.widthDots)
        return printer.print(elements, media, copies)
    }

    suspend fun printText(
        text: String,
        fontSize: FontSize = FontSize.MEDIUM,
        alignment: Alignment = Alignment.LEFT,
        bold: Boolean = false,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val x = calculateX(alignment, media.widthDots, text.length * 10)
        val element = PrintElement.Text(text, x, 20, TextStyle(fontSize, bold, alignment))
        return printer.print(listOf(element), media)
    }

    suspend fun printTexts(
        texts: List<String>,
        fontSize: FontSize = FontSize.MEDIUM,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        var y = 20
        val elements = texts.map { text ->
            val element = PrintElement.Text(text, 10, y, TextStyle(fontSize))
            y += 30
            element
        }
        return printer.print(elements, media)
    }

    suspend fun printQR(
        data: String,
        size: Int = 5,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val qrWidth = size * 20
        val x = calculateX(alignment, media.widthDots, qrWidth)
        val element = PrintElement.QR(data, x, 20, size)
        return printer.print(listOf(element), media)
    }

    suspend fun printBarcode(
        data: String,
        type: BarcodeType = BarcodeType.CODE128,
        height: Int = 60,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val barcodeWidth = data.length * 10
        val x = calculateX(alignment, media.widthDots, barcodeWidth)
        val element = PrintElement.Barcode(data, x, 20, type, 2, height)
        return printer.print(listOf(element), media)
    }

    suspend fun printImage(
        bitmap: Bitmap,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val x = calculateX(alignment, media.widthDots, bitmap.width)
        val element = PrintElement.Image(bitmap, x, 20)
        return printer.print(listOf(element), media)
    }

    suspend fun printImageBase64(
        base64Data: String,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val bitmap = BinaryConverter.base64ToBitmap(base64Data)
            ?: return Result.failure(Exception("Invalid base64 image"))
        return printImage(bitmap, alignment, media)
    }

    suspend fun printPdfBase64(
        base64Data: String,
        page: Int = 1,
        alignment: Alignment = Alignment.CENTER,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val bitmap = PdfRenderer.renderPageToBitmap(base64Data, page, media.widthDots)
            ?: return Result.failure(Exception("Failed to render PDF page $page"))
        return printImage(bitmap, alignment, media)
    }

    suspend fun printKeyValue(
        key: String,
        value: String,
        fontSize: FontSize = FontSize.MEDIUM,
        bold: Boolean = false,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val text = "$key $value"
        return printText(text, fontSize, Alignment.LEFT, bold, media)
    }

    /**
     * Print a row with multiple columns
     * 
     * Example - Two columns (label + value):
     * ```
     * printColumns(
     *     ReceiptLine.Column("nombre:", 0.4f),
     *     ReceiptLine.Column("Andres Gutierrez", 0.6f)
     * )
     * // Output: nombre:              Andres Gutierrez
     * ```
     * 
     * Example - Three columns:
     * ```
     * printColumns(
     *     ReceiptLine.Column("Item", 0.5f),
     *     ReceiptLine.Column("Qty", 0.2f, Alignment.CENTER),
     *     ReceiptLine.Column("$99.99", 0.3f, Alignment.RIGHT)
     * )
     * ```
     */
    suspend fun printColumns(
        vararg columns: ReceiptLine.Column,
        fontSize: FontSize = FontSize.MEDIUM,
        bold: Boolean = false,
        media: MediaConfig = MediaConfig.continuous80mm()
    ): Result<Unit> {
        val line = ReceiptLine.Columns(columns.toList(), fontSize, bold)
        val (element, _) = line.toElement(20, media.widthDots)
        return if (element != null) {
            printer.print(listOf(element), media)
        } else {
            Result.failure(Exception("Failed to create columns element"))
        }
    }

    fun getPdfPageCount(base64Data: String): Int {
        return PdfRenderer.getPageCount(base64Data)
    }

    private fun calculateX(alignment: Alignment, width: Int, contentWidth: Int): Int = when (alignment) {
        Alignment.LEFT -> 10
        Alignment.CENTER -> (width - contentWidth) / 2
        Alignment.RIGHT -> width - contentWidth - 10
    }
}
