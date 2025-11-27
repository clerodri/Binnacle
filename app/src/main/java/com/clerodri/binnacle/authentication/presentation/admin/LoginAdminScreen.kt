package com.clerodri.binnacle.authentication.presentation.admin

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.clerodri.binnacle.R
import com.clerodri.binnacle.authentication.presentation.LoginScreenEvent
import com.clerodri.binnacle.authentication.presentation.components.ButtonComponent
import com.clerodri.binnacle.authentication.presentation.components.EmailTextComponent
import com.clerodri.binnacle.authentication.presentation.components.PasswordFieldComponent
import com.clerodri.binnacle.authentication.presentation.components.TitleAdmin
import com.clerodri.binnacle.authentication.presentation.components.TitleApp
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.WhiteColor


@Composable
fun LoginAdminScreen(
    viewModel: AdminViewModel,
    navigateToLoginGuard: () -> Unit,
    navigateToHome: () -> Unit
) {
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
//            LogoApp(Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(WhiteColor)
            ) {
                AdminScreenContent(
                    state = viewModel.state,
                    onClickArrowBack = {
                        viewModel.onAction(AdminViewModelEvent.ClearFields)
                        navigateToLoginGuard()
                    },
                    onEmailChange = { viewModel.onAction(AdminViewModelEvent.UpdateEmail(it)) },
                    onPasswordChange = { viewModel.onAction(AdminViewModelEvent.UpdatePassword(it)) },
                    onLoginAdmin = { viewModel.onAction(AdminViewModelEvent.LoginAdmin) },
                )
            }

        }
    }
    // Recibe los eventos del viewmodel
    LaunchedEffect(true) {
        viewModel.getEventChannel().collect { event ->
            when (event) {
                LoginScreenEvent.Success -> {
                    viewModel.onAction(AdminViewModelEvent.ClearFields)
                    navigateToHome()
                }

                is LoginScreenEvent.Failure -> {
                    Log.d("RR", "Screen Admin Failure")
                }
            }
        }
    }

//    SystemBackButtonHandler {
//        BinnacleAppRouter.navigateTo(Screen.LoginGuardScreen)
//    }
}


@Composable
fun AdminScreenContent(
    state: AdminState,
    onClickArrowBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginAdmin: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.admin)
    )
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
            TitleAdmin(
                composition = composition,
                comeBack = onClickArrowBack
            )
            EmailTextComponent(
                value = state.email,
                emailError = state.emailError,
                labelValue = stringResource(id = R.string.email),
                painterResource = painterResource(id = R.drawable.ic_email),
                onEmailChange = {
                    onEmailChange(it)
                }
            )
            PasswordFieldComponent(
                value = state.password,
                passwordError = state.passwordError,
                labelValue = stringResource(id = R.string.password),
                painterResource = painterResource(id = R.drawable.ic_password),
                onPasswordChange = {
                    onPasswordChange(it)
                }
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
        ButtonComponent(
            stringResource(R.string.iniciar_sesion),
            loginEnable = state.loginEnable
        ) {
            onLoginAdmin()
        }
    }
}

