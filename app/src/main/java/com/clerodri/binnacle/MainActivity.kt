package com.clerodri.binnacle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.clerodri.binnacle.app.BinnacleApp
import com.clerodri.binnacle.auth.presentation.admin.AdminViewModel
import com.clerodri.binnacle.auth.presentation.guard.GuardViewModel
import com.clerodri.binnacle.location.presentation.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val adminViewModel: AdminViewModel by viewModels()
        val guardViewModel: GuardViewModel by viewModels()

        val locationViewModel: LocationViewModel by viewModels()
        enableEdgeToEdge()

        setContent {
            BinnacleApp(guardViewModel, adminViewModel, locationViewModel)
        }
    }
}








