package com.clerodri.binnacle.home.presentation

import com.clerodri.binnacle.core.components.SnackBarType


sealed class HomeUiEvent {
    data class ShowAlert(val message: String, val type: SnackBarType) : HomeUiEvent()
    data object LogOut : HomeUiEvent()
}