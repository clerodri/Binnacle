package com.clerodri.binnacle.addreport.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.addreport.domain.AddReportUseCase
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.TakePhotoUseCase
import com.clerodri.binnacle.addreport.domain.UploadPhotoUseCase
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.core.components.SnackBarType
import com.clerodri.binnacle.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * UiState for the Add Report screen
 */
data class AddReportUiState(
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val openCamera: Boolean = false,
    val btnCameraEnable: Boolean = false,
    val bitmap: Bitmap? = null,
    val titleError: String? = null,
    val snackBarType: SnackBarType? = null,
    val hideFloatingActionButton: Boolean = false,
    val photoFileName: String? = null
)

@HiltViewModel
class AddReportViewModel @Inject constructor(
    private val addReportUseCase: AddReportUseCase,
    private val photoUseCase: TakePhotoUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReportUiState())
    val state = _uiState.asStateFlow()

    // Used to set up a link between the Camera and your UI.
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val _eventChannel = Channel<ReportUiEvent>()
    internal fun getEventChannel() = _eventChannel.receiveAsFlow()

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
        }
    }

    @SuppressLint("RestrictedApi")
    private val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .setResolutionSelector(
            ResolutionSelector.Builder()
                .setAllowedResolutionMode(ResolutionSelector.PREFER_CAPTURE_RATE_OVER_HIGHER_RESOLUTION)
                .build()
        )
        .setTargetRotation(Surface.ROTATION_0)
        .setBufferFormat(ImageFormat.YUV_420_888)
        .build()

    fun onReportEvent(event: AddReportEvent) {

        when (event) {
            is AddReportEvent.OnAddReport -> {
                if (!hasInternetConnection(context)) {
                    sendScreenEvent(event = ReportUiEvent.OnError("No hay conexión a internet", SnackBarType.Error))
                    return
                }
                if (_uiState.value.title.isBlank()) {
                    _uiState.value = _uiState.value.copy(titleError = "El titulo es requerido")
                    return
                }

                val photoFileName = _uiState.value.photoFileName
                Log.d("CameraX", "PHONEFILE: $photoFileName")

                sendScreenEvent(event = ReportUiEvent.OnSendingReport)
                _uiState.update { it.copy(isLoading = true) }

                val report = Report(
                    title = event.title,
                    description = event.detail,
                    roundId = event.roundId,
                    image = photoFileName,
                    imageType = "image/jpeg"
                )
                createReport(report)

            }

            AddReportEvent.ClearFields -> {
                _uiState.update {
                    it.copy(description = "", title = "", titleError = null)
                }
            }


            is AddReportEvent.OnUpdateDescription -> {
                _uiState.update {
                    it.copy(description = event.description)
                }
            }

            is AddReportEvent.OnUpdateTitle -> {
                _uiState.update {
                    it.copy(title = event.title, titleError = null)
                }
            }

            is AddReportEvent.OnTakePhoto -> {
                _uiState.value = _uiState.value.copy(isLoading = true)
                viewModelScope.launch {
                    val result = photoUseCase(imageCapture)
                    Log.d("CameraX", "onReportEvent: $result")
                    val fileName = if (result != null) {
                        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                        "photo_${formatter.format(Date())}.jpg"
                    } else null

                    _uiState.update {
                        it.copy(
                            openCamera = false,
                            isLoading = false,
                            bitmap = result,
                            photoFileName = fileName
                        )
                    }

                }

            }

            AddReportEvent.OnOpenCamera -> _uiState.update { it.copy(openCamera = true) }
            AddReportEvent.OnCloseCamera -> _uiState.update { it.copy(openCamera = false) }
            AddReportEvent.NoCameraAllowed ->
                sendScreenEvent(event = ReportUiEvent.OnError("No tiene permisos para usar la CAMARA", SnackBarType.Warning))

        }

    }

    private fun createReport(report: Report) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, hideFloatingActionButton = true) }
            delay(4000)
            when (val result = addReportUseCase(report)) {
                is Result.Failure -> {
                    when (result.error) {
                        DataError.Report.REQUEST_TIMEOUT -> {
                            Log.d("CameraX", "createReport: ${result.error}")
                            sendScreenEvent(event = ReportUiEvent.OnError("Servidor no disponible", SnackBarType.Error))
                            _uiState.update { it.copy(isLoading = false) }
                        }

                        DataError.Report.NO_INTERNET -> {
                            sendScreenEvent(event = ReportUiEvent.OnError("No hay internet", SnackBarType.Error))
                            _uiState.update { it.copy(isLoading = false) }
                            Log.d("CameraX", "createReport: ${result.error}")
                        }

                        DataError.Report.SERVICE_UNAVAILABLE -> {
                            sendScreenEvent(event = ReportUiEvent.OnError("Servidor no disponible", SnackBarType.Error))
                            _uiState.update { it.copy(isLoading = false) }
                            Log.d("CameraX", "createReport: ${result.error}")
                        }

                    }
                }

                is Result.Success -> {
                    val preSignedUrl = result.data.preSignedUrl
                    val bitmap = _uiState.value.bitmap

                    Log.d("CameraX", "Bitmap: $bitmap")
                    Log.d("CameraX", "PreSigned URL: $preSignedUrl")
                    if (bitmap != null && preSignedUrl != null) {
                        Log.d("CameraX", "createReport: $preSignedUrl")
                        val uploadResult = uploadPhotoUseCase(preSignedUrl, bitmap)
                        _uiState.update { it.copy(photoFileName = null) }
                        if (uploadResult is Result.Failure) {
                            sendScreenEvent(ReportUiEvent.OnError("Error al subir la foto a S3", SnackBarType.Warning))
                            return@launch
                        }
                    }
                    sendScreenEvent(ReportUiEvent.OnBackWithSuccess(true))
                }
            }
            // Done loading
            _uiState.update { it.copy(isLoading = false, hideFloatingActionButton = false) }

        }
    }


    private fun sendScreenEvent(event: ReportUiEvent) {
        viewModelScope.launch {
            if (event is ReportUiEvent.OnError) {
                _uiState.update { it.copy(snackBarType = event.type) }
            }
            if (event is ReportUiEvent.OnSendingReport) {
                _uiState.update { it.copy(snackBarType = SnackBarType.Info) }
            }
            _eventChannel.send(event)
            if (event is ReportUiEvent.OnBackWithSuccess) {
                _uiState.update {
                    it.copy(
                        description = "",
                        title = "",
                        bitmap = null,
                        photoFileName = null,
                        isLoading = false
                    )
                }
            }
        }
    }

    suspend fun bindToCamera(
        applicationContext: Context,
        lifecycleOwner: LifecycleOwner,
        cameraSelector: CameraSelector
    ) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(applicationContext)

        processCameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, imageCapture, cameraPreviewUseCase
        )

        // Cancellation signals we're done with the camera
        try {
            awaitCancellation()
        } finally {
            processCameraProvider.unbindAll()
        }

    }


}