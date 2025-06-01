package com.clerodri.binnacle.authentication.data.datasource.network

import android.util.Log
import com.clerodri.binnacle.authentication.data.datasource.network.dto.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class LoginService @Inject constructor(private val loginClient: LoginClient) {


    suspend fun doLogin(identification: String): LoginResponse {
        Log.d("RR", "doLogin")
        return withContext(Dispatchers.IO) {
            val response = loginClient.doLoginCall(LoginRequest(identification))
            Log.d("RR", "LoginService doLogin called $response")
            if(response.isSuccessful){
                Log.d("RR", "LoginService ${response.body()}")
                response.body() ?: throw HttpException(response)
            }else{
                Log.d("RR", "LoginService HttpException $response")
                throw HttpException(response)
            }
        }


    }
}