package com.clerodri.binnacle.authentication.presentation.admin
/**
 * Events that the UI(Guard Screen) can send to the [GuardViewModel]
 *
 */
sealed class AdminViewModelEvent {
    data object  LoginAdmin : AdminViewModelEvent()
    data object  ClearFields: AdminViewModelEvent()
    data class UpdateEmail(val email: String) : AdminViewModelEvent()
    data class UpdatePassword(val password: String) : AdminViewModelEvent()
}