package com.sincpro.printer.infrastructure

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Base64
import android.util.Log
import java.io.File

/**
 * INFRASTRUCTURE - Binary Converter
 * 
 * Handles conversion of binary data (Base64, bytes) to formats
 * usable by the printer adapter.
 */
object BinaryConverter {

    private const val TAG = "BinaryConverter"

    /**
     * Decode Base64 string to Bitmap optimized for thermal printing.
     * - Removes alpha channel (transparent pixels become white)
     * - Uses RGB_565 format for Bixolon SDK compatibility
     * 
     * @param base64 Base64 encoded image (with or without data URI prefix)
     * @return Bitmap ready for printing, or null if decoding fails
     */
    fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val cleanBase64 = stripDataUriPrefix(base64)
            val bytes = Base64.decode(cleanBase64, Base64.NO_WRAP)
            
            val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from bytes")
                return null
            }
            
            Log.d(TAG, "Original bitmap: ${originalBitmap.width}x${originalBitmap.height}, config=${originalBitmap.config}, hasAlpha=${originalBitmap.hasAlpha()}")
            
            val rgbBitmap = removeAlphaChannel(originalBitmap)
            
            Log.d(TAG, "Converted bitmap: ${rgbBitmap.width}x${rgbBitmap.height}, config=${rgbBitmap.config}")
            
            if (rgbBitmap !== originalBitmap) {
                originalBitmap.recycle()
            }
            
            rgbBitmap
        } catch (e: Exception) {
            Log.e(TAG, "base64ToBitmap error: ${e.message}", e)
            null
        }
    }

    /**
     * Remove alpha channel from bitmap by drawing on white background.
     * This is critical for thermal printers where transparency = black.
     */
    private fun removeAlphaChannel(source: Bitmap): Bitmap {
        if (source.config == Bitmap.Config.RGB_565) {
            return source
        }
        
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.RGB_565)
        val canvas = Canvas(result)
        
        canvas.drawColor(Color.WHITE)
        
        canvas.drawBitmap(source, 0f, 0f, null)
        
        return result
    }

    /**
     * Decode Base64 string to byte array
     * Supports both raw Base64 and data URI format
     */
    fun base64ToBytes(base64: String): ByteArray? {
        return try {
            val cleanBase64 = stripDataUriPrefix(base64)
            Base64.decode(cleanBase64, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e(TAG, "base64ToBytes error: ${e.message}", e)
            null
        }
    }

    /**
     * Write Base64 data to a temporary file
     * @param base64 Base64 encoded data
     * @param cacheDir directory to create temp file in
     * @param prefix filename prefix
     * @param suffix filename suffix (e.g., ".pdf", ".png")
     * @return temporary File or null if failed
     */
    fun base64ToTempFile(
        base64: String,
        cacheDir: File,
        prefix: String = "temp_",
        suffix: String = ".bin"
    ): File? {
        return try {
            val bytes = base64ToBytes(base64) ?: return null
            val tempFile = File.createTempFile(prefix, suffix, cacheDir)
            tempFile.writeBytes(bytes)
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Encode Bitmap to Base64 string
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 100): String {
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Encode byte array to Base64 string
     */
    fun bytesToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /**
     * Strip data URI prefix if present
     * "data:image/png;base64,ABC123" -> "ABC123"
     */
    private fun stripDataUriPrefix(base64: String): String {
        return if (base64.contains(",")) {
            base64.substringAfter(",")
        } else {
            base64
        }
    }
}
