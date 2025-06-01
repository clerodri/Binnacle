package com.clerodri.binnacle.app

import com.clerodri.binnacle.core.AuthManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AuthManagerEntryPoint {
    fun authManager(): AuthManager
}