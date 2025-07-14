package com.clerodri.binnacle.authentication.presentation.guard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.authentication.domain.model.AuthData
import com.clerodri.binnacle.authentication.domain.model.IdentificationValidator
import com.clerodri.binnacle.authentication.domain.usecase.LoginUseCase
import com.clerodri.binnacle.authentication.presentation.LoginScreenEvent
import com.clerodri.binnacle.core.AuthManager
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.IdentificationError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuardViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val authManager: AuthManager,
    private val identificationValidator: IdentificationValidator,
    @ApplicationContext private val context: Context
) : ViewModel() {


    private val _state = MutableStateFlow(GuardScreenState.GuardState())
    val state: StateFlow<GuardScreenState.GuardState> = _state.asStateFlow()

    val userData: StateFlow<AuthData?> = authManager.userData

    // channel for send events to guard screen
    private val _guardChannel = Channel<LoginScreenEvent>()
    internal fun getGuardChannel() = _guardChannel.receiveAsFlow()


    fun onEvent(event: GuardViewModelEvent) {

        when (event) {
            GuardViewModelEvent.LoginGuard -> {
                if( _state.value.identifier.isBlank()){
                    _state.update { it.copy(identifierError = "Debe ingresar una cédula") }
                    return
                }
                login(context)
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


    private fun login(context: Context) {

        if (!hasInternetConnection(context)) {
            sendScreenEvent(event = LoginScreenEvent.Failure("No hay conexión a internet"))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(500)
            when (val result = loginUseCase(_state.value.identifier)) {
                is Result.Failure -> {
                    handleLoginFailure(result.error)
                }

                is Result.Success -> {
                    authManager.loadUserData()
                    sendScreenEvent(LoginScreenEvent.Success)
                }
            }
            delay(500)
            _state.value =
                _state.value.copy(isLoading = false, identifier = "", identifierError = null)
        }
    }


    private fun handleLoginFailure(error: DataError.AuthNetwork) {
        println("TEST: ${error.name}")
        val message = when (error) {
            DataError.AuthNetwork.GUARD_NOT_FOUND -> "Guardia no registrado"
            DataError.AuthNetwork.SERVICE_UNAVAILABLE -> "Servicio no disponible"
            DataError.AuthNetwork.BAD_CREDENTIAL -> "Cedula no valida"
        }
        sendScreenEvent(LoginScreenEvent.Failure(message))
        _state.update { it.copy(isLoading = false) }
    }

    // Send events back to the UI via the event channel
    private fun sendScreenEvent(event: LoginScreenEvent) {
        viewModelScope.launch { _guardChannel.send(event) }
    }


}