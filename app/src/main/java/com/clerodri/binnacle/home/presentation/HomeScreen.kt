package com.clerodri.binnacle.home.presentation

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.home.domain.HomeType
import com.clerodri.binnacle.home.presentation.components.HomeDividerTextComponent
import com.clerodri.binnacle.home.presentation.components.HomeTimerComponent
import com.clerodri.binnacle.location.presentation.LocationViewModel
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
fun HomeScreen(viewModel: LocationViewModel) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    Scaffold { padding ->
        Screen(modifier = Modifier.padding(padding), viewModel)
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
fun Screen(modifier: Modifier = Modifier, viewModel: LocationViewModel) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState()
    )
    var selectedHomeNav by rememberSaveable {
        mutableStateOf(HomeType.Home)
    }
    Scaffold(
        topBar = {
            HomeTopBar(modifier = modifier, scrollBehavior = scrollBehavior)
        },
        bottomBar = {
            HomeBottomBar(selectedHomeNav){
                    selectedHomeNav = it
            }
        }
    ) { paddingValue ->
        HomeScreenContent(contentPadding = paddingValue, viewModel = viewModel)
    }
}

@Composable
fun HomeBottomBar(selectedScreen: HomeType, onItemSelected: (HomeType) -> Unit) {
    NavigationBar(
        modifier = Modifier.padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

            ),
        containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        HomeType.entries.forEach { item ->
            val selected = selectedScreen == item

            NavigationBarItem(
                selected = selected,
                onClick = { onItemSelected(item) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(modifier: Modifier = Modifier, scrollBehavior: TopAppBarScrollBehavior) {
    TopAppBar(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(100.dp)),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.6f)
        ),

        windowInsets = WindowInsets(top = 0.dp),
        title = {

            Text(
                text = "Ronaldo Rodriguez - Laguna Dorada",
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleLarge,
                fontStyle = FontStyle.Italic
            )


        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
            )
        }
    )

}


// LISTA DE RUTAS
@Composable
fun HomeScreenContent(
    contentPadding: PaddingValues,
    items: List<String> = listOf(
        "1", "2", "3", "4", "5", "6", "8", "9", "11"
    ),
    viewModel: LocationViewModel
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Enables scrolling
            .padding(top = contentPadding.calculateTopPadding(), bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeTimerComponent(modifier = Modifier.fillMaxWidth())
        HomeDividerTextComponent()
        items.forEach { _ ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
//                    .heightIn(500.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Primary)
            ) {
                Column {

//                    Text(text = "Ruta LATITUDE ${viewModel.currentLocation?.latitude}")
//                    Text(text = "Ruta LONGITUDE ${viewModel.currentLocation?.longitude}")
//                    viewModel.currentLocation?.let { GoogleMapComponent(location = it) }
                }

//                Spacer(modifier = Modifier.height(16.dp))
                // GoogleMapComponent()
            }
        }
    }
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


@Composable
fun HomeScreenPreview() {

}
//    Screen(viewModel = viewModel)
