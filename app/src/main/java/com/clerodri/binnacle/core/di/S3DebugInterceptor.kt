package com.clerodri.binnacle.core.di

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class S3DebugInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        Log.d("S3Debug", "════════════════════════════════════════")
        Log.d("S3Debug", "REQUEST URL: ${originalRequest.url}")
        Log.d("S3Debug", "REQUEST METHOD: ${originalRequest.method}")
        Log.d("S3Debug", "REQUEST HEADERS:")
        originalRequest.headers.forEach { (name, value) ->
            Log.d("S3Debug", "  $name: $value")
        }

        val response = chain.proceed(originalRequest)

        Log.d("S3Debug", "RESPONSE CODE: ${response.code}")
        if (!response.isSuccessful) {
            Log.d("S3Debug", "ERROR BODY: ${response.body?.string()}")
        }
        Log.d("S3Debug", "════════════════════════════════════════")

        return response
    }
}