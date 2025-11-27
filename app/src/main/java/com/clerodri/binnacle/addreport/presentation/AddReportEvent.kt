package com.clerodri.binnacle.addreport.presentation


sealed interface AddReportEvent {
    data class OnAddReport(val title: String, val detail:String, val roundId: Int) : AddReportEvent
    data class OnUpdateTitle(val title: String) : AddReportEvent
    data class OnUpdateDescription(val description: String) : AddReportEvent
    data object ClearFields : AddReportEvent
    data object OnTakePhoto : AddReportEvent
    data class OnOpenCamera(val isFrontCamera: Boolean) : AddReportEvent
    data object OnCloseCamera : AddReportEvent
    data object NoCameraAllowed : AddReportEvent
    data class OnRemoveImage(val filename: String) : AddReportEvent
    data class OnImagePreview(val filename: String) : AddReportEvent
    data object OnSwitchCamera : AddReportEvent
}