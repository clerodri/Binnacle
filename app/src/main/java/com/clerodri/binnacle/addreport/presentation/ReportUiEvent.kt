package com.clerodri.binnacle.addreport.presentation

import com.clerodri.binnacle.core.components.SnackBarType

sealed interface ReportUiEvent {
    data class OnBackWithSuccess(val isSuccess: Boolean) : ReportUiEvent
    data class OnError(val message:String, val type: SnackBarType): ReportUiEvent
    data object OnSendingReport : ReportUiEvent
}