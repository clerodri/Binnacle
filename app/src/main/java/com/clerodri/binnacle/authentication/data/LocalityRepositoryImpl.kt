package com.clerodri.binnacle.authentication.data


import com.clerodri.binnacle.authentication.data.datasource.network.LocalityService
import com.clerodri.binnacle.authentication.data.datasource.network.toDomain
import com.clerodri.binnacle.authentication.domain.model.Locality
import com.clerodri.binnacle.authentication.domain.repository.LocalityRepository
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import jakarta.inject.Inject
import retrofit2.HttpException
import java.io.IOException

/**
 * Author: Ronaldo R.
 * Date:  11/22/2025
 * Description:
 **/
class LocalityRepositoryImpl @Inject constructor(
    private val localityService: LocalityService
) : LocalityRepository {

    override suspend fun findLocalities(): Result<List<Locality>, DataError.LocalityError> {
        return try {
            val response = localityService.findLocalities()
            val localities = response.toDomain()
            Result.Success(localities)
        } catch (e: HttpException) {
            val error = when (e.code()) {
                404 -> DataError.LocalityError.ROUTES_NOT_FOUND
                in 500..599 -> DataError.LocalityError.SERVICE_UNAVAILABLE
                else -> DataError.LocalityError.SERVICE_UNAVAILABLE
            }
            Result.Failure(error)
        } catch (e: IOException) {
            Result.Failure(DataError.LocalityError.SERVICE_UNAVAILABLE)
        } catch (e: Exception) {
            Result.Failure(DataError.LocalityError.SERVICE_UNAVAILABLE)
        }
    }

}