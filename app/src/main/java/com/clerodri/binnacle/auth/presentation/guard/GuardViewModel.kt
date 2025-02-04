package com.clerodri.binnacle.auth.presentation.guard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.IdentificationError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.auth.domain.model.IdentificationValidator
import com.clerodri.binnacle.auth.domain.usecase.LoginUseCase
import com.clerodri.binnacle.auth.domain.usecase.UserUseCase
import com.clerodri.binnacle.auth.presentation.LoginScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
                if (user != null && user.isAuthenticated) {
                    sendScreenEvent(LoginScreenEvent.Success)
                }
            }
        }

    }


    fun onEvent(event: GuardViewModelEvent) {
        when (event) {
            GuardViewModelEvent.LoginGuard -> login()
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
            _state.update { it.copy(isLoading = true) }
            when (val result = loginUseCase(_state.value.identifier)) {
                is Result.Failure -> {
                    handleLoginFailure(result.error)
                }

                is Result.Success -> {
                    sendScreenEvent(LoginScreenEvent.Success)
                    _state.update { it.copy(isLoading = false) }

                }
            }
        }
    }


    private fun handleLoginFailure(error: DataError.Network) {
        when (error) {
            DataError.Network.GUARD_NOT_FOUND -> {
                sendScreenEvent(event = LoginScreenEvent.Failure("Guardia no encontrado"))
            }

            DataError.Network.REQUEST_TIMEOUT -> {
                sendScreenEvent(event = LoginScreenEvent.Failure("Servidor no responde"))
            }

            DataError.Network.NO_INTERNET -> {
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