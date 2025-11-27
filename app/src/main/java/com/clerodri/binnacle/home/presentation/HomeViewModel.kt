package com.clerodri.binnacle.home.presentation

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.authentication.domain.model.AuthData
import com.clerodri.binnacle.core.AuthManager
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.core.components.SnackBarType
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.Home
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.domain.usecase.CheckInUseCase
import com.clerodri.binnacle.home.domain.usecase.CheckOutUseCase
import com.clerodri.binnacle.home.domain.usecase.CreateRoundUseCase
import com.clerodri.binnacle.home.domain.usecase.FinishRoundUseCase
import com.clerodri.binnacle.home.domain.usecase.GetCheckInStatusUseCase
import com.clerodri.binnacle.home.domain.usecase.HomeUseCase
import com.clerodri.binnacle.home.domain.usecase.RouteUseCase
import com.clerodri.binnacle.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val routeUseCase: RouteUseCase,
    private val createRoundUseCase: CreateRoundUseCase,
    private val finishRoundUseCase: FinishRoundUseCase,
    private val checkInUseCase: CheckInUseCase,
    private val checkOutUseCase: CheckOutUseCase,
    private val getCheckInStatusUseCase: GetCheckInStatusUseCase,
    private val authManager: AuthManager,
    @ApplicationContext private val context: Context
) : ViewModel() {


    val guardData: StateFlow<AuthData?> = authManager.userData

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _routes = MutableStateFlow<List<Route>>(emptyList())

    val routes: StateFlow<List<Route>> = _routes.asStateFlow()

    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var timerJob: Job? = null

    private val _eventChannel = Channel<HomeUiEvent>()
    internal fun getEventChannel() = _eventChannel.receiveAsFlow()
//    private var hasFetchedRoutes = false
    var hasShownLoginSuccess = false
        private set

    fun markLoginSuccessShown() {
        hasShownLoginSuccess = true
    }


    init {
        loadHomeState()
        onEnterHomeScreen()
    }

    fun onEnterHomeScreen() {
        viewModelScope.launch {
            val result = homeUseCase.validateSession()
            if (result is Result.Failure) {
                onLogOut()
            }else{
                fetchRoutes()
            }
        }
    }


    fun onEvent(event: HomeViewModelEvent) {
        if (!hasInternetConnection(context)) {
            sendScreenEvent(HomeUiEvent.ShowAlert("No hay conexión a internet", SnackBarType.Error))
            return
        }
        when (event) {
            HomeViewModelEvent.OnCheck -> {
                when (_state.value.checkStatus) {
                    ECheckIn.STARTED -> checkOut()
                    ECheckIn.DONE -> sendScreenEvent(
                        event = HomeUiEvent.ShowAlert(
                            "Ya esta registrado su Check-In", SnackBarType.Warning
                        )
                    )

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
                    _state.update { it.copy(currentIndex = _state.value.currentIndex + 1) }
                    saveHomeState()
                }
            }

            HomeViewModelEvent.OnLogOutRequested -> sendScreenEvent(
                event = HomeUiEvent.ShowAlert(
                    "Debe finalizar la ronda para cerrar sesión", SnackBarType.Warning
                )
            )

            HomeViewModelEvent.OnLogOut -> {
                hasShownLoginSuccess = false
                onLogOut()
            }

            HomeViewModelEvent.OnDestroy -> {
                viewModelScope.launch {
                    saveHomeState()
                }
            }

            HomeViewModelEvent.OnReportSuccess -> {
                viewModelScope.launch {
                    sendScreenEvent(
                        event = HomeUiEvent.ShowAlert(
                            "Reporte enviado exitosamente!", SnackBarType.Success
                        )
                    )
                }
            }

        }
    }

    private fun finishRound() {

        viewModelScope.launch {
            when (val result = finishRoundUseCase(_state.value.roundId)) {
                is Result.Failure -> {
                    sendScreenEvent(
                        event = HomeUiEvent.ShowAlert(
                            result.error.name, SnackBarType.Error
                        )
                    )
                }

                is Result.Success -> {
                    sendScreenEvent(
                        event = HomeUiEvent.ShowAlert(
                            "Ronda finalizada exitosamente", SnackBarType.Success
                        )
                    )
                }
            }
            homeUseCase.clearHomeData()
        }
    }

    private fun createRound() {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Guard id: ${guardData.value?.guardId}")
            Log.d("HomeViewModel", "Guard id: ${guardData.value?.localityId}")
            when (val result = createRoundUseCase.invoke(
                guardId = guardData.value?.guardId,
                localityId = guardData.value?.localityId)) {
                is Result.Failure -> {
                    sendScreenEvent(
                        event = HomeUiEvent.ShowAlert(
                            result.error.name, SnackBarType.Error
                        )
                    )
                }

                is Result.Success -> {
                    _state.value = _state.value.copy(roundId = result.data.id, isStarted = true)
                    //delay(300)
                    sendScreenEvent(
                        event = HomeUiEvent.ShowAlert(
                            "Iniciando ronda....", SnackBarType.Success
                        )
                    )
                }
            }
        }
    }

    private fun onLogOut() {

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            authManager.clearUserData()
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
                    _state.value = _state.value.copy(
                        checkStatus = ECheckIn.DONE
                    )
                }
            }
            saveHomeState()
        }


    }

    private fun checkIn() {
        viewModelScope.launch {
//            when (val checkIn = checkInUseCase(_userData.value?.id!!)) {
//                is Result.Failure -> {
//                    when (checkIn.error) {
//                        DataError.CheckError.GUARD_NOT_FOUND -> TODO()
//                        DataError.CheckError.REQUEST_TIMEOUT -> TODO()
//                        DataError.CheckError.NO_INTERNET -> TODO()
//                    }
//                }
//
//                is Result.Success -> {
//                    Log.d("HomeViewModel", "checkIn ViewModel $checkIn")
//                    _state.value = _state.value.copy(
//                        checkStatus = checkIn.data.status,
//                        checkInId = checkIn.data.id
//                    )
//                }
//            }
//            saveHomeState(this)
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
                saveHomeState()
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

    private fun saveHomeState() {
        viewModelScope.launch {
            Home(
                currentIndex = _state.value.currentIndex,
                isStarted = _state.value.isStarted,
                elapsedSeconds = _timer.value,
                roundId = _state.value.roundId,
                checkId = _state.value.checkInId
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
            if (event is HomeUiEvent.ShowAlert) {
                _state.update { it.copy(snackBarType = event.type) }
            }
            _eventChannel.send(event)
        }
    }


    private fun loadHomeState() {
        viewModelScope.launch {
            if (!hasInternetConnection(context)) {
                sendScreenEvent(
                    HomeUiEvent.ShowAlert(
                        "No internet. Can't load data.", SnackBarType.Error
                    )
                )
                return@launch
            }

            homeUseCase.getHomeData().collectLatest { savedState ->
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

    private suspend fun fetchRoutes() {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = routeUseCase.invoke()) {
                is Result.Failure -> {
                    val message = when (result.error) {
                        DataError.LocalityError.ROUTES_NOT_FOUND -> "No se encontraron rutas para la localidad."
                        DataError.LocalityError.SERVICE_UNAVAILABLE -> "Error de conexión. Intenta nuevamente."
                    }
                    sendScreenEvent(HomeUiEvent.ShowAlert(message, SnackBarType.Error))
                }

                is Result.Success -> {
                    _routes.update { result.data.sortedBy { it.order } }

                }
            }
            _state.value = _state.value.copy(isLoading = false)

    }

    private suspend fun fetchCheckInStatus(id: Int) {

        when (val result = getCheckInStatusUseCase(id)) {

            is Result.Failure -> handleCheckStatusFailure(result.error)

            is Result.Success -> _state.value = _state.value.copy(checkStatus = result.data)

        }
    }


}