package com.clerodri.binnacle.home.data.datasource.network

import android.util.Log
import com.clerodri.binnacle.home.domain.model.Locality
import com.clerodri.binnacle.home.domain.model.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HomeService @Inject constructor(
    private val homeClient: HomeClient
) {


    suspend fun getLocalities(localityId: Int): Locality {
        Log.d("RR", "HomeService getLocalities called $localityId")
        return withContext(Dispatchers.IO) {
            val response = homeClient.getLocality(localityId).body()
            Log.d("RR", "HomeService getLocalities called $response")
            val routes = response?.routes?.map {
                Route(it.id, it.localityId, it.name)
            }
            response?.let { Locality(it.id, response.name, routes) }!!
        }

    }
}