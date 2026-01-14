package com.sincpro.printer.domain

data class PrinterConfig(
    val marginLeft: Int = 0,
    val marginTop: Int = 0,
    val density: Density = Density.MEDIUM,
    val speed: Speed = Speed.MEDIUM,
    val orientation: Orientation = Orientation.TOP_TO_BOTTOM,
    val autoCutter: CutterConfig = CutterConfig.DISABLED
) {
    companion object {
        val DEFAULT_80MM_RECEIPT = PrinterConfig(
            marginLeft = 0,
            marginTop = 0,
            density = Density.MEDIUM,
            speed = Speed.FAST,
            orientation = Orientation.TOP_TO_BOTTOM,
            autoCutter = CutterConfig.DISABLED
        )

        val DEFAULT_58MM_RECEIPT = PrinterConfig(
            marginLeft = 0,
            marginTop = 0,
            density = Density.MEDIUM,
            speed = Speed.FAST,
            orientation = Orientation.TOP_TO_BOTTOM,
            autoCutter = CutterConfig.DISABLED
        )

        val HIGH_QUALITY = PrinterConfig(
            marginLeft = 0,
            marginTop = 0,
            density = Density.DARK,
            speed = Speed.SLOW,
            orientation = Orientation.TOP_TO_BOTTOM,
            autoCutter = CutterConfig.DISABLED
        )
    }
}

enum class Density(val level: Int) {
    LIGHT(5),
    MEDIUM(10),
    DARK(15),
    EXTRA_DARK(20)
}

enum class Speed(val ips: Int) {
    SLOW(25),
    MEDIUM(50),
    FAST(70),
    EXTRA_FAST(80)
}

enum class Orientation {
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
}

data class CutterConfig(
    val enabled: Boolean = false,
    val type: CutterType = CutterType.FULL
) {
    companion object {
        val DISABLED = CutterConfig(false)
        val FULL_CUT = CutterConfig(true, CutterType.FULL)
        val PARTIAL_CUT = CutterConfig(true, CutterType.PARTIAL)
    }
}

enum class CutterType { FULL, PARTIAL }

data class PrinterInfo(
    val model: String,
    val firmware: String,
    val serialNumber: String,
    val dpi: Int,
    val hasCutter: Boolean
)
