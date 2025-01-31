//package com.clerodri.binnacle.auth.presentation.admin
//
//
//
//sealed interface AdminScreenEvent {
//    data object LoginAdmin: AdminScreenEvent
//    data object GoToLoginGuard: AdminScreenEvent
//    data class OnLoginGuard(val identifier: String): AdminScreenEvent
//}