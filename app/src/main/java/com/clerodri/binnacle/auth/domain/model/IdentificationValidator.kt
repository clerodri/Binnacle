package com.clerodri.binnacle.auth.domain.model

import com.clerodri.binnacle.auth.domain.IdentificationError
import com.clerodri.binnacle.auth.domain.Result

class IdentificationValidator {


    fun validateIdentification(identification: String): Result<Unit, IdentificationError> {
        if (identification.length < 10) {
            return Result.Failure(IdentificationError.INVALID_IDENTIFICATION)
        }
        if (identification.length > 10) {
            return Result.Failure(IdentificationError.IDENTIFICATION_TO0_LONG)
        }
        return Result.Success(Unit)
    }


}