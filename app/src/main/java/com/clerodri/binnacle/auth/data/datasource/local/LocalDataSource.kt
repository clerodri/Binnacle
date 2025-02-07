package com.clerodri.binnacle.auth.data.datasource.local

import com.clerodri.binnacle.auth.data.storage.UserInformation
import com.clerodri.binnacle.auth.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val userInformation: UserInformation) {


    fun getUserData(): Flow<UserData?> = userInformation.userData


    suspend fun saveUserData(user: UserData) = userInformation.saveUserData(user)


    suspend fun clearUserData() = userInformation.clearUserData()
}