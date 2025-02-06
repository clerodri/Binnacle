package com.clerodri.binnacle.home.domain.repository

import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.model.CheckStatus
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Locality
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getLocalityInfo(localityId: Int) : Locality?

    suspend fun getHomeData(): Flow<Home?>

    suspend fun saveHomeData(home: Home)

    suspend fun clearHomeData()


    suspend fun makeCheckIn(id:Int):CheckIn

    suspend fun makeCheckOut(id:Int) : Result<Unit, DataError.Check>

    suspend fun validateCheckIn(id:Int): Result<CheckStatus, DataError.Check>
}