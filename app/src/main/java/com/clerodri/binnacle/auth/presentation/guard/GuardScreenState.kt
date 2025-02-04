package com.clerodri.binnacle.auth.presentation.guard


/**
 * Sealed class representing the state of the Guard Login view.
 * */

sealed class GuardScreenState {
    data class GuardState(
        var identifier: String = "",
        var identifierError: String? = null,
        var loginEnable: Boolean = false,
        var isAdminScreen: Boolean = false,
        var isLoading: Boolean = false,
    )
}
