package com.clerodri.binnacle.auth.presentation


/**
 * Events that can be sent to the Login Screen
 */
sealed  interface LoginScreenEvent {
    data object  Success: LoginScreenEvent
    data object  Failure: LoginScreenEvent
}