package com.clerodri.binnacle.core.di

import android.util.Log
import com.clerodri.binnacle.authentication.data.datasource.local.LocalDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val localDataSource: LocalDataSource
) : Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("RR", "token RR")
        val token = runBlocking {
            localDataSource.getAuthData().first()?.accessToken ?: ""
        }
        Log.d("RR", "token $token")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }

}