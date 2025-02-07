package com.clerodri.binnacle.home.presentation

import android.Manifest
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
import com.clerodri.binnacle.home.domain.model.HomeType
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.presentation.components.HeaderHome
import com.clerodri.binnacle.home.presentation.components.HomeAddReport
import com.clerodri.binnacle.home.presentation.components.HomeBottomBar
import com.clerodri.binnacle.home.presentation.components.HomeTopBar
import com.clerodri.binnacle.home.presentation.components.RouteItem
import com.clerodri.binnacle.location.presentation.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    locationViewModel: LocationViewModel,
    homeViewModel: HomeViewModel,
    addReport: () -> Unit,
    onLogOut: () -> Unit
) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = true) {

    }
    Scaffold(
        snackbarHost = {

            SnackBarComponent(snackbarHostState, modifier = Modifier.padding(bottom = 150.dp))
        }
    ) { padding ->
        Screen(
            modifier = Modifier.padding(top = padding.calculateTopPadding()),
            locationViewModel = locationViewModel,
            homeViewModel = homeViewModel,
            addReport = addReport
        )
    }
    LaunchedEffect(true) {
        locationPermissions.launchMultiplePermissionRequest()
    }

    LaunchedEffect(true) {
        locationViewModel.getCurrentLocation()
    }

    LaunchedEffect(Unit) {
        homeViewModel.getEventChannel().collect { event ->
            when (event) {

                HomeUiEvent.LogOut -> {
                    onLogOut()
                }

                HomeUiEvent.ShowAlert -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Debe Finalizar la RONDA actual",
                            duration = SnackbarDuration.Short
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
    locationViewModel: LocationViewModel,
    homeViewModel: HomeViewModel,
    addReport: () -> Unit
) {
    val state by homeViewModel.state.collectAsState()
    val user by homeViewModel.userData.collectAsState()
    val routes by homeViewModel.routes.collectAsState()

    var selectedHomeNav by rememberSaveable {
        mutableStateOf(HomeType.Home)
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                modifier = modifier.fillMaxWidth(),
                fullname = user?.fullname,
                localityName = state.localityName
            )
        },
        bottomBar = {
            HomeBottomBar(
                isCheckIn = state.isCheckedIn,
                isEnable = state.enableCheck,
                selectedScreen = selectedHomeNav,
                onItemSelected = { selectedHomeNav = it },
                onLogOut = {
                    if (state.isStarted) {
                        homeViewModel.onEvent(HomeViewModelEvent.OnLogOutRequested)
                    } else {
                        homeViewModel.onLogOut()
                    }
                },
                onCheck = {
                    homeViewModel.onEvent(HomeViewModelEvent.OnCheck)
                }
            )
        },
        floatingActionButton = {
            //state.isStarted
            HomeAddReport(true) {
                addReport()
            }
        }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValue.calculateTopPadding())
        ) {
            HeaderHome(
                modifier = Modifier.fillMaxWidth(),
                isStarted = state.isStarted, isRoundBtnEnabled = state.isRoundBtnEnabled,
                onStart = { homeViewModel.onEvent(HomeViewModelEvent.StartRound) },
                onStop = { homeViewModel.onEvent(HomeViewModelEvent.StopRound) },
                timer = state.timer
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
//            if (state.showDialog) {
//                //insertar dialgo
//                CheckInDialogComponent(
//                    title = "INICIAR RONDA",
//                    message = "Seguro que desea iniciar la ronda?",
//                    onCancel = { homeViewModel.onEvent(HomeViewModelEvent.OnClickDialog) },
//                    onConfirm = {
//
//                    },
//                    onDismissRequest = {homeViewModel.onEvent(HomeViewModelEvent.OnClickDialog)}
//                )
            //          }

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
            info,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )

        routes.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = index >= currentIndex && index <= currentIndex + 1,
                enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = LinearEasing
                    )
                ) + slideInVertically(),
                exit = fadeOut(
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                ) + slideOutVertically(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    RouteItem(
                        index = index,
                        item = item,
                        isActive = index == currentIndex && isStarted,
                        showArrow = index != currentIndex,
                        isLastItem = currentIndex == routes.size - 1,
                        onNextClick = {
                            if (currentIndex < routes.size - 1) {
                                updateIndex()
                            }
                        }
                    )

                }

            }
        }
    }
}




