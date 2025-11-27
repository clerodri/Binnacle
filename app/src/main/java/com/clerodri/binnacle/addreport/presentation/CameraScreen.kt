package com.clerodri.binnacle.addreport.presentation

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clerodri.binnacle.addreport.presentation.components.CameraControlButton
import com.clerodri.binnacle.addreport.presentation.components.CaptureButton
import com.clerodri.binnacle.addreport.presentation.components.LoadingOverlay

//  Constants
private const val DEBOUNCE_TIME = 300L
private const val TOP_PADDING = 50
private const val HORIZONTAL_PADDING = 30
private const val CLOSE_ICON_SIZE = 26


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
                    addReportViewModel.onReportEvent(AddReportEvent.OnSwitchCamera)
                }
            },
            onClose = {
                if (isClickAllowed()) {
                    onCloseCamara()
                }
            },
            modifier = Modifier.align(Alignment.TopCenter),
        )

        CaptureButton(
            isLoading = state.isLoading,
            onClick = {
                if (!state.isLoading && isClickAllowed()) {
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

