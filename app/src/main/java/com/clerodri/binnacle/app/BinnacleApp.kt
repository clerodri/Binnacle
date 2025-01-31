package com.clerodri.binnacle.app


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.clerodri.binnacle.auth.presentation.admin.AdminViewModel
import com.clerodri.binnacle.auth.presentation.guard.GuardViewModel
import com.clerodri.binnacle.location.presentation.LocationViewModel


@Composable
fun BinnacleApp(
    guardViewModel: GuardViewModel,
    adminViewModel: AdminViewModel,
    locationViewModel: LocationViewModel
) {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavigationWrapper(navController, guardViewModel, adminViewModel, locationViewModel)
    }
}