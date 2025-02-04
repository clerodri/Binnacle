package com.clerodri.binnacle.auth.presentation


/**
 * Events that can be sent to the Login Screen
 */
sealed  interface LoginScreenEvent {
    data object  Success: LoginScreenEvent
    data class  Failure(val message:String): LoginScreenEvent
}