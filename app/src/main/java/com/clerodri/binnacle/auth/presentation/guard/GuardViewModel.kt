package com.clerodri.binnacle.auth.presentation.guard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.auth.presentation.LoginScreenEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GuardViewModel @Inject constructor() : ViewModel() {

    // hold the state of guard screen
    var state by mutableStateOf(GuardScreenState.GuardState())
        private set

    // channel for send events to guard screen
    private val _guardChannel = Channel<LoginScreenEvent>()
    internal fun getGuardChannel() = _guardChannel.receiveAsFlow()


    fun onEvent(event: GuardViewModelEvent) {
        when (event) {
            GuardViewModelEvent.LoginGuard -> login()
            is GuardViewModelEvent.UpdateIdentifier -> {
                state = state.copy(
                    loginEnable = isValidIdentifier(event.identifier),
                    identifier = event.identifier,
                    identifierError = if (!isValidIdentifier(event.identifier)) "Debe ingresar 10 números" else null
                )
            }

            GuardViewModelEvent.ClearFields -> {
                state = state.copy(
                    identifier = "",
                    identifierError = null
                )
            }
        }
    }


    private fun login() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(1000)
            sendScreenEvent(LoginScreenEvent.Success)
            state = state.copy(isLoading = false)

            /*       when(val result = loginUseCase.login(state.identifier))
                   {
                       is Result.Failure ->{
                           _authChannel .send(AuthEvents.Failure)
                           when (result.error) {
                               DataError.Network.GUARD_NOT_FOUND -> {
                                   Log.d("RR", "GUARD_NOT_FOUND ")
                               }
                               DataError.Network.REQUEST_TIMEOUT -> {
                                   Log.d("RR", "REQUEST_TIMEOUT ")
                               }
                               DataError.Network.NO_INTERNET -> {
                                   Log.d("RR", "NO_INTERNET ")
                               }
                           }
                       }
                       is Result.Success -> {
                           Log.d("RR", "Success ${result.data}")
                           _authChannel .send(AuthEvents.Success)
                       }
                   }

                    state = state.copy(isLoading = false)*/

        }
    }


    // Send events back to the UI via the event channel
    private fun sendScreenEvent(event: LoginScreenEvent) {
        viewModelScope.launch { _guardChannel.send(event) }
    }

    private fun isValidIdentifier(id: String): Boolean = id.length == 10
}