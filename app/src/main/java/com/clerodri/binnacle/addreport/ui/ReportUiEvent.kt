package com.clerodri.binnacle.addreport.ui

import com.clerodri.binnacle.core.components.SnackBarType

sealed interface ReportUiEvent {
    data object onBack : ReportUiEvent
    data class onBackWithSuccess(val isSuccess: Boolean) : ReportUiEvent
    data class onError(val message:String,val type: SnackBarType): ReportUiEvent
    data object onSendingReport : ReportUiEvent
}