package com.clerodri.binnacle.home.domain.model

data class Home(
    val currentIndex: Int,
    val isStarted: Boolean,
    val isRoundBtnEnabled: Boolean,
    val timer: String,
    val elapsedSeconds: Int,
    val isCheckedIn: Boolean,
    val enableCheck: Boolean
)
