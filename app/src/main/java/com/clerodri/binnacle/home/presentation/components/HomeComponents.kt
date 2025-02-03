package com.clerodri.binnacle.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.home.domain.HomeType
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.Primary
import com.clerodri.binnacle.ui.theme.TextColor


@Composable
fun HomeBottomBar(selectedScreen: HomeType, onItemSelected: (HomeType) -> Unit) {
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
 fun TimerHomeComponent() {
    Row {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(40.dp),
            painter = painterResource(id = R.drawable.ic_timer),
            contentDescription = null
        )
        Text(
            text = "00:00:00",
            modifier = Modifier
                .heightIn()
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal
            ),
            color = Color.Black,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun HomeDividerTextComponent(modifier: Modifier) {
    Row(
        modifier.padding(top = 80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Text(
            text = stringResource(R.string.rondas), fontSize = 14.sp, color = TextColor,
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
    value: String,
    isRoundBtnEnabled: Boolean,
    isStarted: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    ElevatedButton(
        modifier = Modifier
            .width(175.dp)
            .heightIn(50.dp),
        onClick = {
            if (isStarted) {
                onStop()
            } else {
                onStart()
            }
        },
        enabled = isRoundBtnEnabled,
        colors = ButtonColors(
            containerColor = BackGroundAppColor,
            contentColor = Color.White,
            disabledContainerColor = BackGroundAppColor.copy(0.3f),
            disabledContentColor = Color.Gray.copy(0.6f)
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayArrow, contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(ButtonDefaults.IconSize)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview
@Composable
fun TestingButton(modifier: Modifier = Modifier) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {}
            ) {
                Text("Comenzar Ronda")
            }
            ElevatedButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow, contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(8.dp))
                Text("Comenzar Ronda")
            }
            FilledTonalButton(onClick = {}) {
                Text("Comenzar Ronda")
            }
            OutlinedButton(onClick = {}) {
                Text("Comenzar Ronda")
            }

            TextButton(onClick = {}) {
                Text("Comenzar Ronda")
            }


        }
    }
}


@Composable
fun HeadingTextComponent(modifier: Modifier = Modifier, value: String, isActive: Boolean) {

    Text(
        text = value,
        modifier = modifier.heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Light,
            fontStyle = FontStyle.Normal
        ),
        color = if (isActive) Color.Black else Color.Gray.copy(alpha = 0.5f),
        textAlign = TextAlign.Center
    )

}

