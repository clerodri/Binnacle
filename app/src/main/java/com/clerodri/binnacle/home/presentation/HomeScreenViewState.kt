package com.clerodri.binnacle.home.presentation

/**
 * Sealed class representing the state of the home view.
 */

data class HomeScreenViewState(
    val currentIndex: Int = 0,
    val isStarted: Boolean = false,
    val isLoading: Boolean = false,
    val isRoundBtnEnabled: Boolean = true,
    val timer:String = "00:00:00",
    val elapsedSeconds: Int = 0
)