package com.clerodri.binnacle.auth.presentation.admin

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.auth.domain.usecase.LoginUseCase
import com.clerodri.binnacle.auth.presentation.LoginScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
open class AdminViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {


    var state by mutableStateOf(AdminState())
        private set

    private val _adminChannel = Channel<LoginScreenEvent>()
    internal fun getEventChannel() = _adminChannel.receiveAsFlow()


    fun onAction(action: AdminViewModelEvent) {
        when (action) {
            AdminViewModelEvent.LoginAdmin -> {
                login()
            }

            is AdminViewModelEvent.UpdateEmail -> {
                state = state.copy(
                    email = action.email,
                    emailError = if (!isValidEmail(action.email)) "Email no es válido" else null,
                    loginEnable = isValidEmail(action.email) && isValidPassword(state.password)
                )
            }

            is AdminViewModelEvent.UpdatePassword -> {
                state = state.copy(
                    password = action.password,
                    passwordError = if (!isValidPassword(action.password)) "Password debe ser mayor que 5 caracteres" else null,
                    loginEnable = isValidEmail(state.email) && isValidPassword(action.password)
                )
            }

            AdminViewModelEvent.ClearFields -> {
                state = state.copy(
                    email = "",
                    password = "",
                    emailError = null,
                    passwordError = null
                )
            }
        }
    }

    private fun isValidPassword(password: String): Boolean = password.length > 5
    private fun isValidIdentifier(id: String): Boolean = id.length == 10
    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches();

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
        viewModelScope.launch { _adminChannel.send(event) }
    }

}