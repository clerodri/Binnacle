package com.clerodri.binnacle.addreport

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * UiState for the Add Report screen
 */
data class AddReportUiState(
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
)
@HiltViewModel
class AddReportViewModel @Inject constructor():ViewModel() {

    private val _uiState = MutableStateFlow(AddReportUiState())
    val state = _uiState.asStateFlow()



    fun updateTitle(newTitle: String) {
        _uiState.update {
            it.copy(title = newTitle)
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update {
            it.copy(description = newDescription)
        }
    }

    fun clearFields() {
        _uiState.update {
            it.copy(title = "", description = "")
        }

    }
}