package com.clerodri.binnacle.addreport.presentation

import android.util.Log
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.SurfaceRequest
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
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clerodri.binnacle.ui.theme.BackGroundAppColor

//  Constants
private const val TAG = "CameraScreen"
private const val DEBOUNCE_TIME = 300L
private const val TOP_PADDING = 50
private const val HORIZONTAL_PADDING = 30
private const val BOTTOM_PADDING = 80
private const val BUTTON_SIZE = 45
private const val CAPTURE_BUTTON_SIZE = 60
private const val ICON_SIZE = 40
private const val CLOSE_ICON_SIZE = 26
private const val CORNER_RADIUS = 14
private const val LOADING_OVERLAY_ALPHA = 0.5f
private const val LOADING_INDICATOR_SIZE = 60
private const val PROGRESS_INDICATOR_WIDTH = 4

/**
 * Camera Screen Composable
 *
 * Displays camera preview with controls for:
 * - Switching between front/back cameras
 * - Taking photos
 * - Closing camera
 *
 * @param modifier Modifier for styling
 * @param onCloseCamara Callback when user closes camera
 * @param addReportViewModel ViewModel instance for camera operations
 * @param lifecycleOwner Lifecycle owner for camera binding
 * @param isFrontCamera Whether front camera is currently selected
 */
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onCloseCamara: () -> Unit,
    addReportViewModel: AddReportViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    isFrontCamera: Boolean
) {
    val context = LocalContext.current
    val surfaceRequest by addReportViewModel.surfaceRequest.collectAsStateWithLifecycle()
    val state by addReportViewModel.state.collectAsStateWithLifecycle()


    var lastClickTime by remember { mutableStateOf(0L) }

    fun isClickAllowed(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastClickTime > DEBOUNCE_TIME) {
            lastClickTime = currentTime
            true
        } else {
            false
        }
    }


    val cameraSelector = remember(isFrontCamera) {

        val lensFacing = if (isFrontCamera) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }

        CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
    }

    LaunchedEffect(isFrontCamera) {
        addReportViewModel.bindToCamera(context.applicationContext, lifecycleOwner, cameraSelector)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        CameraPreview(surfaceRequest = surfaceRequest)

        TopControlButtons(
            onSwitchCamera = {
                if (isClickAllowed()) {
                    Log.d(TAG, "Switch camera clicked")
                    addReportViewModel.onReportEvent(AddReportEvent.OnSwitchCamera)
                }
            },
            onClose = {
                if (isClickAllowed()) {
                    Log.d(TAG, "Close camera clicked")
                    onCloseCamara()
                }
            },
            modifier = Modifier.align(Alignment.TopCenter),
        )

        CaptureButton(
            isLoading = state.isLoading,
            onClick = {
                if (!state.isLoading && isClickAllowed()) {
                    Log.d(TAG, "Capture photo clicked")
                    addReportViewModel.onReportEvent(AddReportEvent.OnTakePhoto)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )


        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}


@Composable
private fun CameraPreview(surfaceRequest: SurfaceRequest?) {

    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            modifier = Modifier.fillMaxSize()
        )
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }
}

@Composable
private fun TopControlButtons(
    onSwitchCamera: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = TOP_PADDING.dp,
                end = HORIZONTAL_PADDING.dp,
                start = HORIZONTAL_PADDING.dp
            ),

        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Switch camera button
        CameraControlButton(
            icon = Icons.Default.Cameraswitch,
            contentDescription = "Switch camera",
            onClick = onSwitchCamera,
            modifier = Modifier.semantics {
                contentDescription = "Switch between front and back camera"
            }
        )

        // Close camera button
        CameraControlButton(
            icon = Icons.Default.Close,
            contentDescription = "Close camera",
            onClick = onClose,
            iconSize = CLOSE_ICON_SIZE.dp,
            modifier = Modifier.semantics {
                contentDescription = "Close camera and return to report"
            }
        )
    }
}


@Composable
private fun CameraControlButton(
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
private fun CaptureButton(
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
private fun LoadingOverlay() {
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