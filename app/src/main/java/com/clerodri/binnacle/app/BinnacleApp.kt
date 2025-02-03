package com.clerodri.binnacle.app


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.clerodri.binnacle.addreport.AddReportViewModel
import com.clerodri.binnacle.auth.presentation.admin.AdminViewModel
import com.clerodri.binnacle.auth.presentation.guard.GuardViewModel
import com.clerodri.binnacle.home.presentation.HomeViewModel
import com.clerodri.binnacle.location.presentation.LocationViewModel


@Composable
fun BinnacleApp(
    guardViewModel: GuardViewModel,
    adminViewModel: AdminViewModel,
    homeViewModel: HomeViewModel,
    addReportViewModel : AddReportViewModel,
    locationViewModel: LocationViewModel
) {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavigationWrapper(
            navController,
            guardViewModel,
            adminViewModel,
            homeViewModel,
            addReportViewModel,
            locationViewModel
        )
    }
}