package com.clerodri.binnacle.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
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
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(
            text = timer.formatTime(),
            modifier = Modifier
                .heightIn()
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = 50.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Normal
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
    isEnable: Boolean,
    isStarted: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    val buttonText = if (isStarted) stringResource(R.string.btn_finalizar_text)
    else stringResource(R.string.btn_start_text)

    var openDialog by remember { mutableStateOf(false) }
    ElevatedButton(
        modifier = Modifier
            .width(150.dp)
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
            text = buttonText, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold
        )
    }
    if (openDialog) {
        CheckInDialogComponent(
            title = if (isStarted) "FINALIZAR RONDA" else "INICIAR RONDA",
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
fun Timer(modifier: Modifier, timer: Long) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TimerHomeComponent(timer = timer)

    }
//    HomeDividerTextComponent(modifier)
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
    BasicAlertDialog(
        onDismissRequest = { onDismissRequest() },
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
    TopAppBar(
        modifier = modifier
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

