package com.clerodri.binnacle.home.presentation

import android.Manifest
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.home.domain.HomeType
import com.clerodri.binnacle.home.domain.Route
import com.clerodri.binnacle.home.presentation.components.ArrowIndicator
import com.clerodri.binnacle.home.presentation.components.HeadingTextComponent
import com.clerodri.binnacle.home.presentation.components.HomeBottomBar
import com.clerodri.binnacle.home.presentation.components.HomeDividerTextComponent
import com.clerodri.binnacle.home.presentation.components.HomeTimerComponent
import com.clerodri.binnacle.home.presentation.components.HomeTopBar
import com.clerodri.binnacle.location.presentation.LocationViewModel
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: LocationViewModel,
    addReport: () -> Unit,
) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
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


@Composable
fun Screen(modifier: Modifier = Modifier, viewModel: LocationViewModel, addReport: () -> Unit) {

    var selectedHomeNav by rememberSaveable {
        mutableStateOf(HomeType.Home)
    }
    var showFab by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            HomeTopBar(modifier = modifier.fillMaxWidth())
        },
        bottomBar = {
            HomeBottomBar(selectedHomeNav) {
                selectedHomeNav = it
            }
        },
        floatingActionButton = {
            HomeAddReport(showFab) {
                addReport()
            }
        }
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValue.calculateTopPadding())
        ) {
            HomeTimerComponent(modifier = Modifier.fillMaxWidth()) {

            }
            HomeDividerTextComponent(Modifier.fillMaxWidth())
            HomeScreenContent(contentPadding = paddingValue) {

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
        Route("1", "SOLAR 14 FAM.HOOLIGAN"),
        Route("3", "SOLAR Y FAM.HIGGINS"),
        Route("5", "SOLAR 51"),
        Route("6", "CANCHA DE TENNIS")
    ).sortedBy { it.id.toInt() }
    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding()),
//        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
//        HomeTimerComponent(modifier = Modifier.fillMaxWidth()) {
//            onStart()
//        }
//        HomeDividerTextComponent()

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
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    if (index == currentIndex) {
                        Icon(
                            modifier = Modifier.size(50.dp),
                            imageVector = Icons.Default.Check, contentDescription = null
                        )
                    }
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
}

@Composable
fun RouteContent(
    item: Route,
    isActive: Boolean,
    onNextClick: () -> Unit
) {
//    Column(
//        horizontalAlignment = Alignment.Start,
//        verticalArrangement = Arrangement.Center)
//    {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
//            if (isActive) {
//                Text(
//                    modifier = Modifier.padding(16.dp),
//                    textAlign = TextAlign.Start,
//                    fontSize = 18.sp,
//                    fontStyle = FontStyle.Italic,
//                    color = MaterialTheme.colorScheme.onSurface,
//                    text = "Actual"
//                )
//            }
        Icon(
            modifier = Modifier.size(40.dp),
            imageVector = Icons.Filled.DoubleArrow,
            contentDescription = null,
            tint = Color.Black
        )
        HeadingTextComponent(
            value = item.name,
            isActive = isActive
        )

//            Text(
//                text = item.name,
//                modifier = Modifier
//                    .padding(16.dp)
//                    .align(Alignment.CenterVertically),
//                fontSize = 26.sp,
//                fontStyle = FontStyle.Italic,
//                color = Color.Gray.copy(0.6f),
//                fontFamily = FontFamily.Monospace
//            )
//        }

//        if (isActive) {
//            ElevatedButton(
//                onClick = { onNextClick() },
//                colors = ButtonColors(
//                    containerColor = Secondary,
//                    contentColor = Color.White,
//                    disabledContainerColor = Secondary.copy(0.8f),
//                    disabledContentColor = Color.Gray.copy(0.6f)
//                )
//            ) {
//                Icon(
//                    imageVector = Icons.Outlined.Done, contentDescription = null,
//                )
//                Spacer(Modifier.width(10.dp))
//                Text(
//                    text = "Siguiente",
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        }
    }
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




