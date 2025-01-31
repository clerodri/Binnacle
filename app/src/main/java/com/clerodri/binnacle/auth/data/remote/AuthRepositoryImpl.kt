package com.clerodri.binnacle.auth.data.remote


import android.util.Log
import com.clerodri.binnacle.auth.domain.model.DataError
import com.clerodri.binnacle.auth.domain.model.Guard
import com.clerodri.binnacle.auth.domain.model.Result
import com.clerodri.binnacle.auth.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException

internal class AuthRepositoryImpl(
    private val api: LoginApi
): AuthRepository {



    override suspend fun login(identification: String):Result<Guard, DataError.Network> {
        return try {
            val response =  api.doLoginCall(LoginRequest(identification))
            Log.d("RR", "Response $response")
            Result.Success(Guard(1, "test"))
        }catch (e: HttpException){
            when(e.code()){
                404 -> Result.Failure(DataError.Network.GUARD_NOT_FOUND)
                503 -> Result.Failure(DataError.Network.NO_INTERNET)
                else -> {Result.Failure(DataError.Network.REQUEST_TIMEOUT)}
            }
        }catch (io:IOException){
            Result.Failure(DataError.Network.REQUEST_TIMEOUT)
        }
    }
}