package com.clerodri.binnacle.app

import kotlinx.serialization.Serializable

@Serializable
object LoginGuard


@Serializable
object LoginAdmin


@Serializable
object  HomeScreen

@Serializable
data class  ReportScreen(val routeId:Int, val roundId:Int, val localityId:Int)

