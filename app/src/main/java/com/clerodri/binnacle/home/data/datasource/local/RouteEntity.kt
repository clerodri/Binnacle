package com.clerodri.binnacle.home.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.clerodri.binnacle.home.domain.model.Route

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val order: Int
)
fun RouteEntity.toDomain() = Route(id, name, order)
fun Route.toEntity() = RouteEntity(id, name, order)