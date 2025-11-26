package com.clerodri.binnacle.core.di

import android.content.Context
import android.graphics.Bitmap
import com.clerodri.binnacle.addreport.presentation.ImageUiState
import java.io.File
import java.io.FileOutputStream

object WorkManagerSerializer {

    fun serializeImages(images: List<ImageUiState>, context: Context): String {
        val savedImages = mutableListOf<String>()

        images.forEach { imageState ->
            if (imageState.bitmap != null) {
                try {

                    val file = File(context.cacheDir, imageState.filename)

                    FileOutputStream(file).use { out ->
                        imageState.bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }

                    savedImages.add("${imageState.filename}:${file.absolutePath}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return savedImages.joinToString(",")
    }

    fun serializeSignedUrls(signedImages: Map<String, String>): String {
        return signedImages.entries
            .joinToString(",") { (filename, url) -> "$filename:$url" }
    }
}