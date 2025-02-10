package com.clerodri.binnacle.app

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.clerodri.binnacle.authentication.presentation.guard.GuardViewModelEvent
import com.clerodri.binnacle.authentication.presentation.guard.LoginGuardScreen
import com.clerodri.binnacle.home.presentation.HomeScreen
import com.clerodri.binnacle.home.presentation.HomeViewModel
import com.clerodri.binnacle.location.presentation.LocationViewModel

@Composable
fun NavigationWrapper(
    navController: NavHostController,
    guardViewModel: GuardViewModel,
    adminViewModel: AdminViewModel,
    homeViewModel: HomeViewModel,
    addReportViewModel: AddReportViewModel,
    locationViewModel: LocationViewModel
) {
    val isUserAuthenticated by guardViewModel.isAuthenticated.collectAsStateWithLifecycle()

    LaunchedEffect(false) {
        Log.d("GG", "isUserAuthenticated $isUserAuthenticated")
        if (isUserAuthenticated) {
            navController.navigate(HomeScreen) {
                popUpTo(LoginGuard) { inclusive = false }
            }
        }
    }
    val startDestination =
        if (isUserAuthenticated) HomeScreen else LoginGuard
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            )
        }

    ) {
        composable<LoginGuard> {
            Log.d("GG", "LoginGuard")
            LoginGuardScreen(
                viewModel = guardViewModel,
                navigateToLoginAdmin = { navController.navigate(LoginAdmin) },
                navigateToHome = {
                    navController.navigate(HomeScreen) {
                        popUpTo<LoginGuard> {
                            inclusive = false
                        }
                    }

                },
            )
        }
        composable<LoginAdmin> {
            LoginAdminScreen(
                viewModel = adminViewModel,
                navigateToLoginGuard = {
                    navController.navigate(LoginGuard) {
                        popUpTo<LoginGuard> {
                            inclusive = true
                        }
                    }
                },
                navigateToHome = { navController.navigate(HomeScreen) }
            )
        }
        composable<HomeScreen> {


            HomeScreen(
                locationViewModel,
                navigateToReportScreen = { routeId,
                                           roundId,
                                           localityId ->
                    navController.navigate(
                        ReportScreen(
                            routeId = routeId,
                            roundId = roundId,
                            localityId = localityId
                        )
                    )
                },
                homeViewModel = homeViewModel,
                onLogOut = {
                    guardViewModel.onEvent(GuardViewModelEvent.LogOut)
                }
            )


        }

        composable<ReportScreen> { backStackEntry ->

            val details = backStackEntry.toRoute<ReportScreen>()
            AddReportScreen(
                addReportViewModel = addReportViewModel,
                onBack = { navController.popBackStack() },
                routeId = details.routeId,
                roundId = details.roundId,
                localityId = details.localityId,
            )
        }
    }
}
