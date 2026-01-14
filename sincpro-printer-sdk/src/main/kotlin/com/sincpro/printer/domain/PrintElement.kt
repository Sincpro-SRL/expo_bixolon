package com.sincpro.printer.domain

import android.graphics.Bitmap

sealed class PrintElement {
    data class Text(
        val content: String,
        val x: Int,
        val y: Int,
        val style: TextStyle = TextStyle()
    ) : PrintElement()

    data class QR(
        val data: String,
        val x: Int,
        val y: Int,
        val size: Int = 6
    ) : PrintElement()

    data class Barcode(
        val data: String,
        val x: Int,
        val y: Int,
        val type: BarcodeType = BarcodeType.CODE128,
        val width: Int = 2,
        val height: Int = 60
    ) : PrintElement()

    data class Image(
        val bitmap: Bitmap,
        val x: Int,
        val y: Int
    ) : PrintElement()

    data class Space(val dots: Int) : PrintElement()
}

data class TextStyle(
    val fontSize: FontSize = FontSize.MEDIUM,
    val bold: Boolean = false,
    val alignment: Alignment = Alignment.LEFT
)

enum class FontSize { SMALL, MEDIUM, LARGE, XLARGE }

enum class Alignment { LEFT, CENTER, RIGHT }

enum class BarcodeType { CODE128, CODE39, EAN13, EAN8, UPCA, UPCE, CODE93, CODABAR }
