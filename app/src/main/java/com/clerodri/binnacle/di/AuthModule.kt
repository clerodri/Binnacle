package com.clerodri.binnacle.di

import com.clerodri.binnacle.auth.data.remote.AuthRepositoryImpl
import com.clerodri.binnacle.auth.data.remote.LoginApi
import com.clerodri.binnacle.auth.domain.model.IdentificationGuardValidator
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    private const val BASE_URL = "http://192.168.100.70:8080/api/v1/"

    @Provides
    @Singleton
    fun provideApi(): LoginApi {
         return Retrofit.Builder()
             .baseUrl(BASE_URL)
             .addConverterFactory(GsonConverterFactory.create())
             .build()
             .create(LoginApi::class.java)
            }

    @Provides
    @Singleton
    fun provideRepository(api: LoginApi): AuthRepository {
        return AuthRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideIdentificationValidator(): IdentificationGuardValidator {
        return IdentificationGuardValidator()
    }
}