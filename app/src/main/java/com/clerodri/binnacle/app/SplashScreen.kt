package com.clerodri.binnacle.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.clerodri.binnacle.util.hasInternetConnection
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(1000)

        val hasInternet = hasInternetConnection(context)
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AuthManagerEntryPoint::class.java
        )
        val authManager = entryPoint.authManager()

        authManager.loadUserData()
        val userData = authManager.userData.value

        if (userData?.isAuthenticated == true && hasInternet) {
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2973B2)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bienvenido",
            color = Color.White,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
    }
}
