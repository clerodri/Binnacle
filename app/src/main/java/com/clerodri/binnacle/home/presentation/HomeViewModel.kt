package com.clerodri.binnacle.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.auth.domain.model.UserData
import com.clerodri.binnacle.auth.domain.usecase.UserUseCase
import com.clerodri.binnacle.home.domain.model.CheckStatus
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.domain.usecase.HomeUseCase
import com.clerodri.binnacle.home.domain.usecase.LocalityUseCase
import com.clerodri.binnacle.util.formatTime
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
        viewModelScope.launch {
            loadUserState()
            loadHomeState()
        }
    }


    // Handle user events
    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.OnCheck -> {
                Log.d("HomeViewModel", "OnCheck")
                if (_state.value.isCheckedIn) checkOut() else checkIn()
            }

            HomeViewModelEvent.StartRound -> {
                Log.d("HomeViewModel", "Start round")
                startTimer()
            }

            HomeViewModelEvent.StopRound -> {
                Log.d("HomeViewModel", "Stop round")
                stopTimer()
            }

            HomeViewModelEvent.UpdateIndex -> {
                if (_state.value.currentIndex == _routes.value.size - 2) {
                    _state.update {
                        it.copy(isRoundBtnEnabled = true, currentIndex = it.currentIndex + 1)
                    }

                } else {
                    _state.update {
                        it.copy(currentIndex = it.currentIndex + 1)
                    }
                }

                saveHomeState()
            }

            HomeViewModelEvent.OnLogOutRequested -> sendScreenEvent(event = HomeUiEvent.ShowAlert)

            HomeViewModelEvent.OnCheckOut -> _state.update { it.copy(isCheckedOut = true) }
        }
    }


    fun onLogOut() {

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            Log.d("OO", "viewModel onLogOut - START")
            userUseCase.clearUserData()
            Log.d("OO", "viewModel onLogOut - Home cleared")
            homeUseCase.clearHomeData()
            Log.d("OO", "viewModel onLogOut - User data cleared")
            delay(500)
            sendScreenEvent(event = HomeUiEvent.LogOut)
            delay(500)
            Log.d("OO", "viewModel onLogOut - Navigation triggered")
            _state.value = _state.value.copy(isLoading = false)
        }
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
            saveHomeState()
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
        saveHomeState()
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
                saveHomeState()
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
        saveHomeState()


    }

    private fun saveHomeState() {
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


    private fun sendScreenEvent(event: HomeUiEvent) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }

    private fun loadUserState() {
        viewModelScope.launch {
            userUseCase.getUserData().collectLatest { data ->
                Log.d("RR", "loadUserData called $data")
                if (data != null) {
                    _userData.update { data }
                    if (data.id != null) {
                        fetchLocality(data.id)
                    }
                    fetchCheckInStatus(data.id!!)
                }
            }
        }
    }

    private suspend fun fetchLocality(id: Int) {
        val locality = localityUseCase.invoke(id)
        Log.d("RR", "loadUserData locality called $locality")
        _routes.update {
            locality?.routes?.sortedBy { it.id }!!
        }

        _state.update {
            it.copy(localityName = locality?.name!!, isRoundBtnEnabled = _routes.value.isNotEmpty())
        }


    }

    private suspend fun fetchCheckInStatus(id: Int) {

        when (val result = homeUseCase.validateCheckStatus(id)) {


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


    private fun loadHomeState() {
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


}