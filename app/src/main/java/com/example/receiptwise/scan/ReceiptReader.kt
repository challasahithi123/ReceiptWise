package com.example.receiptwise.scan

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.Locale

// The three things we try to find on a receipt
data class ReceiptInfo(val shop: String, val date: String, val total: String)

// Reads the text in the photo with ML Kit and then sends the result back.
// It sends null if nothing could be read.
fun readReceipt(context: Context, photoFile: File, onResult: (ReceiptInfo?) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, Uri.fromFile(photoFile))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                // If there is no text at all, the photo was not useful
                if (result.text.isBlank()) onResult(null) else onResult(parseReceipt(result.text))
            }
            .addOnFailureListener { onResult(null) }
    } catch (e: Exception) {
        onResult(null)
    }
}

// Looks inside the text and tries to find the shop, the date and the total.
// These are simple rules, so they will not be perfect. That is why the user
// always checks the result on the form before saving.
fun parseReceipt(text: String): ReceiptInfo {
    // Split into lines and remove the empty ones
    val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

    // Shop: shops print their name at the top, so take the first line with 3 or more letters
    val shop = lines.firstOrNull { line -> line.count { it.isLetter() } >= 3 } ?: ""

    // Date: looks for a pattern like 12/11/2026, 12-11-26 or 12.11.2026
    val date = Regex("""\d{1,2}[/.-]\d{1,2}[/.-]\d{2,4}""").find(text)?.value ?: ""

    // Total: look only at lines that have the word "total" (but not "subtotal")
    val totalLines = lines.filter {
        it.contains("total", ignoreCase = true) && !it.contains("subtotal", ignoreCase = true)
    }
    var total = biggestAmount(totalLines.joinToString("\n"))

    // If there was no total line, use the biggest amount on the whole receipt
    if (total == null) {
        total = biggestAmount(text)
    }

    // Always write the total with a dot and two decimals, like 14.50
    val totalText = if (total == null) "" else String.format(Locale.UK, "%.2f", total)

    return ReceiptInfo(shop.take(30), date, totalText)
}

// Finds all amounts like 14.50 or 14,50 in the text and gives back the biggest one
private fun biggestAmount(text: String): Double? {
    return Regex("""\d+[.,]\d{2}""")
        .findAll(text)
        .mapNotNull { it.value.replace(',', '.').toDoubleOrNull() }
        .maxOrNull()
}