package com.sincpro.printer.domain

import android.graphics.Bitmap

data class Receipt(
    val header: List<ReceiptLine> = emptyList(),
    val body: List<ReceiptLine> = emptyList(),
    val footer: List<ReceiptLine> = emptyList()
) {
    class Builder {
        private val header = mutableListOf<ReceiptLine>()
        private val body = mutableListOf<ReceiptLine>()
        private val footer = mutableListOf<ReceiptLine>()

        fun header(vararg lines: ReceiptLine) = apply { header.addAll(lines) }
        fun body(vararg lines: ReceiptLine) = apply { body.addAll(lines) }
        fun footer(vararg lines: ReceiptLine) = apply { footer.addAll(lines) }
        fun build() = Receipt(header.toList(), body.toList(), footer.toList())
    }

    companion object {
        fun builder() = Builder()
    }
}

sealed class ReceiptLine {
    data class Text(
        val content: String,
        val fontSize: FontSize = FontSize.MEDIUM,
        val bold: Boolean = false,
        val alignment: Alignment = Alignment.LEFT
    ) : ReceiptLine()

    data class KeyValue(
        val key: String,
        val value: String,
        val fontSize: FontSize = FontSize.MEDIUM,
        val bold: Boolean = false
    ) : ReceiptLine()

    data class QR(
        val data: String,
        val size: Int = 5,
        val alignment: Alignment = Alignment.CENTER
    ) : ReceiptLine()

    data class Separator(
        val char: Char = '-',
        val length: Int = 48
    ) : ReceiptLine()

    data class Space(val lines: Int = 1) : ReceiptLine()

    data class Image(
        val bitmap: Bitmap,
        val alignment: Alignment = Alignment.CENTER
    ) : ReceiptLine()

    data class Barcode(
        val data: String,
        val type: BarcodeType = BarcodeType.CODE128,
        val width: Int = 2,
        val height: Int = 60,
        val alignment: Alignment = Alignment.CENTER
    ) : ReceiptLine()
}
