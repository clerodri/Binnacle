package com.clerodri.binnacle.auth.domain.model

class IdentificationGuardValidator {


    fun validateIdentification(identification: String):Result<Unit, IdentificationError >{
        if(identification.length<10){
            return Result.Failure(IdentificationError.INVALID_IDENTIFICATION)
        }
        if(identification.length>10){
            return Result.Failure(IdentificationError.IDENTIFICATION_TO0_LONG)
        }
        return Result.Success(Unit)
    }

    enum class IdentificationError: Error{
        INVALID_IDENTIFICATION,
        IDENTIFICATION_TO0_LONG,
    }
}