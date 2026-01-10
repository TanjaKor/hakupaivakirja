package com.tanjan.hakupivkirja.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object TrainingShareUtils {
    private const val TAG = "TrainingShareUtils"

    fun shareBitmap(context: Context, bitmap: Bitmap, fileName: String = "treenisuunnitelma.jpg") {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, fileName)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.close()

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/jpeg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Jaa treeni"))
        } catch (e: Exception) {
            Log.e(TAG, "Virhe jaettaessa kuvaa", e)
        }
    }

    /**
     * Luodaan tyylitelty kuvakaappaus treenistä tekstin perusteella.
     */
    fun createTrainingImage(context: Context, text: String): Bitmap {
        val lines = text.split("\n")
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            isAntiAlias = true
        }
        
        val margin = 50
        val lineSpacing = 60
        val width = 1080 
        
        val height = (lines.size * lineSpacing) + 300
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        
        canvas.drawColor(Color.WHITE)
        
        // Otsikko
        val headerPaint = Paint().apply { color = Color.parseColor("#006D3B") }
        canvas.drawRect(0f, 0f, width.toFloat(), 150f, headerPaint)
        
        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 50f
            isFakeBoldText = true
        }
        canvas.drawText("HAKUPÄIVÄKIRJA - SUUNNITELMA", margin.toFloat(), 95f, titlePaint)

        var y = 250f
        lines.forEach { line ->
            if (line.contains("Treenisuunnitelma") || line.contains("Pistot")) {
                paint.isFakeBoldText = true
                paint.textSize = 45f
            } else if (line.startsWith("📍")) {
                paint.isFakeBoldText = true
                paint.textSize = 42f
            } else {
                paint.isFakeBoldText = false
                paint.textSize = 38f
            }
            canvas.drawText(line, margin.toFloat(), y, paint)
            y += lineSpacing
        }
        
        return bitmap
    }
}
