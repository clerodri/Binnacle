package com.clerodri.binnacle.core.di

import android.content.Context
import com.clerodri.binnacle.auth.data.AuthRepositoryImpl
import com.clerodri.binnacle.auth.data.network.AuthInterceptor
import com.clerodri.binnacle.auth.data.network.LoginClient
import com.clerodri.binnacle.auth.data.network.LoginService
import com.clerodri.binnacle.auth.domain.model.IdentificationGuardValidator
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import com.clerodri.binnacle.util.AuthPreferences
import com.clerodri.binnacle.util.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://192.168.100.70:8080/api/v1/"

    @Provides
    @Singleton
    fun providesAuthInterceptor(authPreferences: AuthPreferences): AuthInterceptor {
        return AuthInterceptor(authPreferences)
    }

    @Provides
    @Singleton
    fun provideAuthPreferences(@ApplicationContext context: Context): AuthPreferences {
        return AuthPreferences(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }


    @Provides
    @Singleton
    fun provideLoginClient(retrofit: Retrofit): LoginClient {
        return retrofit.create(LoginClient::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    }

    @Provides
    @Singleton
    fun provideRepository(api: LoginService): AuthRepository {
        return AuthRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideIdentificationValidator(): IdentificationGuardValidator {
        return IdentificationGuardValidator()
    }
}