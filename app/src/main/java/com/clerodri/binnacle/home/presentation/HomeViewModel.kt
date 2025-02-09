package com.clerodri.binnacle.home.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.authentication.domain.model.UserData
import com.clerodri.binnacle.authentication.domain.usecase.UserUseCase
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.domain.usecase.CheckInUseCase
import com.clerodri.binnacle.home.domain.usecase.CheckOutUseCase
import com.clerodri.binnacle.home.domain.usecase.CreateRoundUseCase
import com.clerodri.binnacle.home.domain.usecase.FinishRoundUseCase
import com.clerodri.binnacle.home.domain.usecase.GetCheckInStatusUseCase
import com.clerodri.binnacle.home.domain.usecase.HomeUseCase
import com.clerodri.binnacle.home.domain.usecase.LocalityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
    private val localityUseCase: LocalityUseCase,
    private val createRoundUseCase: CreateRoundUseCase,
    private val finishRoundUseCase: FinishRoundUseCase,
    private val checkInUseCase: CheckInUseCase,
    private val checkOutUseCase: CheckOutUseCase,
    private val getCheckInStatusUseCase: GetCheckInStatusUseCase,

    ) : ViewModel() {


    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _routes = MutableStateFlow<List<Route>>(emptyList())

    val routes: StateFlow<List<Route>> = _routes.asStateFlow()

    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var timerJob: Job? = null

    private val _eventChannel = Channel<HomeUiEvent>()
    internal fun getEventChannel() = _eventChannel.receiveAsFlow()


    init {
        viewModelScope.launch {
            loadUserState()
            loadHomeState()
        }
    }


    fun onEvent(event: HomeViewModelEvent) {
        when (event) {
            HomeViewModelEvent.OnCheck -> {

                when (_state.value.checkStatus) {
                    ECheckIn.STARTED -> checkOut()
                    ECheckIn.DONE -> sendScreenEvent(event = HomeUiEvent.ShowAlert("Ya esta registrado su Check-In"))
                    ECheckIn.READY -> checkIn()
                }
            }

            HomeViewModelEvent.StartRound -> {

                startTimer()
                createRound()
            }

            HomeViewModelEvent.StopRound -> {
                stopTimer()
                finishRound()

            }

            HomeViewModelEvent.UpdateIndex -> {

                viewModelScope.launch {
                    Log.d("RR", "updateIndex called ${_state.value.currentIndex}")
                    if (_state.value.currentIndex == _routes.value.size - 1) {

                        _state.value =
                            _state.value.copy(currentIndex = _state.value.currentIndex + 1)
                    } else {
                        _state.value =
                            _state.value.copy(currentIndex = _state.value.currentIndex + 1)
                    }
                    saveHomeState(this)
                }

            }

            HomeViewModelEvent.OnLogOutRequested ->
                sendScreenEvent(event = HomeUiEvent.ShowAlert("Debe finalizar la ronda para cerrar sesion"))

            HomeViewModelEvent.OnLogOut -> onLogOut()
            HomeViewModelEvent.OnDestroy -> {
                viewModelScope.launch {
                    saveHomeState(this)
                }
            }
        }
    }

    private fun finishRound() {
        viewModelScope.launch {
            when (val result = finishRoundUseCase(_state.value.roundId)) {
                is Result.Failure -> {
                    sendScreenEvent(event = HomeUiEvent.ShowAlert(result.error.name))
                }

                is Result.Success -> {
                    sendScreenEvent(event = HomeUiEvent.ShowAlert("Ronda finalizada exitosamente"))
                }
            }
            homeUseCase.clearHomeData()
        }
    }

    private fun createRound() {
        viewModelScope.launch {
            when (val result = createRoundUseCase.invoke(_userData.value?.id!!)) {
                is Result.Failure -> {
                    sendScreenEvent(event = HomeUiEvent.ShowAlert(result.error.name))
                }

                is Result.Success -> {
                    Log.d("ReportRepositoryImpl", "ROUNDID IN HOMEVIEWMODEL: $result")
                    _state.value = _state.value.copy(roundId = result.data.id)
                    delay(500)
                    sendScreenEvent(event = HomeUiEvent.ShowAlert("Ronda iniciada"))
                }
            }
        }
    }

    private fun onLogOut() {

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userUseCase.clearUserData()
            homeUseCase.clearHomeData()
            delay(500)
            sendScreenEvent(event = HomeUiEvent.LogOut)
            delay(500)
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun checkOut() {
        viewModelScope.launch {
            when (val result = checkOutUseCase(_state.value.checkInId)) {
                is Result.Failure -> {
                    handleCheckStatusFailure(result.error)
                }

                is Result.Success -> {
                    Log.d("RR", "checkOut ViewModel $result")
                    _state.value = _state.value.copy(
                        checkStatus = ECheckIn.DONE
                    )
                }
            }
            saveHomeState(this)
        }


    }

    private fun checkIn() {
        viewModelScope.launch {
            when (val checkIn = checkInUseCase(_userData.value?.id!!)) {
                is Result.Failure -> {
                    when (checkIn.error) {
                        DataError.CheckError.GUARD_NOT_FOUND -> TODO()
                        DataError.CheckError.REQUEST_TIMEOUT -> TODO()
                        DataError.CheckError.NO_INTERNET -> TODO()
                    }
                }

                is Result.Success -> {
                    Log.d("HomeViewModel", "checkIn ViewModel $checkIn")
                    _state.value = _state.value.copy(
                        checkStatus = checkIn.data.status,
                        checkInId = checkIn.data.id
                    )
                }
            }
            saveHomeState(this)
        }

    }

    private fun startTimer(resumeFrom: Long = 0) {
        _state.value = _state.value.copy(isStarted = true)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _timer.value = resumeFrom
            while (true) {
                delay(1000)
                _timer.value++
                saveHomeState(this)
            }
        }
    }

    private fun stopTimer() {
        _state.value = _state.value.copy(isStarted = false, currentIndex = 0)
        _timer.value = 0
        timerJob?.cancel()
    }


    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }


    private fun saveHomeState(viewModelScore: CoroutineScope) {
        viewModelScore.launch {
            homeUseCase.saveHomeData(
                Home(
                    currentIndex = _state.value.currentIndex,
                    isStarted = _state.value.isStarted,
                    elapsedSeconds = _timer.value,
                    roundId = _state.value.roundId,
                    checkId = _state.value.checkInId
                )
            )
        }
    }

    private fun handleCheckStatusFailure(error: DataError.CheckError) {
        when (error) {
            DataError.CheckError.GUARD_NOT_FOUND -> {

            }

            DataError.CheckError.REQUEST_TIMEOUT -> {

            }

            DataError.CheckError.NO_INTERNET -> {}
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

    private fun loadHomeState() {
        Log.d("HomeViewModel", "loadSavedState called")
        viewModelScope.launch {
            homeUseCase.getHomeData().collectLatest { savedState ->
                Log.d("HomeViewModel", "loadSavedState called $savedState")
                _state.update {
                    it.copy(
                        currentIndex = savedState?.currentIndex!!,
                        isStarted = savedState.isStarted,
                        roundId = savedState.roundId,
                        checkInId = savedState.checkId
                    )
                }
                if (savedState?.isStarted == true && savedState.elapsedSeconds > 0) {
                    startTimer(resumeFrom = savedState.elapsedSeconds)
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
            it.copy(localityName = locality?.name!!)
        }


    }

    private suspend fun fetchCheckInStatus(id: Int) {

        when (val result = getCheckInStatusUseCase(id)) {

            is Result.Failure -> handleCheckStatusFailure(result.error)

            is Result.Success -> _state.value = _state.value.copy(checkStatus = result.data)

        }


    }
}