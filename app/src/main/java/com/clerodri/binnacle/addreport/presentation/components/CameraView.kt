package com.clerodri.binnacle.addreport.presentation.components

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clerodri.binnacle.addreport.presentation.AddReportViewModel

@Composable
fun CameraView(
    viewModel: AddReportViewModel,
    modifier: Modifier = Modifier
) {
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()

    AndroidView(
        factory = { context ->
            PreviewView(context)
        },
        modifier = modifier,
        update = { previewView ->
            surfaceRequest?.let { request ->
                previewView.surfaceProvider.onSurfaceRequested(request)
            }
        }
    )
}
