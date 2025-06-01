package com.clerodri.binnacle.core.di

import android.app.Application
import android.content.Context
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomeClient(retrofit: Retrofit): HomeClient {
        return retrofit.create(HomeClient::class.java)
    }


    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): HomeInformation {
        return HomeInformation(context)
    }


    @Provides
    @Singleton
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO


    @Provides
    @Singleton
    fun provideHomeRepository(
        homeDataSource: HomeDataSource,
        homeService: HomeService
    ): HomeRepository {
        return HomeRepositoryImpl(homeDataSource, homeService)
    }
}