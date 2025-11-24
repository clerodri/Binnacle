package com.clerodri.binnacle.authentication.data.datasource.network

import com.clerodri.binnacle.authentication.data.datasource.network.dto.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class LoginService @Inject constructor(private val loginClient: LoginClient) {


    suspend fun doLogin(identification: String, selectedOption: String?): LoginResponse {

        return withContext(Dispatchers.IO) {
            val response = loginClient.doLoginCall(LoginRequest(identification, selectedOption))
            if (response.isSuccessful) {
                response.body() ?: throw HttpException(response)
            } else {
                throw HttpException(response)
            }
        }


    }
}