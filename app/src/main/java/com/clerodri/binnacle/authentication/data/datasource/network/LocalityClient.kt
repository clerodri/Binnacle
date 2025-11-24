package com.clerodri.binnacle.authentication.data.datasource.network

import com.clerodri.binnacle.authentication.data.datasource.network.dto.UrbanizationDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * Author: Ronaldo R.
 * Date:  11/22/2025
 * Description:
 **/
interface LocalityClient {

    @GET("api/v1/locality")
    suspend fun findLocalities(): Response<List<UrbanizationDto>>
}