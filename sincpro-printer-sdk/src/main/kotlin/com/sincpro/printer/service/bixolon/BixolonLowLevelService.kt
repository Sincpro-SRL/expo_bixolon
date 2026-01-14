package com.sincpro.printer.service.bixolon

import android.graphics.Bitmap
import com.sincpro.printer.domain.*

class BixolonLowLevelService(private val printer: IPrinter) {

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
