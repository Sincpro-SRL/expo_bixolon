package com.sincpro.printer.domain

data class MediaConfig(
    val widthDots: Int,
    val heightDots: Int,
    val type: MediaType = MediaType.CONTINUOUS,
    val gapDots: Int = 0
) {
    companion object {
        fun continuous80mm() = MediaConfig(640, 0, MediaType.CONTINUOUS, 0)
        fun continuous58mm() = MediaConfig(464, 0, MediaType.CONTINUOUS, 0)
        fun label(widthMm: Int, heightMm: Int, gapMm: Int = 3): MediaConfig {
            val dpmm = 8
            return MediaConfig(widthMm * dpmm, heightMm * dpmm, MediaType.GAP, gapMm * dpmm)
        }
    }
}

enum class MediaType { CONTINUOUS, GAP, BLACK_MARK }
