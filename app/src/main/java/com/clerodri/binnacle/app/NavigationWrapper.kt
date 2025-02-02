package com.clerodri.binnacle.app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.clerodri.binnacle.auth.presentation.admin.AdminViewModel
import com.clerodri.binnacle.auth.presentation.admin.LoginAdminScreen
import com.clerodri.binnacle.auth.presentation.guard.GuardViewModel
import com.clerodri.binnacle.auth.presentation.guard.LoginGuardScreen
import com.clerodri.binnacle.home.presentation.HomeScreen
import com.clerodri.binnacle.home.presentation.HomeViewModel
import com.clerodri.binnacle.location.presentation.LocationViewModel
import com.clerodri.binnacle.report.AddReportScreen

@Composable
fun NavigationWrapper(
    navController: NavHostController,
    guardViewModel: GuardViewModel,
    adminViewModel: AdminViewModel,
    homeViewModel: HomeViewModel,
    locationViewModel: LocationViewModel
) {

    NavHost(navController = navController, startDestination = HomeScreen) {
        composable<LoginGuard> {
            LoginGuardScreen(
                viewModel = guardViewModel,
                navigateToLoginAdmin = { navController.navigate(LoginAdmin) },
                navigateToHome = { navController.navigate(HomeScreen) },
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
                locationViewModel, addReport = { navController.navigate(ReportScreen) },
                homeViewModel = homeViewModel
            )
        }

        composable<ReportScreen> {
            AddReportScreen()
        }
    }
}