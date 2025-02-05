package com.clerodri.binnacle.core.di

import android.content.Context
import com.clerodri.binnacle.auth.data.AuthRepositoryImpl
import com.clerodri.binnacle.auth.data.datasource.local.LocalDataSource
import com.clerodri.binnacle.auth.data.datasource.network.LoginClient
import com.clerodri.binnacle.auth.data.datasource.network.LoginService
import com.clerodri.binnacle.auth.data.storage.UserInformation
import com.clerodri.binnacle.auth.domain.model.IdentificationValidator
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import com.clerodri.binnacle.home.data.HomeRepositoryImpl
import com.clerodri.binnacle.home.data.datasource.local.HomeDataSource
import com.clerodri.binnacle.home.data.datasource.network.HomeClient
import com.clerodri.binnacle.home.data.datasource.network.HomeService
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

    private const val BASE_URL = "http://192.168.100.70:8080/"




    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }



    @Provides
    @Singleton
    fun providesAuthInterceptor(localDataSource: LocalDataSource): AuthInterceptor {
        return AuthInterceptor(localDataSource)
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
    fun provideHomeClient(retrofit: Retrofit): HomeClient {
        return retrofit.create(HomeClient::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: LoginService,
        localDataSource: LocalDataSource
    ): AuthRepository {
        return AuthRepositoryImpl(api, localDataSource)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(
        homeDataSource: HomeDataSource,
        homeService: HomeService
    ): HomeRepository {
        return HomeRepositoryImpl(homeDataSource, homeService)
    }

    @Provides
    @Singleton
    fun provideIdentificationValidator(): IdentificationValidator {
        return IdentificationValidator()
    }
}