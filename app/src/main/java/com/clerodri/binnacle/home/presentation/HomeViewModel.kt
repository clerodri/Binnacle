package com.clerodri.binnacle.home.presentation

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.auth.data.storage.UserData
import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.auth.domain.usecase.UserUseCase
import com.clerodri.binnacle.home.domain.model.CheckStatus
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.domain.usecase.HomeUseCase
import com.clerodri.binnacle.home.domain.usecase.LocalityUseCase
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
    private val homeUseCase: HomeUseCase,
    private val userUseCase: UserUseCase,
    private val localityUseCase: LocalityUseCase
) : ViewModel() {


    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()


    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()


    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes.asStateFlow()

    private var timerJob: Job? = null

    private val _eventChannel = Channel<HomeUiEvent>()
    internal fun getEventChannel() = _eventChannel.receiveAsFlow()

    init {
        loadUserData()
        loadHomeData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userUseCase.getUserData().collectLatest { data ->
                Log.d("RR", "loadUserData called $data")
                if (data != null) {
                    _userData.update { data }

                    loadLocality(data.id.toInt())
                    loadCheckInStatus(data)
                }
            }
        }
    }

    private suspend fun loadLocality(id: Int) {
        val locality = localityUseCase.invoke(id)
        Log.d("RR", "loadUserData locality called $locality")
        _userData.update {
            it?.copy(localityId = locality?.name!!)
        }
        _routes.update {
            locality?.routes?.sortedBy { it.id }!!
        }
    }

    private suspend fun loadCheckInStatus(data: UserData) {

        when (val result = homeUseCase.validateCheckStatus(data.id.toInt())) {


            is Result.Failure -> {
                handleCheckStatusFailure(result.error)
            }

            is Result.Success -> {
                when (result.data) {
                    CheckStatus.STARTED -> {
                        _state.update {
                            it.copy(
                                enableCheck = true,
                                isCheckedIn = true,
                                isCheckedOut = false
                            )
                        }
                    }

                    CheckStatus.DONE -> {
                        _state.update {
                            it.copy(
                                enableCheck = false,
                                isCheckedIn = true,
                                isCheckedOut = true
                            )
                        }
                    }

                    CheckStatus.READY -> {
                        _state.update {
                            it.copy(
                                enableCheck = true,
                                isCheckedIn = false,
                                isCheckedOut = false
                            )
                        }
                    }
                }
            }
        }


    }


    private fun loadHomeData() {
        Log.d("HomeViewModel", "loadSavedState called")
        viewModelScope.launch {
            homeUseCase.getHomeData().collectLatest { savedState ->
                _state.update {
                    it.copy(
                        currentIndex = savedState?.currentIndex!!,
                        isStarted = savedState.isStarted,
                        isRoundBtnEnabled = savedState.isRoundBtnEnabled,
                        timer = savedState.timer,
                        enableCheck = savedState.enableCheck,
                        isCheckedIn = savedState.isCheckedIn,
                        isCheckedOut = savedState.isCheckedOut
                    )
                }

                if (savedState?.isStarted!! && savedState.elapsedSeconds > 0 && timerJob == null) {
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


    // Handle user events
    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.OnCheck -> {
                // onZoomAll()  <- example
                Log.d("HomeViewModel", "OnCheckIn")

                if (_state.value.isCheckedIn) {
                    checkOut()
                } else {
                    checkIn()
                }
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
                saveHomeData()
            }

            HomeViewModelEvent.OnLogOutRequested -> {
                if (_state.value.isStarted) {
                    sendScreenEvent(event = HomeUiEvent.ShowSnackbar("Debe Finalizar la RONDA actual"))
                }
            }

            HomeViewModelEvent.OnCheckOut -> {
                _state.update { it.copy(isCheckedOut = true) }
            }
        }
    }

    fun clearUserData() {
        viewModelScope.launch {
            userUseCase.clearUserData()
            homeUseCase.clearHomeData()
        }
    }

    fun resetCheck() {
        _state.update {
            it.copy(isCheckedIn = false, isCheckedOut = false, enableCheck = true)
        }
        saveHomeData()
    }

    private fun checkOut() {
        viewModelScope.launch {
            when (val result = homeUseCase.makeCheckOut(_userData.value?.id?.toInt())) {
                is Result.Failure -> {
                    handleCheckStatusFailure(result.error)
                }

                is Result.Success -> {
                    _state.update {
                        it.copy(isCheckedOut = true, enableCheck = false)
                    }
                }
            }
            saveHomeData()
        }


    }

    private fun checkIn() {
        viewModelScope.launch {
            val checkIn = homeUseCase.makeCheckIn(_userData.value?.id?.toInt())
            Log.d("RR", "checkIn ViewModel $checkIn")
            _state.update {
                it.copy(isCheckedIn = true)
            }
        }
        saveHomeData()
    }


    private fun startTimer(resumeFrom: Int = 0) {
        if (timerJob?.isActive == true) {
            Log.d("HomeViewModel", "Timer already running. Ignoring start request.")
            return
        }
        Log.d("HomeViewModel", "startTimer called")
        _state.update {
            it.copy(
                isStarted = true,
                isRoundBtnEnabled = _routes.value.size == 1,
                elapsedSeconds = resumeFrom
            )
        }

        timerJob = viewModelScope.launch {
            var currentTime = resumeFrom
            while (true) {
                delay(1000L)
                currentTime++
                _state.update {
                    it.copy(timer = formatTime(currentTime), elapsedSeconds = currentTime)
                }
                saveHomeData()
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
        saveHomeData()


    }

    private fun saveHomeData() {
        viewModelScope.launch {
            homeUseCase.saveHomeData(
                Home(
                    currentIndex = _state.value.currentIndex,
                    isStarted = _state.value.isStarted,
                    isRoundBtnEnabled = _state.value.isRoundBtnEnabled,
                    timer = _state.value.timer,
                    elapsedSeconds = _state.value.elapsedSeconds,
                    isCheckedIn = _state.value.isCheckedIn,
                    enableCheck = _state.value.enableCheck,
                    isCheckedOut = _state.value.isCheckedOut
                )
            )
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }


    private fun handleCheckStatusFailure(error: DataError.Check) {
        when (error) {
            DataError.Check.CONFLICT -> {
                _state.update {
                    it.copy(
                        enableCheck = false,
                        isCheckedIn = false,
                        isCheckedOut = false
                    )
                }
            }
        }

    }


    // Send events back to the UI via the event channel
    private fun sendScreenEvent(event: HomeUiEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }


}