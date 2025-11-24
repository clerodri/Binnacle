package com.clerodri.binnacle.authentication.data.datasource.network

import com.clerodri.binnacle.authentication.data.datasource.network.dto.UrbanizationDto
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Author: Ronaldo R.
 * Date:  11/22/2025
 * Description:
 **/
class LocalityService @Inject constructor(
    private val localityClient: LocalityClient
) {

    suspend fun findLocalities(): List<UrbanizationDto> {
        return withContext(Dispatchers.IO) {
            val response = localityClient.findLocalities()
            println("TEST: ${response.code()}")

            if (response.isSuccessful) {
                response.body() ?: throw HttpException(response)
            } else {
                throw HttpException(response)
            }
        }
    }
}