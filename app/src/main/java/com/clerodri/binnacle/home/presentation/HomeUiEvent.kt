package com.clerodri.binnacle.home.presentation
/**
 * Events to send to the UI from the [HomeViewModel]
 */
sealed class HomeUiEvent {
    data class ShowAlert(val message: String) : HomeUiEvent()
    data object LogOut : HomeUiEvent()

}