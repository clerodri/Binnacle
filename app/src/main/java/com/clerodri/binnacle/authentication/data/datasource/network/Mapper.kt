package com.clerodri.binnacle.authentication.data.datasource.network

import com.clerodri.binnacle.authentication.data.datasource.network.dto.UrbanizationDto
import com.clerodri.binnacle.authentication.domain.model.Locality

/**
 * Author: Ronaldo R.
 * Date:  11/22/2025
 * Description:
 **/
fun UrbanizationDto.toDomain(): Locality {
    return Locality(
        id = this.id,
        name = this.name,
        address = this.address
    )
}

fun List<UrbanizationDto>.toDomain(): List<Locality> {
    return this.map { it.toDomain() }
}