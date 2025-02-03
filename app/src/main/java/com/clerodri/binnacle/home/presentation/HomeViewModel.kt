package com.clerodri.binnacle.home.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.home.domain.Route
import com.clerodri.binnacle.util.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val preferences = DataStoreManager(application)

    //    // Event channel to send events to The UI
    private val _eventChannel = Channel<HomeUiEvent>()
    internal fun getEventChannel() = _eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(HomeScreenViewState())
    val state: StateFlow<HomeScreenViewState> = _state.asStateFlow()

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes.asStateFlow()

    private var timerJob: Job? = null


    init {
        loadSavedState()
        fetchRoutes()
    }

    private fun loadSavedState() {
        Log.d("HomeViewModel", "loadSavedState called")

        viewModelScope.launch {
            preferences.homeScreenState.collectLatest { savedState ->
                _state.value = savedState

                if (savedState.isStarted && savedState.elapsedSeconds > 0 && timerJob == null) {
                    Log.d(
                        "HomeViewModel",
                        "Resuming timer from ${savedState.elapsedSeconds} seconds"
                    )
                    startTimer(resumeFrom = savedState.elapsedSeconds)
                } else {
                    Log.d("HomeViewModel", "Timer is not running on app start")
                }
            }

        }
    }


    private fun fetchRoutes() {
        viewModelScope.launch {
            try {
                val items = listOf(
                    Route("1", "SOLAR 14 FAM.HOOLIGAN"),
                    Route("3", "SOLAR Y FAM.HIGGINS"),
                    Route("5", "SOLAR 51"),
                    Route("6", "CANCHA DE TENNIS")
                )
                _routes.value = items.sortedBy { it.id.toInt() }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching routes: ${e.message}")
            }
        }
    }

    // Handle user events
    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.OnCheckIn -> {
                // onZoomAll()  <- example
            }

            HomeViewModelEvent.MakeReport -> {

            }

            HomeViewModelEvent.OnCheckOut -> {

            }

            HomeViewModelEvent.StartRound -> {
                Log.d("HomeViewModel", "Start round")
                startTimer()
            }

            HomeViewModelEvent.StopRound -> {
                Log.d("HomeViewModel", "Stop round")
                stopTimer()
                // api call - POST request with ronda data.
            }

            HomeViewModelEvent.UpdateIndex -> {
                if (_state.value.currentIndex == _routes.value.size - 2) {
                    _state.update {
                        it.copy(isRoundBtnEnabled = true)
                    }

                }
                _state.update {
                    it.copy(currentIndex = it.currentIndex + 1)
                }
                saveState()
            }

            HomeViewModelEvent.OnLogOut -> {
               if(_state.value.isStarted){
                   sendScreenEvent(event = HomeUiEvent.ShowSnackbar("Debe Finalizar la RONDA actual"))
               }
            }
        }
    }


    private fun startTimer(resumeFrom: Int = 0) {
        if (timerJob?.isActive == true) {
            Log.d("HomeViewModel", "Timer already running. Ignoring start request.")
            return
        }
        Log.d("HomeViewModel", "startTimer called")
        _state.update {
            it.copy(isStarted = true, isRoundBtnEnabled = false, elapsedSeconds = resumeFrom)
        }

        timerJob = viewModelScope.launch {
            var currentTime = resumeFrom
            while (true) {
                delay(1000L)
                currentTime++
                _state.update {
                    it.copy(timer = formatTime(currentTime), elapsedSeconds = currentTime)
                }
                saveState()
            }
        }
    }

    private fun stopTimer() {
        Log.d("HomeViewModel", "stopTimer called")
        timerJob?.cancel()
        timerJob = null
        _state.update {
            it.copy(
                currentIndex = 0,
                isStarted = false,
                timer = "00:00:00",
                elapsedSeconds = 0
            )
        }
        viewModelScope.launch {
            saveState()
        }

    }

    private fun saveState() {
        viewModelScope.launch {
            preferences.saveState(_state.value)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

//    private fun onZoomAll() {  <- example
//        sendScreenEvent(MountainsScreenEvent.OnZoomAll)
//    }

    // Send events back to the UI via the event channel
    private fun sendScreenEvent(event: HomeUiEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }


}