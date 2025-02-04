package com.clerodri.binnacle.auth.presentation.guard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.auth.domain.DataError
import com.clerodri.binnacle.auth.domain.Result
import com.clerodri.binnacle.auth.domain.model.Guard
import com.clerodri.binnacle.auth.domain.usecase.LoginUseCase
import com.clerodri.binnacle.auth.presentation.LoginScreenEvent
import com.clerodri.binnacle.util.AuthPreferences
import com.clerodri.binnacle.util.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
    private val authPreferences: AuthPreferences
) : ViewModel() {


    private val _state = MutableStateFlow(GuardScreenState.GuardState())
    val state: StateFlow<GuardScreenState.GuardState> = _state.asStateFlow()


    // channel for send events to guard screen
    private val _guardChannel = Channel<LoginScreenEvent>()
    internal fun getGuardChannel() = _guardChannel.receiveAsFlow()


    fun onEvent(event: GuardViewModelEvent) {
        when (event) {
            GuardViewModelEvent.LoginGuard -> login()
            is GuardViewModelEvent.UpdateIdentifier -> {
                _state.update {
                    it.copy(
                        loginEnable = isValidIdentifier(event.identifier),
                        identifier = event.identifier,
                        identifierError = if (!isValidIdentifier(event.identifier)) "Debe ingresar 10 números" else null
                    )
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

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = loginUseCase(_state.value.identifier)) {
                is Result.Failure -> {
                    handleLoginFailure(result.error)
                }

                is Result.Success -> {
                    handleLoginSuccess(result.data)
                }
            }
        }
    }


    private fun handleLoginSuccess(user: Guard) {
        Log.d("RR", "Success GuardViewModel $user")
        if (user.id == null ||
            user.fullname.isNullOrBlank() ||
            user.localityId == null
            || user.accessToken.isNullOrBlank()
            || user.refreshToken.isNullOrBlank()
        ) {
            Log.e("RR", "Error: Missing required user data. Cannot save to DataStore.")
            return
        }
        viewModelScope.launch {
            Log.e("RR", "saveUserData.")
            authPreferences.saveUserData(
                UserData(
                    id = user.id,
                    fullname = user.fullname,
                    localityId = user.localityId,
                    accessToken = user.accessToken,
                    refreshToken = user.refreshToken
                )
            )
            Log.d("RR", "User data saved successfully!")

            _state.update { it.copy(isLoading = false) }
            sendScreenEvent(LoginScreenEvent.Success)
        }
    }

    private fun handleLoginFailure(error: DataError.Network) {
        when (error) {
            DataError.Network.GUARD_NOT_FOUND -> Log.d("RR", "GUARD_NOT_FOUND")
            DataError.Network.REQUEST_TIMEOUT -> Log.d("RR", "REQUEST_TIMEOUT")
            DataError.Network.NO_INTERNET -> Log.d("RR", "NO_INTERNET")
        }

        sendScreenEvent(LoginScreenEvent.Failure)

        _state.update { it.copy(isLoading = false) }
    }

    // Send events back to the UI via the event channel
    private fun sendScreenEvent(event: LoginScreenEvent) {
        viewModelScope.launch { _guardChannel.send(event) }
    }


    private fun isValidIdentifier(id: String): Boolean = id.length == 10
}