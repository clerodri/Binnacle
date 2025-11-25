package com.clerodri.binnacle.core.di

import android.app.Application
import com.clerodri.binnacle.addreport.data.ReportRepositoryImpl
import com.clerodri.binnacle.addreport.data.datasource.network.ReportClient
import com.clerodri.binnacle.addreport.data.datasource.network.ReportService
import com.clerodri.binnacle.addreport.domain.ReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportModule {

    @Provides
    @Singleton
    fun provideReportClient(retrofit: Retrofit): ReportClient {
        return retrofit.create(ReportClient::class.java)
    }

    @Provides
    @Singleton
    fun provideReportRepository(
        reportService: ReportService,
        application: Application,
        @S3OkHttpClient okHttpClient: OkHttpClient
    ): ReportRepository {
        return ReportRepositoryImpl(reportService, application, okHttpClient)
    }


}