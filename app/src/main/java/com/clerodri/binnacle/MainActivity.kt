package com.clerodri.binnacle

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.clerodri.binnacle.addreport.ui.AddReportViewModel
import com.clerodri.binnacle.app.BinnacleApp
import com.clerodri.binnacle.authentication.presentation.admin.AdminViewModel
import com.clerodri.binnacle.authentication.presentation.guard.GuardViewModel
import com.clerodri.binnacle.home.presentation.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val adminViewModel: AdminViewModel by viewModels()
    private val guardViewModel: GuardViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val addReportViewModel: AddReportViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, CAMERA_PERMISSION, 100
            )
        }
        setContent {
            BinnacleApp(
                guardViewModel, adminViewModel, homeViewModel, addReportViewModel
            )
        }
    }


    private fun arePermissionsGranted(): Boolean {
        return CAMERA_PERMISSION.all { permission ->
            ContextCompat.checkSelfPermission(
                applicationContext, permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        val CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }

}










