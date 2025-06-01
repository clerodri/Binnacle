package com.clerodri.binnacle.home.domain.model

data class Home(
    val currentIndex: Int,
    val isStarted: Boolean,
    val elapsedSeconds: Long,
    val roundId: Long,
    val checkId: Int
)
