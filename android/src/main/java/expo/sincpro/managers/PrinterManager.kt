package expo.sincpro.managers

import android.util.Log
import expo.sincpro.bixolon.BixolonQRPrinter

class PrinterManager(private val bixolonQRPrinter: BixolonQRPrinter) {
    private val TAG = "PrinterManager"

    fun printPlainText(text: String): Boolean {
        try {
            Log.d(TAG, "Printing plain text using Bixolon SDK")
            
            val finalText = if (text.isNotEmpty()) {
                text
            } else {
                "Texto vacío"
            }

            bixolonQRPrinter.initializeForNewPrint()
            
            bixolonQRPrinter.beginTransactionPrint()
            
            val lines = finalText.split("\n")
            var yPosition = 50
            
            for (line in lines) {
                if (line.isNotEmpty()) {
                    bixolonQRPrinter.drawText(
                        line,
                        50, yPosition,
                        BixolonQRPrinter.FONT_SIZE_10,
                        1, 1, 0,
                        BixolonQRPrinter.ROTATION_NONE,
                        false, false,
                        BixolonQRPrinter.TEXT_ALIGNMENT_LEFT
                    )
                    yPosition += 30
                } else {
                    yPosition += 15
                }
            }
            
            bixolonQRPrinter.print(1, 1)
            
            bixolonQRPrinter.endTransactionPrint()

            Log.d(TAG, "Plain text printed successfully using Bixolon SDK")
            Log.d(TAG, "  Content: $finalText")
            Log.d(TAG, "  Lines printed: ${lines.size}")

            return true
                
        } catch (e: Exception) {
            Log.e(TAG, "Error printing plain text: ${e.message}")
            throw e
        }
    }

    fun printInvoice(invoiceText: String): Boolean {
        try {
            Log.d(TAG, "Printing invoice using Bixolon SDK")
            
            val finalText = if (invoiceText.isNotEmpty()) {
                invoiceText
            } else {
                "Factura vacía"
            }
            
            bixolonQRPrinter.initializeForNewPrint()
            
            bixolonQRPrinter.beginTransactionPrint()
            
            val lines = finalText.split("\n")
            var yPosition = 50
            
            for (line in lines) {
                if (line.isNotEmpty()) {
                    bixolonQRPrinter.drawText(
                        line,
                        50, yPosition,
                        BixolonQRPrinter.FONT_SIZE_10,
                        1, 1, 0,
                        BixolonQRPrinter.ROTATION_NONE,
                        false, false,
                        BixolonQRPrinter.TEXT_ALIGNMENT_LEFT
                    )
                    yPosition += 30
                } else {
                    yPosition += 15
                }
            }
            
            bixolonQRPrinter.print(1, 1)
            
            bixolonQRPrinter.endTransactionPrint()

            Log.d(TAG, "Invoice printed successfully using Bixolon SDK")
            Log.d(TAG, "  Content: $finalText")
            Log.d(TAG, "  Lines printed: ${lines.size}")
            return true
            
        } catch (e: Exception) {
            Log.e(TAG, "Error printing invoice: ${e.message}")
            throw e
        }
    }

    fun printQRCode(text: String, size: Int): Boolean {
        try {
            Log.d(TAG, "Printing QR code for text: $text using Bixolon SDK")
            
            bixolonQRPrinter.initializeForNewPrint()
            
            bixolonQRPrinter.beginTransactionPrint()
            
            bixolonQRPrinter.drawQrCode(
                text,
                100,
                50,
                BixolonQRPrinter.QR_CODE_MODEL2,
                BixolonQRPrinter.ECC_LEVEL_7,
                Math.min(size, 3),
                BixolonQRPrinter.ROTATION_NONE
            )
            
            bixolonQRPrinter.print(1, 1)
            
            bixolonQRPrinter.endTransactionPrint()

            Log.d(TAG, "QR code printed successfully using Bixolon SDK")
            Log.d(TAG, "  Content: $text")
            Log.d(TAG, "  Size: $size")

            return true
                
        } catch (e: Exception) {
            Log.e(TAG, "Error printing QR code: ${e.message}")
            throw e
        }
    }

    fun printQRCodeAdvanced(data: String, size: Int): Boolean {
        try {
            Log.d(TAG, "Printing advanced QR code for data: $data")
            
            val success = bixolonQRPrinter.printQRCode(data, size)
            
            if (success) {
                Log.d(TAG, "QR code printed successfully with Bixolon library")
                return true
            } else {
                Log.e(TAG, "Failed to print QR code with Bixolon library")
                return false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error printing QR code: ${e.message}")
            throw e
        }
    }

    fun printFormattedText(text: String, fontSize: Int = BixolonQRPrinter.FONT_SIZE_10): Boolean {
        try {
            Log.d(TAG, "Printing formatted text using Bixolon SDK")
            Log.d(TAG, "Full text to print: $text")
            
            val finalText = if (text.isNotEmpty()) {
                text
            } else {
                "Texto vacío"
            }

            bixolonQRPrinter.initializeForNewPrint()
            
            bixolonQRPrinter.beginTransactionPrint()
            
            val lines = finalText.split("\n")
            Log.d(TAG, "Total lines to print: ${lines.size}")
            var yPosition = 50
            var lineCount = 0
            
            for (line in lines) {
                lineCount++
                Log.d(TAG, "Printing line $lineCount: '$line'")
                
                if (line.isNotEmpty()) {
                    val maxLineLength = 32
                    if (line.length > maxLineLength) {
                        val words = line.split(" ")
                        var currentLine = ""
                        
                        for (word in words) {
                            if ((currentLine + word).length <= maxLineLength) {
                                currentLine += if (currentLine.isEmpty()) word else " $word"
                            } else {
                                if (currentLine.isNotEmpty()) {
                                    Log.d(TAG, "Printing wrapped line: '$currentLine'")
                                    bixolonQRPrinter.drawText(
                                        currentLine,
                                        50, yPosition,
                                        fontSize,
                                        1, 1, 0,
                                        BixolonQRPrinter.ROTATION_NONE,
                                        false, false,
                                        BixolonQRPrinter.TEXT_ALIGNMENT_LEFT
                                    )
                                    yPosition += 30
                                }
                                currentLine = word
                            }
                        }
                        
                        if (currentLine.isNotEmpty()) {
                            Log.d(TAG, "Printing final wrapped line: '$currentLine'")
                            bixolonQRPrinter.drawText(
                                currentLine,
                                50, yPosition,
                                fontSize,
                                1, 1, 0,
                                BixolonQRPrinter.ROTATION_NONE,
                                false, false,
                                BixolonQRPrinter.TEXT_ALIGNMENT_LEFT
                            )
                            yPosition += 30
                        }
                    } else {
                        Log.d(TAG, "Printing normal line: '$line'")
                        bixolonQRPrinter.drawText(
                            line,
                            50, yPosition,
                            fontSize,
                            1, 1, 0,
                            BixolonQRPrinter.ROTATION_NONE,
                            false, false,
                            BixolonQRPrinter.TEXT_ALIGNMENT_LEFT
                        )
                        yPosition += 30
                    }
                } else {
                    Log.d(TAG, "Skipping empty line")
                    yPosition += 15
                }
            }
            
            Log.d(TAG, "Calling print() with ${lineCount} lines")
            bixolonQRPrinter.print(1, 1)
            
            bixolonQRPrinter.endTransactionPrint()

            Log.d(TAG, "Formatted text printed successfully using Bixolon SDK")
            Log.d(TAG, "  Total lines processed: $lineCount")
            Log.d(TAG, "  Final Y position: $yPosition")

            return true
                
        } catch (e: Exception) {
            Log.e(TAG, "Error printing formatted text: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTrace.joinToString("\n")}")
            throw e
        }
    }

    fun printTextSimple(text: String): Boolean {
        try {
            Log.d(TAG, "Printing simple text using Bixolon SDK")
            Log.d(TAG, "Full text to print: $text")
            
            val finalText = if (text.isNotEmpty()) {
                text
            } else {
                "Texto vacío"
            }

            bixolonQRPrinter.initializeForNewPrint()
            
            bixolonQRPrinter.beginTransactionPrint()
            
            val lines = finalText.split("\n")
            Log.d(TAG, "Total lines to print: ${lines.size}")
            var yPosition = 50
            
            for ((index, line) in lines.withIndex()) {
                Log.d(TAG, "Printing line ${index + 1}: '$line'")
                
                if (line.isNotEmpty()) {
                    bixolonQRPrinter.drawText(
                        line,
                        50, yPosition,
                        BixolonQRPrinter.FONT_SIZE_10,
                        1, 1, 0,
                        BixolonQRPrinter.ROTATION_NONE,
                        false, false,
                        BixolonQRPrinter.TEXT_ALIGNMENT_LEFT
                    )
                    yPosition += 25
                } else {
                    yPosition += 10
                }
                
                if ((index + 1) % 10 == 0) {
                    Log.d(TAG, "Adding extra spacing at line ${index + 1}")
                    yPosition += 20
                }
            }
            
            Log.d(TAG, "Adding final spacing")
            yPosition += 50
            
            Log.d(TAG, "Calling print() with ${lines.size} lines")
            bixolonQRPrinter.print(1, 1)
            
            bixolonQRPrinter.endTransactionPrint()

            Log.d(TAG, "Simple text printed successfully using Bixolon SDK")
            Log.d(TAG, "  Total lines processed: ${lines.size}")
            Log.d(TAG, "  Final Y position: $yPosition")

            return true
                
        } catch (e: Exception) {
            Log.e(TAG, "Error printing simple text: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTrace.joinToString("\n")}")
            throw e
        }
    }

    fun printTextInPages(text: String): Boolean {
        try {
            Log.d(TAG, "Printing text in pages using Bixolon SDK")
            Log.d(TAG, "Full text to print: $text")
            
            val finalText = if (text.isNotEmpty()) {
                text
            } else {
                "Texto vacío"
            }

            val lines = finalText.split("\n")
            Log.d(TAG, "Total lines to print: ${lines.size}")
            
            val linesPerPage = 8
            val totalPages = (lines.size + linesPerPage - 1) / linesPerPage
            
            for (page in 0 until totalPages) {
                Log.d(TAG, "Printing page ${page + 1} of $totalPages")
                
                bixolonQRPrinter.initializeForNewPrint()
                bixolonQRPrinter.beginTransactionPrint()
                
                val startIndex = page * linesPerPage
                val endIndex = minOf(startIndex + linesPerPage, lines.size)
                var yPosition = 50
                
                for (i in startIndex until endIndex) {
                    val line = lines[i]
                    Log.d(TAG, "Printing line ${i + 1} on page ${page + 1}: '$line'")
                    
                    if (line.isNotEmpty()) {
                        bixolonQRPrinter.drawText(
                            line,
                            50, yPosition,
                            BixolonQRPrinter.FONT_SIZE_10,
                            1, 1, 0,
                            BixolonQRPrinter.ROTATION_NONE,
                            false, false,
                            BixolonQRPrinter.TEXT_ALIGNMENT_LEFT
                        )
                        yPosition += 30
                    } else {
                        yPosition += 15
                    }
                }
                
                Log.d(TAG, "Adding page spacing for page ${page + 1}")
                yPosition += 50
                
                bixolonQRPrinter.print(1, 1)
                bixolonQRPrinter.endTransactionPrint()
                
                Log.d(TAG, "Page ${page + 1} printed successfully")
            }

            Log.d(TAG, "All pages printed successfully using Bixolon SDK")
            Log.d(TAG, "  Total pages: $totalPages")
            Log.d(TAG, "  Total lines: ${lines.size}")

            return true
                
        } catch (e: Exception) {
            Log.e(TAG, "Error printing text in pages: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTrace.joinToString("\n")}")
            throw e
        }
    }

}
