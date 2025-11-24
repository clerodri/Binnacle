package com.clerodri.binnacle.authentication.presentation.guard

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.clerodri.binnacle.R
import com.clerodri.binnacle.authentication.presentation.LoginScreenEvent
import com.clerodri.binnacle.authentication.presentation.components.ButtonComponent
import com.clerodri.binnacle.authentication.presentation.components.CedulaFieldComponent
import com.clerodri.binnacle.authentication.presentation.components.ClickableAdminTextComponent
import com.clerodri.binnacle.authentication.presentation.components.DividerTextComponent
import com.clerodri.binnacle.authentication.presentation.components.LogoApp
import com.clerodri.binnacle.authentication.presentation.components.SelectionScreen
import com.clerodri.binnacle.authentication.presentation.components.TitleApp
import com.clerodri.binnacle.authentication.presentation.components.TitleGuard
import com.clerodri.binnacle.core.components.SnackBarComponent
import com.clerodri.binnacle.core.components.SnackBarType
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.WhiteColor
import kotlinx.coroutines.launch


@Composable
fun LoginGuardScreen(
    viewModel: GuardViewModel,
    navigateToLoginAdmin: () -> Unit,
    navigateToHome: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val state by viewModel.state.collectAsState()


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
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(WhiteColor)
            ) {
                if( state.showSelectionScreen){
                    SelectionScreen(
                        options = state.availableOptions,
                        selectedOption = state.selectedOption,
                        onOptionSelected = {
                            viewModel.onEvent(GuardViewModelEvent.SelectOption(it))
                        },
                        onContinue = {
                            viewModel.onEvent(GuardViewModelEvent.ProceedToLogin)
                        }
                    )

                }else{

                    LoginGuardContent(
                        state,
                        navigateToLoginAdmin = {
                            viewModel.onEvent(GuardViewModelEvent.ClearFields)
                            navigateToLoginAdmin()
                        },
                        onClickLogin = { viewModel.onEvent(GuardViewModelEvent.LoginGuard) },
                        onIdentifierChange = { viewModel.onEvent(GuardViewModelEvent.UpdateIdentifier(it)) },
                        onBackToSelection = { viewModel.onEvent(GuardViewModelEvent.BackToSelection) }
                    )
                }
            }
        }
        SnackBarComponent(
            snackbarHostState,
            modifier = Modifier.padding(top = 20.dp),
            type = SnackBarType.Error
        )
    }


    // Recibe los eventos del viewmodel
    LaunchedEffect(Unit) {
        viewModel.getGuardChannel().collect { event ->
            when (event) {
                LoginScreenEvent.Success -> {
                    navigateToHome()
                    viewModel.onEvent(GuardViewModelEvent.ClearFields)
                }

                is LoginScreenEvent.Failure -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
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
    onIdentifierChange: (String) -> Unit,
    onBackToSelection: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.codigo_dactilar)
    )
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TextButton(
            onClick = onBackToSelection,
            modifier = Modifier.padding(bottom = 8.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor =  Color(0xFF2973B2)
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Volver")
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {

            LottieAnimation(
                composition = composition,
                isPlaying = true,
                iterations = LottieConstants.IterateForever,
                speed = 1f,
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.Fit
            )
        }
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
//            TitleGuard()

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
                    //navigateToLoginAdmin()
                }
            )
        }
    }
}




