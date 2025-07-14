package com.clerodri.binnacle.home.domain.repository

import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.home.domain.model.CheckIn
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Round
import com.clerodri.binnacle.home.domain.model.Route
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getRoutes(): Result<List<Route>, DataError.LocalityError>

    suspend fun getHomeData(): Flow<Home?>

    suspend fun saveHomeData(home: Home)

    suspend fun clearHomeData()


    suspend fun makeCheckIn(id: Int): Result<CheckIn, DataError.CheckError>

    suspend fun makeCheckOut(id: Int): Result<Unit, DataError.CheckError>

    suspend fun validateCheckIn(id: Int): Result<ECheckIn, DataError.CheckError>

    suspend fun startRound(guardId: String): Result<Round, DataError.Network>

    suspend fun stopRound(roundId: Long): Result<Unit, DataError.Network>

    suspend fun validateSession(): Result<Unit, DataError>


}