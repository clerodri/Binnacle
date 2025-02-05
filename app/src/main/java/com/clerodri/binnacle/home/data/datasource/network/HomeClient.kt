package com.clerodri.binnacle.home.data.datasource.network

import com.clerodri.binnacle.home.data.datasource.network.dto.LocalityResponse
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeClient{

    @GET("api/v1/localities/{id}")
    suspend fun getLocality(@Path("id") localityId: Int): Response<LocalityResponse>
}