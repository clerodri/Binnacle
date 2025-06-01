package com.clerodri.binnacle.core.di

import android.content.Context
import com.clerodri.binnacle.authentication.data.AuthRepositoryImpl
import com.clerodri.binnacle.authentication.data.datasource.local.LocalDataSource
import com.clerodri.binnacle.authentication.data.datasource.network.LoginClient
import com.clerodri.binnacle.authentication.data.datasource.network.LoginService
import com.clerodri.binnacle.authentication.data.storage.AuthInformation
import com.clerodri.binnacle.authentication.domain.model.IdentificationValidator
import com.clerodri.binnacle.authentication.domain.repository.AuthRepository
import com.clerodri.binnacle.core.AuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {


    @Provides
    @Singleton
    fun provideAuthPreferences(@ApplicationContext context: Context): AuthInformation {
        return AuthInformation(context)
    }

    @Provides
    @Singleton
    fun provideLoginClient(retrofit: Retrofit): LoginClient {
        return retrofit.create(LoginClient::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: LoginService,
        localDataSource: LocalDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(
            api,
            localDataSource
        )
    }

    @Provides
    @Singleton
    fun provideIdentificationValidator(): IdentificationValidator {
        return IdentificationValidator()
    }


    @Provides
    @Singleton
    fun provideAuthManager(localDataSource: LocalDataSource): AuthManager {
        return AuthManager(localDataSource)
    }



}