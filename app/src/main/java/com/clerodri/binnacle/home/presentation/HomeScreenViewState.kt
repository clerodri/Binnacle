package com.clerodri.binnacle.home.presentation

/**
 * Sealed class representing the state of the home view.
 */
sealed class HomeScreenViewState{

    data object  Loading: HomeScreenViewState()


}
