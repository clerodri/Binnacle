package com.clerodri.binnacle.home.presentation

import android.location.Location
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.home.domain.HomeType
import com.clerodri.binnacle.home.domain.Route
import com.clerodri.binnacle.home.presentation.components.ArrowIndicator
import com.clerodri.binnacle.home.presentation.components.HomeBottomBar
import com.clerodri.binnacle.home.presentation.components.HomeDividerTextComponent
import com.clerodri.binnacle.home.presentation.components.HomeTimerComponent
import com.clerodri.binnacle.home.presentation.components.HomeTopBar
import com.clerodri.binnacle.location.presentation.LocationViewModel
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.Primary
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: LocationViewModel,
    addReport: () -> Unit,
) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    Scaffold { padding ->
        Screen(modifier = Modifier.padding(padding), viewModel, addReport)
    }
    LaunchedEffect(true) {
        locationPermissions.launchMultiplePermissionRequest()
    }
    LaunchedEffect(true) {
        viewModel.getCurrentLocation()
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(modifier: Modifier = Modifier, viewModel: LocationViewModel, addReport: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    var selectedHomeNav by rememberSaveable {
        mutableStateOf(HomeType.Home)
    }
    var showFab by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            HomeTopBar(modifier = modifier, scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            HomeBottomBar(selectedHomeNav) {
                selectedHomeNav = it
            }
        },
        floatingActionButton = {
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
    ) { paddingValue ->
        HomeScreenContent(contentPadding = paddingValue) {

        }
    }
}


@Composable
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
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isActive) BackGroundAppColor else BackGroundAppColor.copy(0.3f),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            RouteContent(item = item, isActive = isActive, onNextClick = onNextClick)
        }
    }
}

@Composable
fun RouteContent(
    item: Route,
    isActive: Boolean,
    onNextClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isActive) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = "Current"
                )
            }

            Text(
                text = item.name,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically),
                fontSize = 26.sp,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(0.6f),
                fontFamily = FontFamily.Monospace
            )
        }

        if (isActive) {
            Button(onClick = onNextClick) {
                Text("Next")
            }
        }
    }
}


@Composable
fun HomeScreenContent(
    contentPadding: PaddingValues,
    onStart: () -> Unit
) {
    val items = listOf(
        Route("1", "RUTA A"),
        Route("3", "RUTA B"),
        Route("2", "RUTA D"),
    ).sortedBy { it.id.toInt() }
    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeTimerComponent(modifier = Modifier.fillMaxWidth()) {
            onStart()
        }
        HomeDividerTextComponent()

        items.forEachIndexed { index, item ->
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
                RouteItem(
                    item = item,
                    isActive = index == currentIndex,
                    showArrow = index != currentIndex,
                    onNextClick = {
                        if (currentIndex < items.size - 1) {
                            currentIndex++
                        }
                    }
                )
            }
        }
    }
}


/*
@Composable
fun HomeScreenContent(
    contentPadding: PaddingValues,
) {
    val items = listOf(
                        Route("1", "RUTA A"),
                        Route("3", "RUTA B"),
                        Route("2", "RUTA D"),
                        ).sortedBy { it.id.toInt() }
    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = contentPadding.calculateTopPadding(), bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeTimerComponent(modifier = Modifier.fillMaxWidth())
        HomeDividerTextComponent()
        items.forEachIndexed { index, item ->
            AnimatedVisibility(
                visible = index >= currentIndex && index <= currentIndex + 1,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (index != currentIndex) {
                        Spacer(modifier = Modifier.height(60.dp))
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(60.dp))
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 50.dp)
                            .heightIn(100.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (index == currentIndex) BackGroundAppColor else BackGroundAppColor.copy(
                                    0.3f
                                ),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center,
                    )
                    {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        )
                        {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            )
                            {
                                if (index == currentIndex) {
                                    Text(
                                        modifier = Modifier.padding(16.dp),
                                        textAlign = TextAlign.Start,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        text = "Current"
                                    )
                                }

                                Text(
                                    text = item.name,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(Alignment.CenterVertically),
                                    fontSize = 26.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White.copy(0.6f),
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (index == currentIndex) {
                                Button(onClick = {
                                    if (currentIndex < items.size - 1) {
                                        currentIndex++
                                    }
                                })
                                {
                                    Text("Next")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
*/

/*
    //                Spacer(modifier = Modifier.height(16.dp))
    // GoogleMapComponent()
//                    Text(text = "Ruta LATITUDE ${viewModel.currentLocation?.latitude}")
//                    Text(text = "Ruta LONGITUDE ${viewModel.currentLocation?.longitude}")
//                    viewModel.currentLocation?.let { GoogleMapComponent(location = it) }


//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                contentPadding = PaddingValues(top = contentPadding.calculateTopPadding()),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//            ) {
//                item {
//                    HomeTimerComponent(modifier = Modifier.fillMaxWidth())
//                    HomeDividerTextComponent()
//                }
//                items(10){
//                    Box(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                            .heightIn(100.dp)
//                            .fillMaxWidth()
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Primary)
//                    )
//
//                    {
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//                }
//
//            }
*/

@Composable
fun RouteItemComponent(modifier: Modifier = Modifier, value: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 50.dp)
            .heightIn(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Primary)
    )

    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Text(
                text = value,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 26.sp,
                fontStyle = FontStyle.Italic,
                color = Color.LightGray,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}


@Composable
fun GoogleMapComponent(modifier: Modifier = Modifier, location: Location) {
    val singapore = LatLng(location.latitude, location.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 16f)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = singapore),
            title = "Laguna dorada",
            snippet = "Marker in Laguna dorada"
        )
    }
}


