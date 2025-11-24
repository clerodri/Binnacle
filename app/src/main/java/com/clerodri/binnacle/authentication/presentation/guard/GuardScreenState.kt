package com.clerodri.binnacle.authentication.presentation.guard

import com.clerodri.binnacle.authentication.domain.model.Locality


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
        val showSelectionScreen: Boolean = true, // true to show selection first
        val selectedOption: Locality? = null,
        val availableOptions: List<Locality> = emptyList() // Your list items
    )
}
