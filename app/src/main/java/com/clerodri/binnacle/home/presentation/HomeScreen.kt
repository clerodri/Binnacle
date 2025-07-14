package com.clerodri.binnacle.home.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.core.components.BigSpinner
import com.clerodri.binnacle.core.components.SnackBarComponent
import com.clerodri.binnacle.core.components.SnackBarType
import com.clerodri.binnacle.home.domain.model.HomeType
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.presentation.components.AddReportButton
import com.clerodri.binnacle.home.presentation.components.HomeBottomBar
import com.clerodri.binnacle.home.presentation.components.HomeTopBar
import com.clerodri.binnacle.home.presentation.components.RouteItem
import com.clerodri.binnacle.home.presentation.components.Timer
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navigateToReportScreen: (Int, Int) -> Unit,
    onLogOut: () -> Unit,
    reportSuccess: Boolean,
    onClearSuccessReport: () -> Unit
) {

    val snackState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = true) {}

    val state by homeViewModel.state.collectAsState()
    Scaffold(snackbarHost = {
        SnackBarComponent(
            snackState,
            modifier = Modifier.padding(bottom = 150.dp),
            type = state.snackBarType ?: SnackBarType.Success
        )
    }) { padding ->

        Screen(
            modifier = Modifier.padding(top = padding.calculateTopPadding()),
            homeViewModel = homeViewModel,
            navigateToReportScreen = navigateToReportScreen
        )
    }

    LaunchedEffect(Unit) {
        homeViewModel.onEnterHomeScreen()
    }

    LaunchedEffect(reportSuccess) {
        if (reportSuccess) {
            coroutineScope.launch {
                homeViewModel.onEvent(HomeViewModelEvent.OnReportSuccess)
            }
            onClearSuccessReport()
        }
    }

    LaunchedEffect(Unit) {
        if (!homeViewModel.hasShownLoginSuccess) {
            snackState.showSnackbar(
                "¡Inicio de sesión exitoso!", duration = SnackbarDuration.Short
            )
            homeViewModel.markLoginSuccessShown()
        }
    }
    LaunchedEffect(Unit) {

        homeViewModel.getEventChannel().collect { event ->
            when (event) {

                HomeUiEvent.LogOut -> {
                    onLogOut()
                }


                is HomeUiEvent.ShowAlert -> {
                    coroutineScope.launch {
                        snackState.showSnackbar(
                            message = event.message, duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navigateToReportScreen: (Int, Int) -> Unit
) {
    val state by homeViewModel.state.collectAsState()
    val guard by homeViewModel.guardData.collectAsState()
    val routes by homeViewModel.routes.collectAsState()
    val timerValue by homeViewModel.timer.collectAsState()
    var selectedHomeNav by rememberSaveable {
        mutableStateOf(HomeType.Home)
    }


    Scaffold(topBar = {
        HomeTopBar(
            modifier = modifier.fillMaxWidth(), fullname = guard?.fullName
        )
    }, bottomBar = {
        HomeBottomBar(
            checkInStatus = state.checkStatus,
            selectedScreen = selectedHomeNav,
            onItemSelected = { selectedHomeNav = it },
            onLogOut = {
                if (state.isStarted) {
                    homeViewModel.onEvent(HomeViewModelEvent.OnLogOutRequested)
                } else {
                    homeViewModel.onEvent(HomeViewModelEvent.OnLogOut)
                }
            },
            onCheck = {
                homeViewModel.onEvent(HomeViewModelEvent.OnCheck)
            },
            isCheckEnabled = false
        )
    }, floatingActionButton = {
        AddReportButton(state.isStarted) {
            val roundId = state.roundId
            val routeId = routes[state.currentIndex].order
            navigateToReportScreen(routeId, roundId.toInt())
        }
    }) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValue.calculateTopPadding())
        ) {
            Timer(
                modifier = Modifier.fillMaxWidth(),
                isTimerRunning = state.isStarted,
                isEnable = !state.isStarted || state.currentIndex == routes.size - 1,
                onStart = { homeViewModel.onEvent(HomeViewModelEvent.StartRound) },
                onStop = { homeViewModel.onEvent(HomeViewModelEvent.StopRound) },
                timer = timerValue
            )
            if (state.isLoading) {
                BigSpinner(paddingValue)
            } else {
                HomeScreenContent(
                    contentPadding = paddingValue,
                    isStarted = state.isStarted,
                    currentIndex = state.currentIndex,
                    routes = routes,
                    updateIndex = { homeViewModel.onEvent(HomeViewModelEvent.UpdateIndex) },
                )
            }

        }
    }
}

@Composable
private fun HomeScreenContent(
    contentPadding: PaddingValues,
    isStarted: Boolean,
    routes: List<Route>,
    currentIndex: Int,
    updateIndex: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val info = if (!isStarted) stringResource(R.string.press_comenzar_to_start_ronda)
        else stringResource(R.string.complete_all_rounds_for_finish_round)

        Text(
            info, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.error
        )

        routes.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = index >= currentIndex && index <= currentIndex + 1,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 1000, easing = LinearEasing
                    )
                ) + slideInVertically(),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 1000, easing = FastOutSlowInEasing
                    )
                ) + slideOutVertically(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    RouteItem(index = index,
                        item = item,
                        isActive = index == currentIndex && isStarted,
                        showArrow = index != currentIndex,
                        isLastItem = currentIndex == routes.size - 1,
                        onNextClick = {
                            if (currentIndex < routes.size - 1) {
                                updateIndex()
                            }
                        })

                }

            }
        }
    }
}




