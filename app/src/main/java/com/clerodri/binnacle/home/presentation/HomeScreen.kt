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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.home.domain.HomeType
import com.clerodri.binnacle.home.domain.Route
import com.clerodri.binnacle.home.presentation.components.ArrowIndicator
import com.clerodri.binnacle.home.presentation.components.HeadingTextComponent
import com.clerodri.binnacle.home.presentation.components.HomeDividerTextComponent
import com.clerodri.binnacle.home.presentation.components.StartButtonComponent
import com.clerodri.binnacle.home.presentation.components.TimerHomeComponent
import com.clerodri.binnacle.location.presentation.LocationViewModel
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.Primary
import com.clerodri.binnacle.ui.theme.Secondary
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
            SnackbarHost(snackbarHostState, modifier = Modifier.padding(bottom = 150.dp))
        }
    ) { padding ->
        Screen(
            modifier = Modifier.padding(top = padding.calculateTopPadding()),
            locationViewModel = locationViewModel,
            homeViewModel = homeViewModel,
            addReport = addReport,
            onLogOut = onLogOut
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
                is HomeUiEvent.ShowSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(event.message)
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
    addReport: () -> Unit,
    onLogOut: () -> Unit
) {
    val state by homeViewModel.state.collectAsState()

    val routes by homeViewModel.routes.collectAsState()

    var selectedHomeNav by rememberSaveable {
        mutableStateOf(HomeType.Home)
    }
    Scaffold(
        topBar = {
            HomeTopBar(modifier = modifier.fillMaxWidth())
        },
        bottomBar = {
            HomeBottomBar(
                selectedHomeNav,
                onItemSelected = { selectedHomeNav = it },
                onLogOut = {
                    if (state.isStarted) {
                        homeViewModel.onEvent(HomeViewModelEvent.OnLogOut)
                    } else {
                        onLogOut()
                    }
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

@Composable
private fun HeaderHome(
    modifier: Modifier,
    isStarted: Boolean,
    isRoundBtnEnabled: Boolean,
    timer: String,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val buttonText = if (isStarted) stringResource(R.string.btn_finalizar_text)
    else stringResource(R.string.btn_start_text)

    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimerHomeComponent(timer = timer)

        StartButtonComponent(
            buttonText, isRoundBtnEnabled = isRoundBtnEnabled,
            isStarted = isStarted,
            onStart = { onStart() },
            onStop = { onStop() }
        )

    }
    HomeDividerTextComponent(modifier)
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

@Composable
private fun RouteItem(
    index: Int,
    item: Route,
    isActive: Boolean,
    showArrow: Boolean,
    isLastItem: Boolean,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showArrow) {
            ArrowIndicator()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.spacedBy(100.dp),
        ) {
            Box(
                modifier = Modifier
                    .heightIn(100.dp)
                    .border(
                        1.dp,
                        color = if (isActive) BackGroundAppColor.copy(0.3f) else Color.Transparent.copy(
                            0.2f
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(
                        if (isActive) BackGroundAppColor.copy(0.3f) else Color.Transparent.copy(0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                RouteContent(
                    index = index,
                    item = item,
                    isActive = isActive,
                    isLastItem = isLastItem,
                    onNextClick = onNextClick
                )
            }
        }

    }

}


/*@Composable
fun RouteItem(
    item: Route,
    isActive: Boolean,
    showArrow: Boolean,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showArrow) {
            ArrowIndicator()
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 50.dp)
                .heightIn(100.dp)
                .fillMaxWidth()
                .border(
                    1.dp,
                    color = BackGroundAppColor.copy(0.6f),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    if (isActive) BackGroundAppColor.copy(0.3f) else Color.Transparent.copy(0.2f),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center,

            ) {
            RouteContent(item = item, isActive = isActive, onNextClick = onNextClick)
        }
    }
}*/

@Composable
private fun RouteContent(
    index: Int,
    item: Route,
    isActive: Boolean,
    isLastItem: Boolean,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        if (isActive && !isLastItem) {
            ElevatedButton(
                onClick = { onNextClick() },
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = BackGroundAppColor,
                    disabledContainerColor = Secondary.copy(0.8f),
                    disabledContentColor = Color.Gray.copy(0.6f)
                )
            ) {
                Text(
                    stringResource(R.string.listo_text),
                    fontSize = 16.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        Box(
            modifier = Modifier
                .width(100.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "# ${(index + 1)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) BackGroundAppColor else Color.Transparent.copy(0.1f)
            )
        }
        HeadingTextComponent(
            value = item.name,
            isActive = isActive,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(modifier: Modifier) {
    TopAppBar(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(100.dp)),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f)
        ),
        windowInsets = WindowInsets(0.dp),
        title = {
            Text(
                text = "Ronaldo Rodriguez - Laguna Dorada",
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                fontSize = 25.sp,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic
            )


        },
        navigationIcon = {
//            Icon(
//                painter = painterResource(id = R.drawable.),
//                contentDescription = null,
//                modifier = Modifier
//                    .padding(start = 12.dp, end = 8.dp).size(40.dp)
//            )
            Icon(
                imageVector = Icons.Outlined.VerifiedUser,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(30.dp)

            )
        }
    )

}


@Composable
private fun HomeAddReport(showFab: Boolean, addReport: () -> Unit) {
    AnimatedVisibility(visible = showFab) {
        FloatingActionButton(
            onClick = { addReport() },
        ) {
            Icon(
                Icons.Filled.AddCircle,
                stringResource(id = R.string.add_report),
                tint = BackGroundAppColor
            )
        }
    }
}


@Composable
fun HomeBottomBar(
    selectedScreen: HomeType,
    onItemSelected: (HomeType) -> Unit,
    onLogOut: () -> Unit

) {
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

            ),
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {

        HomeType.entries.forEach { item ->
            val selected = selectedScreen == item

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (item == HomeType.LogOut) {
                        onLogOut()
                    } else {
                        onItemSelected(item)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    unselectedIconColor = Color.Gray.copy(0.6f)
                ),
                label = {
                    Text(text = stringResource(id = item.title))
                },
                alwaysShowLabel = true,
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = stringResource(id = item.title)
                    )
                }
            )
        }

    }
}


