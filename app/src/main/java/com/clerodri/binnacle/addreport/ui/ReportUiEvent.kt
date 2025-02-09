package com.clerodri.binnacle.addreport.ui

sealed interface ReportUiEvent {
    data object onBack : ReportUiEvent
    data class onError(val message:String): ReportUiEvent
}