package com.clerodri.binnacle.home.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.home.domain.model.ECheckIn
import com.clerodri.binnacle.home.domain.model.HomeType
import com.clerodri.binnacle.ui.theme.Primary

/**
 * Author: Ronaldo R.
 * Date:  11/26/2025
 * Description:
 **/
@Composable
fun HomeBottomBar(
    timer: Long,
    checkInStatus: ECheckIn,
    selectedScreen: HomeType,
    onItemSelected: (HomeType) -> Unit,
    onLogOut: () -> Unit,
    onCheck: () -> Unit,
    isCheckEnabled: Boolean = true,
    isEnable: Boolean,
    isStarted: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
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
    )
    {

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

    Box(
        modifier = Modifier
            .fillMaxWidth().offset(y = (-150).dp),
        contentAlignment = Alignment.Center
    ) {
        Timer(modifier = Modifier.fillMaxWidth(), timer = timer)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp).offset(y = (-60).dp),
        contentAlignment = Alignment.Center
    ) {
        StartButtonComponent(
            isEnable = isEnable,
            isStarted = isStarted,
            onStart = onStart,
            onStop = onStop
        )
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

