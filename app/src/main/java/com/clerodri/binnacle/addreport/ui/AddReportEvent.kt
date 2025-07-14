package com.clerodri.binnacle.addreport.ui


sealed interface AddReportEvent {
    data class OnAddReport(val title: String, val detail:String, val roundId: Int) : AddReportEvent
    data class OnUpdateTitle(val title: String) : AddReportEvent
    data class OnUpdateDescription(val description: String) : AddReportEvent
    data object ClearFields : AddReportEvent
    data object OnTakePhoto : AddReportEvent
    data object OnOpenCamera : AddReportEvent
    data object OnCloseCamera : AddReportEvent
    data object NoCameraAllowed : AddReportEvent
}