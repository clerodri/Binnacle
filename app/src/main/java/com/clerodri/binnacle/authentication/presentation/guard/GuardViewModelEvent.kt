package com.clerodri.binnacle.authentication.presentation.guard

import com.clerodri.binnacle.authentication.domain.model.Locality

/**
 * Events that the UI(Guard Screen) can send to the [GuardViewModel]
 *
 */
sealed class GuardViewModelEvent {
    data object ClearFields : GuardViewModelEvent()
    data object LoginGuard : GuardViewModelEvent()

    // data object  LogOut : GuardViewModelEvent()
    data class UpdateIdentifier(val identifier: String) : GuardViewModelEvent()
    data class SelectOption(val locality: Locality) : GuardViewModelEvent()
    object ProceedToLogin : GuardViewModelEvent()
    object BackToSelection : GuardViewModelEvent()
}