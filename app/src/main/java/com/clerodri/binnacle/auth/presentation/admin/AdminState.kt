package com.clerodri.binnacle.auth.presentation.admin

data class AdminState(
    var email:String = "",
    var emailError:String? = null,
    var password:String = "",
    var passwordError:String? = null,
    var isLoading:Boolean = false,
    var loginEnable:Boolean = false,
)
