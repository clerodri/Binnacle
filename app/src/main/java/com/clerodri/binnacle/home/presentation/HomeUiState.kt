package com.clerodri.binnacle.home.presentation

import com.clerodri.binnacle.home.domain.model.ECheckIn

/**
 * Sealed class representing the state of the home view.
 */

data class HomeUiState(
    val currentIndex: Int = 0,
    val isStarted: Boolean = false,
    val isLoading: Boolean = false,
    val elapsedSeconds: Long = 0,
    val localityName:String ="",
    val roundId:Int = 0,
    val checkInId:Int = 0,
    val checkStatus:ECheckIn = ECheckIn.READY
)
