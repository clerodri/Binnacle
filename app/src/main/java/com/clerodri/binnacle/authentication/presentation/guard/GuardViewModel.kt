package com.clerodri.binnacle.authentication.presentation.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.IdentificationError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.authentication.domain.model.IdentificationValidator
import com.clerodri.binnacle.authentication.domain.usecase.LoginUseCase
import com.clerodri.binnacle.authentication.domain.usecase.UserUseCase
import com.clerodri.binnacle.authentication.presentation.LoginScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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
class GuardViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userUseCase: UserUseCase,
    private val identificationValidator: IdentificationValidator
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _state = MutableStateFlow(GuardScreenState.GuardState())
    val state: StateFlow<GuardScreenState.GuardState> = _state.asStateFlow()


    // channel for send events to guard screen
    private val _guardChannel = Channel<LoginScreenEvent>()
    internal fun getGuardChannel() = _guardChannel.receiveAsFlow()

    init {
       checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            userUseCase.getUserData().collectLatest { user ->
                val authStatus = user?.isAuthenticated == true
                _isAuthenticated.value = authStatus
            }
        }

    }


    fun onEvent(event: GuardViewModelEvent) {
        when (event) {
            GuardViewModelEvent.LoginGuard -> {
                login()
            }
            is GuardViewModelEvent.UpdateIdentifier -> {
                val result = identificationValidator.validateIdentification(event.identifier)
                when (result) {
                    is Result.Failure -> handleInvalidIdentifier(event.identifier, result.error)
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                loginEnable = true,
                                identifier = event.identifier,
                                identifierError = null
                            )
                        }
                    }
                }
            }

            GuardViewModelEvent.ClearFields -> {
                _state.update {
                    it.copy(
                        identifier = "",
                        identifierError = null,
                        loginEnable = false
                    )

                }
            }

            GuardViewModelEvent.LogOut -> {

                _isAuthenticated.value = false
            }
        }
    }

    private fun handleInvalidIdentifier(value: String, error: IdentificationError) {
        when (error) {
            IdentificationError.INVALID_IDENTIFICATION -> {
                _state.update {
                    it.copy(
                        identifier = value,
                        loginEnable = false,
                        identifierError = "Debe ingresar 10 números"
                    )
                }
            }

            IdentificationError.IDENTIFICATION_TO0_LONG -> {
                _state.update {
                    it.copy(
                        identifier = value,
                        loginEnable = false,
                        identifierError = "No debe ser mayor de 10 números"
                    )
                }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            delay(500)
            when (val result = loginUseCase(_state.value.identifier)) {
                is Result.Failure -> {
                   handleLoginFailure(result.error)
                    _state.value = _state.value.copy(isLoading = true)
                }

                is Result.Success -> {
                    sendScreenEvent(LoginScreenEvent.Success)
                }
            }
            delay(500)
            _state.value =
                _state.value.copy(isLoading = false, identifier = "", identifierError = null)
        }
    }


    private fun handleLoginFailure(error: DataError.AuthNetwork) {
        when (error) {
            DataError.AuthNetwork.GUARD_NOT_FOUND -> {
                sendScreenEvent(event = LoginScreenEvent.Failure("Guardia no existe"))
            }

            DataError.AuthNetwork.REQUEST_TIMEOUT -> {
                sendScreenEvent(event = LoginScreenEvent.Failure("Servidor no responde"))
            }

            DataError.AuthNetwork.NO_INTERNET -> {
                sendScreenEvent(event = LoginScreenEvent.Failure("No hay conexión a internet"))
            }
        }

        _state.update { it.copy(isLoading = false) }
    }

    // Send events back to the UI via the event channel
    private fun sendScreenEvent(event: LoginScreenEvent) {
        viewModelScope.launch { _guardChannel.send(event) }
    }


}