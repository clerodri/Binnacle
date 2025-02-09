package com.clerodri.binnacle.authentication.presentation.guard
/**
 * Events that the UI(Guard Screen) can send to the [GuardViewModel]
 *
 */
sealed class GuardViewModelEvent {
    data object  ClearFields: GuardViewModelEvent()
    data object  LoginGuard : GuardViewModelEvent()
    data object  LogOut : GuardViewModelEvent()
    data class UpdateIdentifier(val identifier: String) : GuardViewModelEvent()
}