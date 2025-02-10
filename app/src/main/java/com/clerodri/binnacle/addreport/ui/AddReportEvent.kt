package com.clerodri.binnacle.addreport.ui

import com.clerodri.binnacle.addreport.domain.Report

sealed interface AddReportEvent {
    data class OnAddReport(val report: Report) : AddReportEvent
    data class OnUpdateTitle(val title: String) : AddReportEvent
    data class OnUpdateDescription(val description: String) : AddReportEvent
    data object OnNavigateToHome : AddReportEvent
    data object OnTakePhoto : AddReportEvent
    data object OnOpenCamera : AddReportEvent
    data object OnCloseCamera : AddReportEvent
    data object NoCameraAllowed : AddReportEvent


}