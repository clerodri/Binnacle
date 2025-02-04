package com.clerodri.binnacle.core.di

import android.content.Context
import com.clerodri.binnacle.auth.data.AuthRepositoryImpl
import com.clerodri.binnacle.auth.data.datasource.local.LocalDataSource
import com.clerodri.binnacle.auth.data.datasource.network.AuthInterceptor
import com.clerodri.binnacle.auth.data.datasource.network.LoginClient
import com.clerodri.binnacle.auth.data.datasource.network.LoginService
import com.clerodri.binnacle.auth.data.storage.UserInformation
import com.clerodri.binnacle.auth.domain.model.IdentificationValidator
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import com.clerodri.binnacle.home.data.HomeRepositoryImpl
import com.clerodri.binnacle.home.data.datasource.local.HomeDataSource
import com.clerodri.binnacle.home.data.storage.HomeInformation
import com.clerodri.binnacle.home.domain.repository.HomeRepository
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
    fun providesAuthInterceptor(userInformation: UserInformation): AuthInterceptor {
        return AuthInterceptor(userInformation)
    }

    @Provides
    @Singleton
    fun provideAuthPreferences(@ApplicationContext context: Context): UserInformation {
        return UserInformation(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): HomeInformation {
        return HomeInformation(context)
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
    fun provideAuthRepository(api: LoginService, localDataSource: LocalDataSource): AuthRepository {
        return AuthRepositoryImpl(api, localDataSource)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(homeDataSource: HomeDataSource): HomeRepository {
        return HomeRepositoryImpl(homeDataSource)
    }

    @Provides
    @Singleton
    fun provideIdentificationValidator(): IdentificationValidator {
        return IdentificationValidator()
    }
}