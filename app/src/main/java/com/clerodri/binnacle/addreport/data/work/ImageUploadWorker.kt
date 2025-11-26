package com.clerodri.binnacle.addreport.data.work


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


/**
 * Author: Ronaldo R.
 * Date:  11/25/2025
 * Description:
 **/
@HiltWorker
class ImageUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "ImageUploadWorker"
        const val KEY_IMAGES_JSON = "images_json"
        const val KEY_SIGNED_URLS_JSON = "signed_urls_json"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🚀 Starting background image upload work")

            val imagesJson =
                inputData.getString(KEY_IMAGES_JSON) ?: return@withContext Result.failure()
            val signedUrlsJson =
                inputData.getString(KEY_SIGNED_URLS_JSON) ?: return@withContext Result.failure()

            Log.d(TAG, "📦 Received: $imagesJson")

            val images = parseImagesJson(imagesJson)
            val signedUrls = parseSignedUrlsJson(signedUrlsJson)

            var uploadedCount = 0
            var failedCount = 0

            images.forEach { (filename, imagePath) ->
                try {
                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    if (bitmap == null) {
                        Log.w(TAG, "Failed to decode: $filename")
                        failedCount++
                        return@forEach
                    }

                    val presignedUrl = signedUrls[filename]
                    if (presignedUrl == null) {
                        Log.w(TAG, "No URL for: $filename")
                        failedCount++
                        return@forEach
                    }

                    // ✅ Call the repository/usecase directly
                    // Assuming you have access to the app context
                    val uploadResult = performUpload(presignedUrl, bitmap)

                    if (uploadResult) {
                        uploadedCount++
                        Log.d(TAG, "✅ Uploaded: $filename ($uploadedCount)")
                    } else {
                        failedCount++
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error uploading $filename: ${e.message}")
                    failedCount++
                }
            }

            Log.d(TAG, "📊 Complete: $uploadedCount success, $failedCount failed")
            return@withContext Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Worker error: ${e.message}", e)
            return@withContext Result.retry()
        }
    }

    // ✅ Simple HTTP upload without UseCase
    private suspend fun performUpload(presignedUrl: String, bitmap: Bitmap): Boolean {
        return try {
            val byteArray = ByteArrayOutputStream().apply {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, this)
            }.toByteArray()

            val request = okhttp3.Request.Builder()
                .url(presignedUrl)
                .put(byteArray.toRequestBody("image/jpeg".toMediaType(), 0, byteArray.size))
                .build()

            val client = okhttp3.OkHttpClient()
            val response = client.newCall(request).execute()

            response.isSuccessful.also {
                if (!it) Log.e(TAG, "Upload failed: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload error: ${e.message}", e)
            false
        }
    }

    private fun parseImagesJson(json: String): Map<String, String> {
        // Simple parsing: "photo_1.jpg:/path/to/file.jpg,photo_2.jpg:/path/to/file.jpg"
        return json.split(",")
            .associate { pair ->
                val (filename, path) = pair.split(":")
                filename to path
            }
    }

    private fun parseSignedUrlsJson(json: String): Map<String, String> {
        // Simple parsing: "photo_1.jpg:https://s3.aws.com/...,photo_2.jpg:https://..."
        return json.split(",")
            .associate { pair ->
                val parts = pair.split(":")
                val filename = parts[0]
                val url = parts.drop(1).joinToString(":") // Handle URLs with ://
                filename to url
            }
    }
}