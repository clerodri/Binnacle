package com.clerodri.binnacle.core.di

import com.clerodri.binnacle.BuildConfig
import com.clerodri.binnacle.authentication.data.datasource.local.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class S3OkHttpClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {



    @Provides
    @Singleton
    @ApiOkHttpClient
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.HEADERS
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @S3OkHttpClient
    fun provideS3OkHttpClient(s3DebugInterceptor: S3DebugInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .connectTimeout(60L, TimeUnit.SECONDS)  // ← Mayor timeout para upload
            .readTimeout(60L, TimeUnit.SECONDS)
            .writeTimeout(60L, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(s3DebugInterceptor)
            .build()
    }


    @Provides
    @Singleton
    fun providesAuthInterceptor(localDataSource: LocalDataSource): AuthInterceptor {
        return AuthInterceptor(localDataSource)
    }

    @Provides
    @Singleton
    fun providesS3Interceptor(): S3DebugInterceptor{
        return S3DebugInterceptor();
    }


    @Provides
    @Singleton
    fun provideRetrofit(@ApiOkHttpClient okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    }


}