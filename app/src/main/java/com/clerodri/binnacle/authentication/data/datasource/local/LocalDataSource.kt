package com.clerodri.binnacle.authentication.data.datasource.local

import com.clerodri.binnacle.authentication.data.storage.UserInformation
import com.clerodri.binnacle.authentication.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val userInformation: UserInformation) {


    fun getUserData(): Flow<UserData?> = userInformation.userData


    suspend fun saveUserData(user: UserData) = userInformation.saveUserData(user)


    suspend fun clearUserData() = userInformation.clearUserData()
}