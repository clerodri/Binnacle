package com.clerodri.binnacle.home.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.addreport.presentation.components.UrbanizationMapScreen
import com.clerodri.binnacle.core.components.BigSpinner
import com.clerodri.binnacle.core.components.SnackBarComponent
import com.clerodri.binnacle.core.components.SnackBarType
import com.clerodri.binnacle.home.domain.model.HomeType
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.presentation.components.AddReportButton
import com.clerodri.binnacle.home.presentation.components.HomeBottomBar
import com.clerodri.binnacle.home.presentation.components.HomeScreenList
import com.clerodri.binnacle.home.presentation.components.HomeTopBar
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

// ✅ Constants
private const val URBANIZATION_LATITUDE = -2.140910
private const val URBANIZATION_LONGITUDE = -79.859712
private const val URBANIZATION_NAME = "Urb. Laguna Dorada"
private const val MAP_HEIGHT_FRACTION = 0.3f
private const val SNACKBAR_BOTTOM_PADDING = 150

private val URBANIZATION_BOUNDARY = listOf(
    LatLng(-2.139500, -79.863200),  // Top-left
    LatLng(-2.140100, -79.856200),  // Top-right
    LatLng(-2.142400, -79.856200),  // Bottom-right
    LatLng(-2.141900, -79.863200),  // Bottom-left
    LatLng(-2.139500, -79.863200),  // Close polygon
)

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navigateToReportScreen: (Int, Int) -> Unit,
    onLogOut: () -> Unit
) {
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    val state by homeViewModel.state.collectAsState()


    LaunchedEffect(Unit) {
        //homeViewModel.onEnterHomeScreen()
    }

    LaunchedEffect(Unit) {
        if (!homeViewModel.hasShownLoginSuccess) {
            snackState.showSnackbar(
                "¡Inicio de sesión exitoso!",
                duration = SnackbarDuration.Short
            )
            homeViewModel.markLoginSuccessShown()
        }
    }


    LaunchedEffect(Unit) {
        homeViewModel.getEventChannel().collect { event ->
            handleHomeEvent(event, onLogOut, snackState, coroutineScope)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackBarComponent(
                snackState,
                modifier = Modifier.padding(bottom = SNACKBAR_BOTTOM_PADDING.dp),
                type = state.snackBarType ?: SnackBarType.Success
            )
        }
    ) { padding ->
        Screen(
            modifier = Modifier.padding(top = padding.calculateTopPadding()),
            homeViewModel = homeViewModel,
            navigateToReportScreen = navigateToReportScreen,
            state = state
        )
    }
}


private suspend fun handleHomeEvent(
    event: HomeUiEvent,
    onLogOut: () -> Unit,
    snackState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    when (event) {
        HomeUiEvent.LogOut -> onLogOut()
        is HomeUiEvent.ShowAlert -> {
            coroutineScope.launch {
                snackState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
}

@Composable
private fun Screen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navigateToReportScreen: (Int, Int) -> Unit,
    state: HomeUiState
) {
    val guard by homeViewModel.guardData.collectAsState()
    val routes by homeViewModel.routes.collectAsState()
    val timerValue by homeViewModel.timer.collectAsState()

    var selectedHomeNav by rememberSaveable {
        mutableStateOf(HomeType.Home)
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                modifier = modifier.fillMaxWidth(),
                fullname = guard?.fullName
            )
        },
        bottomBar = {
            HomeBottomBar(
                timer = timerValue,
                checkInStatus = state.checkStatus,
                selectedScreen = selectedHomeNav,
                onItemSelected = { selectedHomeNav = it },
                onLogOut = {
                    val event = if (state.isStarted) {
                        HomeViewModelEvent.OnLogOutRequested
                    } else {
                        HomeViewModelEvent.OnLogOut
                    }
                    homeViewModel.onEvent(event)
                },
                onCheck = {
                    homeViewModel.onEvent(HomeViewModelEvent.OnCheck)
                },
                isCheckEnabled = false,
                isEnable = !state.isStarted || state.currentIndex == routes.size - 1,
                onStart = { homeViewModel.onEvent(HomeViewModelEvent.StartRound) },
                onStop = { homeViewModel.onEvent(HomeViewModelEvent.StopRound) },
                isStarted = state.isStarted
            )
        },
        floatingActionButton = {
            if (routes.isNotEmpty()) {
                AddReportButton(state.isStarted) {
                    val roundId = state.roundId
                    val routeId = routes[state.currentIndex].order
                    navigateToReportScreen(routeId, roundId.toInt())
                }
            }
        }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValue.calculateTopPadding())
        ) {
            when {
                state.isLoading -> BigSpinner(paddingValue)
                routes.isEmpty() -> EmptyRoutesMessage()
                else -> HomeScreenContent(
                    state = state,
                    routes = routes,
                    timer = timerValue,
                    updateIndex = { homeViewModel.onEvent(HomeViewModelEvent.UpdateIndex) }
                )
            }
        }
    }
}


@Composable
private fun EmptyRoutesMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            "No hay rutas disponibles",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    routes: List<Route>,
    timer: Long,
    updateIndex: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        val currentSectionName = getCurrentSectionName(routes, state.currentIndex)

        UrbanizationMapScreen(
            latitude = URBANIZATION_LATITUDE,
            longitude = URBANIZATION_LONGITUDE,
            title = "$URBANIZATION_NAME - $currentSectionName",
            boundaryPoints = URBANIZATION_BOUNDARY,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(MAP_HEIGHT_FRACTION)
        )


        HomeScreenList(
            state = state,
            routes = routes,
            updateIndex = updateIndex
        )
    }
}


@Composable
private fun getCurrentSectionName(routes: List<Route>, currentIndex: Int): String {
    return if (routes.isNotEmpty() && currentIndex < routes.size) {
        routes[currentIndex].name
    } else {
        "Sección"
    }
}