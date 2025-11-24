package com.clerodri.binnacle.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.clerodri.binnacle.R
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.HomeType
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.Primary
import com.clerodri.binnacle.ui.theme.Secondary
import com.clerodri.binnacle.ui.theme.TextColor
import com.clerodri.binnacle.util.formatTime


@Composable
fun ArrowIndicator() {
    Spacer(modifier = Modifier.height(40.dp))
    Icon(
        imageVector = Icons.Filled.ArrowUpward,
        contentDescription = null,
        tint = BackGroundAppColor.copy(0.6f),
        modifier = Modifier.size(50.dp)
    )
    Spacer(modifier = Modifier.height(40.dp))
}


@Composable
fun TimerHomeComponent(timer: Long) {
    Row {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.timer)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.Transparent)
        ) {

            LottieAnimation(
                composition = composition,
                isPlaying = true,
                iterations = LottieConstants.IterateForever,
                speed = 1f,
                modifier = Modifier.size(50.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(
            text = timer.formatTime(),
            modifier = Modifier
                .heightIn()
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = 30.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal
            ),
            color = Color.Black,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun HomeDividerTextComponent(modifier: Modifier) {
    Row(
        modifier.padding(top = 80.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Text(
            text = stringResource(R.string.rondas),
            fontSize = 14.sp,
            color = TextColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}

@Composable
fun StartButtonComponent(
    value: String, isEnable: Boolean, isStarted: Boolean, onStart: () -> Unit, onStop: () -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }
    ElevatedButton(
        modifier = Modifier
            .width(175.dp)
            .heightIn(50.dp), onClick = {
            openDialog = true
        }, enabled = isEnable, colors = ButtonColors(
            containerColor = BackGroundAppColor,
            contentColor = Color.White,
            disabledContainerColor = BackGroundAppColor.copy(0.3f),
            disabledContentColor = Color.Gray.copy(0.6f)
        )
    ) {
        Text(
            text = value, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold
        )
    }
    if (openDialog) {
        CheckInDialogComponent(title = if (isStarted) "FINALIZAR RONDA" else "INICIAR RONDA",
            message = if (isStarted) "Seguro que desea finalizar la ronda?" else "Seguro que desea iniciar la ronda?",
            onCancel = { openDialog = false },
            onConfirm = {
                if (isStarted) onStop() else onStart()
                openDialog = false
            },
            onDismissRequest = { openDialog = false })
    }
}


@Composable
fun HeadingTextComponent(modifier: Modifier = Modifier, value: String, isActive: Boolean) {

    Text(
        text = value,
        modifier = modifier.heightIn(),
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Light,
            fontStyle = FontStyle.Normal
        ),
        color = if (isActive) Color.Black else Color.Gray.copy(alpha = 0.5f),
        textAlign = TextAlign.Center
    )

}

@Composable
fun HomeBottomBar(
    checkInStatus: ECheckIn,
    selectedScreen: HomeType,
    onItemSelected: (HomeType) -> Unit,
    onLogOut: () -> Unit,
    onCheck: () -> Unit,
    isCheckEnabled: Boolean = true
) {
    val isCheckIn = checkInStatus == ECheckIn.STARTED
    var openDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var onConfirmAction by remember { mutableStateOf({ }) }
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .shadow(
                elevation = 20.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

            ), containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {

        HomeType.entries.forEach { item ->
            val selected = selectedScreen == item
            val enabled = if (item == HomeType.Check) isCheckEnabled else true
            NavigationBarItem(selected = selected, onClick = {
                if (enabled) {
                    when (item) {
                        HomeType.Check -> {
                            title = if (isCheckIn) "Check-Out" else "Check-In"
                            message =
                                if (isCheckIn) "Esta seguro de registrar su Check-Out?" else "Esta seguro de registrar su Check-In?"
                            if (checkInStatus == ECheckIn.DONE) onCheck()
                            onConfirmAction = onCheck
                            openDialog = checkInStatus != ECheckIn.DONE

                        }

                        HomeType.LogOut -> {
                            title = "Cerrar Sesión"
                            message = "Esta seguro de cerrar la sesión?"
                            onConfirmAction = onLogOut
                            openDialog = true

                        }

                        else -> onItemSelected(item)
                    }
                }


            }, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Primary,
                unselectedIconColor = Color.Gray.copy(0.6f),
                disabledIconColor = Color.Gray.copy(0.3f),
                disabledTextColor = Color.Gray.copy(0.3f)
            ), label = {
                Text(
                    text = if (item == HomeType.Check && isCheckIn) stringResource(R.string.checkout)
                    else stringResource(id = item.title)
                )
            }, alwaysShowLabel = true, icon = {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = stringResource(id = item.title)
                )
            }, enabled = enabled

            )
        }
    }
    if (openDialog) {
        CheckInDialogComponent(title = title,
            message = message,
            onCancel = { openDialog = false },
            onConfirm = {
                onConfirmAction()
                openDialog = false

            },
            onDismissRequest = { openDialog = false })
    }
}


@Composable
fun Timer(
    modifier: Modifier,
    isTimerRunning: Boolean,
    isEnable: Boolean,
    timer: Long,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val buttonText = if (isTimerRunning) stringResource(R.string.btn_finalizar_text)
    else stringResource(R.string.btn_start_text)

    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimerHomeComponent(timer = timer)

        StartButtonComponent(value = buttonText,
            isEnable = isEnable,
            isStarted = isTimerRunning,
            onStart = { onStart() },
            onStop = { onStop() })

    }
    HomeDividerTextComponent(modifier)
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CheckInDialogComponent(
    title: String,
    message: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = { onDismissRequest() },
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(),
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = AlertDialogDefaults.TonalElevation,

                ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = BackGroundAppColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message, color = MaterialTheme.colorScheme.error
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = { onCancel() }, modifier = Modifier.padding(end = 20.dp),
                        ) {
                            Text(
                                stringResource(R.string.check_cancel),
                                color = Color.Gray.copy(0.8f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Button(
                            onClick = { onConfirm() }, colors = ButtonDefaults.buttonColors(
                                containerColor = BackGroundAppColor

                            )
                        ) {
                            Text(
                                text = stringResource(R.string.dialog_aceptar_txt),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(modifier: Modifier, fullname: String?) {
    TopAppBar(modifier = modifier
        .padding(horizontal = 20.dp, vertical = 10.dp)
        .clip(RoundedCornerShape(100.dp)), colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.White
    ), windowInsets = WindowInsets(0.dp), title = {
        Text(
            text = "$fullname",
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
            fontSize = 25.sp,
            style = MaterialTheme.typography.titleLarge,
            fontStyle = FontStyle.Italic
        )


    }, navigationIcon = {
        Image(
            painter = painterResource(id = R.drawable.guard_logo),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 12.dp, end = 8.dp)
                .size(30.dp)
        )
    })

}


@Composable
fun AddReportButton(showFab: Boolean, addReport: () -> Unit) {
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
fun RouteItem(
    index: Int,
    item: Route,
    isActive: Boolean,
    showArrow: Boolean,
    isLastItem: Boolean,
    onNextClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
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


@Composable
fun RouteContent(
    index: Int, item: Route, isActive: Boolean, isLastItem: Boolean, onNextClick: () -> Unit
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
                onClick = { onNextClick() }, colors = ButtonColors(
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
                .padding(8.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = "# ${(index + 1)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = if (isActive) BackGroundAppColor else Color.Transparent.copy(0.1f)
            )
        }
        HeadingTextComponent(
            value = item.name, isActive = isActive, modifier = Modifier.weight(1f)
        )
    }
}

