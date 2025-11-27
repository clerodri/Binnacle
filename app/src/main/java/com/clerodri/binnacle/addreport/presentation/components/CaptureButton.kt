package com.clerodri.binnacle.addreport.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.ui.theme.BackGroundAppColor

//  Constants
private const val BOTTOM_PADDING = 80
private const val BUTTON_SIZE = 45
private const val CAPTURE_BUTTON_SIZE = 60
private const val ICON_SIZE = 40
private const val CLOSE_ICON_SIZE = 26
private const val CORNER_RADIUS = 14
private const val LOADING_OVERLAY_ALPHA = 0.5f
private const val LOADING_INDICATOR_SIZE = 60
private const val PROGRESS_INDICATOR_WIDTH = 4

@Composable
fun CameraControlButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    iconSize: androidx.compose.ui.unit.Dp = ICON_SIZE.dp,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(CORNER_RADIUS.dp))
            .size(BUTTON_SIZE.dp)
            .background(BackGroundAppColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(iconSize)
        )
    }
}


@Composable
fun CaptureButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = BOTTOM_PADDING.dp),

        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .size(CAPTURE_BUTTON_SIZE.dp)
                .background(BackGroundAppColor)
                .clickable(
                    enabled = !isLoading,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    onClick = onClick
                )
                .semantics {
                    contentDescription = "Take photo"
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(CLOSE_ICON_SIZE.dp)
            )
        }
    }
}


@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = LOADING_OVERLAY_ALPHA)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = PROGRESS_INDICATOR_WIDTH.dp,
            modifier = Modifier.size(LOADING_INDICATOR_SIZE.dp)
        )
    }
}