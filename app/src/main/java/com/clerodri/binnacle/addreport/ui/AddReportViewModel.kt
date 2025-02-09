package com.clerodri.binnacle.addreport.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.addreport.domain.AddReportUseCase
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.home.presentation.HomeUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
class AddReportViewModel @Inject constructor(
    private val addReportUseCase: AddReportUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReportUiState())
    val state = _uiState.asStateFlow()

    private val _eventChannel = Channel<ReportUiEvent>()
    internal fun getEventChannel() = _eventChannel.receiveAsFlow()


    fun onReportEvent(event: AddReportEvent) {
        when (event) {
            is AddReportEvent.OnAddReport -> {
                createReport(event.routeId, event.roundId, event.localityId)
                //call api post
            }

            AddReportEvent.OnNavigateToHome -> {
                _uiState.update {
                    it.copy(description = "", title = "")
                }
            }

            AddReportEvent.OnTakePhoto -> {

            }

            is AddReportEvent.OnUpdateDescription -> {
                _uiState.update {
                    it.copy(description = event.description)
                }
            }

            is AddReportEvent.OnUpdateTitle -> {
                _uiState.update {
                    it.copy(title = event.title)
                }
            }
        }

    }

    private fun createReport(routeId: Int, roundId: Int, localityId: Int) {
        viewModelScope.launch {
            when (val result = addReportUseCase(
                content = _uiState.value.description,
                imgUrl = "testingImgUrl",
                routeId = routeId,
                roundId = roundId,
                localityId = localityId
            )) {
                is Result.Failure -> {
                    when (result.error) {
                        DataError.Report.REQUEST_TIMEOUT -> {
                            Log.d("AddReportViewModel", "createReport: ${result.error}")
                            sendScreenEvent(event = ReportUiEvent.onError(result.error.name))
                        }

                        DataError.Report.NO_INTERNET -> {
                            sendScreenEvent(event = ReportUiEvent.onError(result.error.name))
                            Log.d("AddReportViewModel", "createReport: ${result.error}")
                        }
                    }
                }

                is Result.Success -> {
                    Log.d("AddReportViewModel", "createReport: ${result.data}")
                    _uiState.update {
                        it.copy(description = "", title = "")
                    }
                    sendScreenEvent(event = ReportUiEvent.onBack)

                }
            }
        }
    }


    private fun sendScreenEvent(event: ReportUiEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }
}