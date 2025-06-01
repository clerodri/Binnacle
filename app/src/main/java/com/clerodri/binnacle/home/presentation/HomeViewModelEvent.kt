package com.clerodri.binnacle.home.presentation
/**
 * Events that the UI can send to the [HomeViewModel]
 */
sealed class HomeViewModelEvent {
    data object  OnCheck: HomeViewModelEvent()
    data object  OnLogOutRequested: HomeViewModelEvent()
    data object  StartRound: HomeViewModelEvent()
    data object  StopRound: HomeViewModelEvent()
    data object  UpdateIndex: HomeViewModelEvent()
    data object  OnLogOut: HomeViewModelEvent()
    data object  OnDestroy: HomeViewModelEvent()
    data object  OnReportSuccess: HomeViewModelEvent()
}