package com.clerodri.binnacle.auth.data.network

import com.clerodri.binnacle.util.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authPreferences: AuthPreferences
) : Interceptor {




    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking { "" }
        val request = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}