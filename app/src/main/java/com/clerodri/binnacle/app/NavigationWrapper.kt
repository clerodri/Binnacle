package com.clerodri.binnacle.app

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.clerodri.binnacle.addreport.ui.AddReportScreen
import com.clerodri.binnacle.addreport.ui.AddReportViewModel
import com.clerodri.binnacle.authentication.presentation.admin.AdminViewModel
import com.clerodri.binnacle.authentication.presentation.admin.LoginAdminScreen
import com.clerodri.binnacle.authentication.presentation.guard.GuardViewModel
import com.clerodri.binnacle.authentication.presentation.guard.LoginGuardScreen
import com.clerodri.binnacle.home.presentation.HomeScreen
import com.clerodri.binnacle.home.presentation.HomeViewModel
import dagger.hilt.android.EntryPointAccessors

@Composable
fun NavigationWrapper(
    navController: NavHostController,
    guardViewModel: GuardViewModel,
    adminViewModel: AdminViewModel,
    homeViewModel: HomeViewModel,
    addReportViewModel: AddReportViewModel,
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext, AuthManagerEntryPoint::class.java
        )
        entryPoint.authManager().loadUserData()
    }

    val userData by guardViewModel.userData.collectAsStateWithLifecycle()

    LaunchedEffect(userData?.isAuthenticated) {

        if (userData?.isAuthenticated == true) {
            navController.navigate(HomeScreen) {
                popUpTo(LoginGuard) { inclusive = false }
            }
        }

    }

    val startDestination = SplashScreen
    NavHost(navController = navController, startDestination = startDestination, enterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)
        )
    }, exitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)
        )
    }, popEnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)
        )
    }, popExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)
        )
    }

    ) {

        composable<SplashScreen> {
            SplashScreen(onNavigateToHome = {
                navController.navigate(HomeScreen) {
                    popUpTo(SplashScreen) { inclusive = true }
                    launchSingleTop = true
                }
            }, onNavigateToLogin = {
                navController.navigate(LoginGuard) {
                    popUpTo(SplashScreen) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable<LoginGuard> {
            Log.d("GG", "LoginGuard")
            LoginGuardScreen(viewModel = guardViewModel,
                navigateToLoginAdmin = { navController.navigate(LoginAdmin) },
                navigateToHome = {
                    navController.navigate(HomeScreen) {
                        popUpTo(LoginGuard) { inclusive = false }
                        launchSingleTop = true
                    }
                })
        }
        composable<LoginAdmin> {
            LoginAdminScreen(viewModel = adminViewModel, navigateToLoginGuard = {
                navController.navigate(LoginGuard) {
                    popUpTo<LoginGuard> {
                        inclusive = true
                    }
                }
            }, navigateToHome = { navController.navigate(HomeScreen) })
        }
        composable<HomeScreen> { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val reportSuccess = savedStateHandle.get<Boolean>("report_success") ?: false
            HomeScreen(navigateToReportScreen = { routeId, roundId, localityId ->
                navController.navigate(
                    ReportScreen(
                        routeId = routeId, roundId = roundId, localityId = localityId
                    )
                )
            }, homeViewModel = homeViewModel, onLogOut = {
                navController.navigate(LoginGuard) {
                    popUpTo(HomeScreen) { inclusive = true }
                    launchSingleTop = true
                }
            }, reportSuccess = reportSuccess, onClearSuccessReport = {
                savedStateHandle["report_success"] = false
            }

            )
        }

        composable<ReportScreen> { backStackEntry ->

            val details = backStackEntry.toRoute<ReportScreen>()
            AddReportScreen(
                viewModel = addReportViewModel, onBack = { isSuccess ->
                    if (isSuccess) {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "report_success",
                            true
                        )
                    }
                    navController.popBackStack()
                }, roundId = details.roundId
            )

        }
    }
}
