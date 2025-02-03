package com.clerodri.binnacle.auth.presentation.guard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.auth.presentation.LoginScreenEvent
import com.clerodri.binnacle.auth.presentation.components.ButtonComponent
import com.clerodri.binnacle.auth.presentation.components.CedulaFieldComponent
import com.clerodri.binnacle.auth.presentation.components.ClickableAdminTextComponent
import com.clerodri.binnacle.auth.presentation.components.DividerTextComponent
import com.clerodri.binnacle.auth.presentation.components.LogoApp
import com.clerodri.binnacle.auth.presentation.components.TitleApp
import com.clerodri.binnacle.auth.presentation.components.TitleGuard
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.WhiteColor


@Composable
fun LoginGuardScreen(
    viewModel: GuardViewModel,
    navigateToLoginAdmin: () -> Unit,
    navigateToHome: () -> Unit
) {
    val state  by viewModel.state.collectAsState()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGroundAppColor),
        color = BackGroundAppColor
    ) {
        Column(Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(80.dp))
            TitleApp(
                value = stringResource(R.string.app_name),
                version = stringResource(R.string.version_app)
            )
            LogoApp(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(40.dp))
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(WhiteColor)
            ) {
                LoginGuardContent(
                    state,
                    navigateToLoginAdmin = {
                        viewModel.onEvent(GuardViewModelEvent.ClearFields)
                        navigateToLoginAdmin()
                    },
                    onClickLogin = { viewModel.onEvent(GuardViewModelEvent.LoginGuard) },
                    onIdentifierChange = { viewModel.onEvent(GuardViewModelEvent.UpdateIdentifier(it)) }
                )
            }

        }
    }


    // Recibe los eventos del viewmodel
    LaunchedEffect(true) {
        viewModel.getGuardChannel().collect { event ->
            when (event) {
                LoginScreenEvent.Failure -> Log.d("RR", "Screen Guard Failure")
                LoginScreenEvent.Success -> {
                    navigateToHome()
                    viewModel.onEvent(GuardViewModelEvent.ClearFields)
                }
            }
        }
    }


}

@Composable
fun LoginGuardContent(
    state: GuardScreenState.GuardState,
    navigateToLoginAdmin: () -> Unit,
    onClickLogin: () -> Unit = {},
    onIdentifierChange: (String) -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x80FFFFFF))
            ) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.Center),
                    color = BackGroundAppColor
                )
            }
        } else {
            TitleGuard()

            CedulaFieldComponent(
                value = state.identifier,
                labelValue = stringResource(id = R.string.cedula),
                painterResource(id = R.drawable.ic_cedula),
                identifierError = state.identifierError,
                onIdentifierChange = { onIdentifierChange(it) }
            )

            Spacer(modifier = Modifier.height(80.dp))
            ButtonComponent(
                stringResource(R.string.iniciar_sesion),
                loginEnable = state.loginEnable
            ) {
                onClickLogin()
            }
            Spacer(modifier = Modifier.height(80.dp))
            DividerTextComponent()
            Spacer(modifier = Modifier.height(20.dp))
            ClickableAdminTextComponent(Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    navigateToLoginAdmin()
                })
        }
    }
}




