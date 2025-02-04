package com.clerodri.binnacle.auth.data.datasource.network

import com.clerodri.binnacle.auth.data.storage.UserInformation
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userInformation: UserInformation
) : Interceptor {




    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking { "" }
        val request = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}