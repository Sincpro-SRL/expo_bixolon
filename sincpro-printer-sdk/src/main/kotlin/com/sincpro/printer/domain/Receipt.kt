package com.sincpro.printer.domain

import android.graphics.Bitmap

data class Receipt(
    val header: List<ReceiptLine> = emptyList(),
    val body: List<ReceiptLine> = emptyList(),
    val footer: List<ReceiptLine> = emptyList()
) {
    fun toElements(mediaWidth: Int): List<PrintElement> {
        val elements = mutableListOf<PrintElement>()
        var y = 20

        (header + body + footer).forEach { line ->
            val (element, height) = line.toElement(y, mediaWidth)
            if (element != null) elements.add(element)
            y += height
        }

        return elements
    }

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
    abstract fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int>

    data class Text(
        val content: String,
        val fontSize: FontSize = FontSize.MEDIUM,
        val bold: Boolean = false,
        val alignment: Alignment = Alignment.LEFT
    ) : ReceiptLine() {
        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            val x = calculateX(alignment, mediaWidth, content.length * 10)
            return PrintElement.Text(content, x, y, TextStyle(fontSize, bold, alignment)) to 30
        }
    }

    data class KeyValue(
        val key: String,
        val value: String,
        val fontSize: FontSize = FontSize.MEDIUM,
        val bold: Boolean = false
    ) : ReceiptLine() {
        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            return PrintElement.Text("$key: $value", 10, y, TextStyle(fontSize, bold)) to 30
        }
    }

    data class QR(
        val data: String,
        val size: Int = 5,
        val alignment: Alignment = Alignment.CENTER
    ) : ReceiptLine() {
        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            val qrWidth = size * 20
            val x = calculateX(alignment, mediaWidth, qrWidth)
            return PrintElement.QR(data, x, y, size) to (qrWidth + 20)
        }
    }

    data class Barcode(
        val data: String,
        val type: BarcodeType = BarcodeType.CODE128,
        val width: Int = 2,
        val height: Int = 60,
        val alignment: Alignment = Alignment.CENTER
    ) : ReceiptLine() {
        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            val barcodeWidth = data.length * 10
            val x = calculateX(alignment, mediaWidth, barcodeWidth)
            return PrintElement.Barcode(data, x, y, type, width, height) to (height + 30)
        }
    }

    data class Image(
        val bitmap: Bitmap,
        val alignment: Alignment = Alignment.CENTER
    ) : ReceiptLine() {
        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            val x = calculateX(alignment, mediaWidth, bitmap.width)
            return PrintElement.Image(bitmap, x, y) to (bitmap.height + 10)
        }
    }

    data class Separator(
        val char: Char = '-',
        val length: Int = 48
    ) : ReceiptLine() {
        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            val sep = char.toString().repeat(length)
            return PrintElement.Text(sep, 10, y, TextStyle(FontSize.SMALL)) to 20
        }
    }

    data class Space(val lines: Int = 1) : ReceiptLine() {
        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            return null to (lines * 20)
        }
    }

    /**
     * Row with multiple columns - each column has text and relative width
     * Example:
     * ```
     * Columns(
     *     Column("nombre:", 0.3f),      // 30% width
     *     Column("Andres Gutierrez", 0.7f)  // 70% width
     * )
     * ```
     * Or with alignment:
     * ```
     * Columns(
     *     Column("Total:", 0.6f, Alignment.LEFT),
     *     Column("$99.99", 0.4f, Alignment.RIGHT)
     * )
     * ```
     */
    data class Columns(
        val columns: List<Column>,
        val fontSize: FontSize = FontSize.MEDIUM,
        val bold: Boolean = false
    ) : ReceiptLine() {
        constructor(vararg cols: Column, fontSize: FontSize = FontSize.MEDIUM, bold: Boolean = false) 
            : this(cols.toList(), fontSize, bold)

        override fun toElement(y: Int, mediaWidth: Int): Pair<PrintElement?, Int> {
            // Build padded string for single-line print
            val totalChars = when (fontSize) {
                FontSize.SMALL -> 64
                FontSize.MEDIUM -> 48
                FontSize.LARGE -> 32
                FontSize.XLARGE -> 24
            }
            
            val sb = StringBuilder()
            columns.forEach { col ->
                val colChars = (totalChars * col.widthRatio).toInt()
                val text = when (col.alignment) {
                    Alignment.LEFT -> col.text.padEnd(colChars)
                    Alignment.CENTER -> col.text.padStart((colChars + col.text.length) / 2).padEnd(colChars)
                    Alignment.RIGHT -> col.text.padStart(colChars)
                }
                sb.append(text.take(colChars))
            }
            
            return PrintElement.Text(sb.toString(), 10, y, TextStyle(fontSize, bold)) to 30
        }
    }

    data class Column(
        val text: String,
        val widthRatio: Float = 0.5f,
        val alignment: Alignment = Alignment.LEFT
    )

    companion object {
        fun calculateX(alignment: Alignment, width: Int, contentWidth: Int): Int = when (alignment) {
            Alignment.LEFT -> 10
            Alignment.CENTER -> (width - contentWidth) / 2
            Alignment.RIGHT -> width - contentWidth - 10
        }
    }
}
