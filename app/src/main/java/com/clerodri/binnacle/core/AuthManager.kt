package com.clerodri.binnacle.core

import android.util.Log
import com.clerodri.binnacle.authentication.data.datasource.local.LocalDataSource
import com.clerodri.binnacle.authentication.domain.model.AuthData
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first


@Singleton
class AuthManager @Inject constructor(private val localDataSource: LocalDataSource) {
    private val _userData = MutableStateFlow<AuthData?>(null)
    val userData: StateFlow<AuthData?> = _userData.asStateFlow()

    suspend fun loadUserData() {
        _userData.value = localDataSource.getAuthData().first()
        Log.d("AuthManager", "loadUserData called ${_userData.value}")

    }

    suspend fun clearUserData() {
        _userData.value = null

        localDataSource.clearAuthData()

    }
}