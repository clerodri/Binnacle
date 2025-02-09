package com.clerodri.binnacle.home.domain.repository

import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Locality
import com.clerodri.binnacle.home.domain.model.Round
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getLocalityInfo(localityId: Int): Locality?

    suspend fun getHomeData(): Flow<Home?>

    suspend fun saveHomeData(home: Home)

    suspend fun clearHomeData()


    suspend fun makeCheckIn(id: Int): Result<CheckIn, DataError.CheckError>

    suspend fun makeCheckOut(id: Int): Result<Unit, DataError.CheckError>

    suspend fun validateCheckIn(id: Int): Result<ECheckIn, DataError.CheckError>

    suspend fun startRound(guardId: Int): Result<Round, DataError.Network>

    suspend fun stopRound(roundId: Int): Result<Unit, DataError.Network>
}