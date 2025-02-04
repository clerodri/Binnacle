package com.clerodri.binnacle.home.data.network

import com.clerodri.binnacle.home.data.network.dto.LocalityResponse
import retrofit2.Response
import retrofit2.http.GET

interface HomeClient{

    @GET("")
    suspend fun getLocality( localityId: Int):
            Response<LocalityResponse>
}