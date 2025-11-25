package com.example.recappage.util // Sesuaikan package kamu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.recappage.model.IntakeEntry
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    fun generateAndGetUri(context: Context, data: List<IntakeEntry>, monthName: String): android.net.Uri? {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titlePaint = Paint()

        // Konfigurasi Halaman (Ukuran A4 standar)
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Style Judul
        titlePaint.textSize = 24f
        titlePaint.color = Color.BLACK
        titlePaint.isFakeBoldText = true
        titlePaint.textAlign = Paint.Align.CENTER

        // Style Isi
        paint.textSize = 14f
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.LEFT

        // --- MENGGAMBAR KE PDF ---
        var yPosition = 60f

        // 1. Gambar Judul
        canvas.drawText("Monthly Recap: $monthName", 297f, yPosition, titlePaint)
        yPosition += 40f

        // 2. Header Tabel
        paint.isFakeBoldText = true
        canvas.drawText("Date", 50f, yPosition, paint)
        canvas.drawText("Food Name", 150f, yPosition, paint)
        canvas.drawText("Calories", 450f, yPosition, paint)

        // Garis bawah header
        val linePaint = Paint()
        linePaint.strokeWidth = 2f
        canvas.drawLine(50f, yPosition + 10f, 545f, yPosition + 10f, linePaint)

        yPosition += 30f
        paint.isFakeBoldText = false // Reset ke teks biasa

        // 3. Loop Data Makanan
        var totalCalories = 0

        for (item in data) {
            // Cek jika halaman penuh (sederhana)
            if (yPosition > 800f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = 50f
            }

            // Gambar baris data
            val dateString = item.date.toString() // Sesuaikan format tanggal jika perlu
            canvas.drawText(dateString, 50f, yPosition, paint)

            // Potong nama makanan jika terlalu panjang
            val safeName = if (item.name.length > 30) item.name.take(30) + "..." else item.name
            canvas.drawText(safeName, 150f, yPosition, paint)

            canvas.drawText("${item.calories} kcal", 450f, yPosition, paint)

            totalCalories += item.calories
            yPosition += 25f
        }

        // 4. Total Summary
        yPosition += 20f
        canvas.drawLine(50f, yPosition, 545f, yPosition, linePaint)
        yPosition += 30f

        paint.isFakeBoldText = true
        paint.textSize = 16f
        canvas.drawText("TOTAL CALORIES:", 250f, yPosition, paint)
        canvas.drawText("$totalCalories kcal", 450f, yPosition, paint)

        pdfDocument.finishPage(page)

        // --- MENYIMPAN FILE ---
        val file = File(context.cacheDir, "Recap_$monthName.pdf")

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()

            // Generate URI aman menggunakan FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}