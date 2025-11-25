package com.clerodri.binnacle.app


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.clerodri.binnacle.addreport.presentation.AddReportViewModel
import com.clerodri.binnacle.authentication.presentation.admin.AdminViewModel
import com.clerodri.binnacle.authentication.presentation.guard.GuardViewModel
import com.clerodri.binnacle.home.presentation.HomeViewModel


@Composable
fun BinnacleApp(
    guardViewModel: GuardViewModel,
    adminViewModel: AdminViewModel,
    homeViewModel: HomeViewModel,
    addReportViewModel: AddReportViewModel,
) {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavigationWrapper(
            navController, guardViewModel, adminViewModel, homeViewModel, addReportViewModel
        )
    }
}