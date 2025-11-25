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
        val token = runBlocking {
            localDataSource.getAuthData().first()?.accessToken ?: ""
        }
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
           .addHeader("Authorization", "Bearer $token")
           .addHeader("Content-Type", "application/json")
           .build()
        Log.d("AuthInterceptor", "Added JWT to request: ${originalRequest.url}")
        return chain.proceed(newRequest)
    }

}