package com.sincpro.printer.domain

data class MediaConfig(
    val widthDots: Int,
    val heightDots: Int,
    val type: MediaType = MediaType.CONTINUOUS,
    val gapDots: Int = 0
) {
    companion object {
        private const val DOTS_PER_MM = 8

        fun continuous80mm() = MediaConfig(640, 0, MediaType.CONTINUOUS)
        fun continuous72mm() = MediaConfig(576, 0, MediaType.CONTINUOUS)
        fun continuous58mm() = MediaConfig(464, 0, MediaType.CONTINUOUS)
        fun continuous(widthMm: Int) = MediaConfig(widthMm * DOTS_PER_MM, 0, MediaType.CONTINUOUS)

        fun label(widthMm: Int, heightMm: Int, gapMm: Int = 3) = MediaConfig(
            widthDots = widthMm * DOTS_PER_MM,
            heightDots = heightMm * DOTS_PER_MM,
            type = MediaType.GAP,
            gapDots = gapMm * DOTS_PER_MM
        )

        fun blackMark(widthMm: Int, heightMm: Int, markMm: Int = 3) = MediaConfig(
            widthDots = widthMm * DOTS_PER_MM,
            heightDots = heightMm * DOTS_PER_MM,
            type = MediaType.BLACK_MARK,
            gapDots = markMm * DOTS_PER_MM
        )
    }
}

enum class MediaType { CONTINUOUS, GAP, BLACK_MARK }
