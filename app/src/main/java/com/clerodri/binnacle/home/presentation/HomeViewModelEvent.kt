package com.clerodri.binnacle.home.presentation
/**
 * Events that the UI can send to the [HomeViewModel]
 */
sealed class HomeViewModelEvent {
    data object  OnCheckIn: HomeViewModelEvent()
    data object  OnCheckOut: HomeViewModelEvent()
    data object  MakeReport: HomeViewModelEvent()
    data object  StartRound: HomeViewModelEvent()
    data object  StopRound: HomeViewModelEvent()
}