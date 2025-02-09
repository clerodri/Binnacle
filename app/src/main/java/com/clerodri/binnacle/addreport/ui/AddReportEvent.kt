package com.clerodri.binnacle.addreport.ui

sealed interface AddReportEvent{
    data class  OnAddReport(val routeId:Int, val roundId:Int, val localityId:Int) : AddReportEvent
    data class OnUpdateTitle(val title:String) : AddReportEvent
    data class OnUpdateDescription(val description:String) : AddReportEvent
    data object OnNavigateToHome: AddReportEvent
    data object OnTakePhoto: AddReportEvent
}