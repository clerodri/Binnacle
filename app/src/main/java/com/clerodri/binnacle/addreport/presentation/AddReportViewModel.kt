package com.clerodri.binnacle.addreport.presentation

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
import com.clerodri.binnacle.addreport.data.datasource.network.dto.AddReportResponse
import com.clerodri.binnacle.addreport.domain.AddReportUseCase
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.TakePhotoUseCase
import com.clerodri.binnacle.addreport.domain.UploadPhotoUseCase
import com.clerodri.binnacle.addreport.presentation.ReportUiEvent.OnError
import com.clerodri.binnacle.addreport.presentation.ReportUiEvent.OnSendingReport
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

data class ImageUiState(
    val filename: String,
    val bitmap: Bitmap? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f
)

data class AddReportUiState(
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val openCamera: Boolean = false,
    val btnCameraEnable: Boolean = false,
    val titleError: String? = null,
    val snackBarType: SnackBarType? = null,
    val hideFloatingActionButton: Boolean = false,
    val images: List<ImageUiState> = emptyList(),
) {
    // Computed properties para simplificar la lógica
    val hasImages: Boolean get() = images.isNotEmpty()
    val isValid: Boolean get() = title.isNotBlank() && hasImages
    val canAddMoreImages: Boolean get() = images.size < MAX_IMAGES

    companion object {
        const val MAX_IMAGES = 5
    }
}

@HiltViewModel
class AddReportViewModel @Inject constructor(
    private val addReportUseCase: AddReportUseCase,
    private val photoUseCase: TakePhotoUseCase,
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "AddReportViewModel"
        private const val REPORT_SUCCESS_DELAY = 4000L
    }

    private val _uiState = MutableStateFlow(AddReportUiState())
    val state = _uiState.asStateFlow()

    // Camera Setup.
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

    //Public methods

    fun onReportEvent(event: AddReportEvent) {

        when (event) {
            is AddReportEvent.OnAddReport -> submitReport(event)
            is AddReportEvent.OnUpdateTitle -> updateTitle(event.title)
            is AddReportEvent.OnUpdateDescription -> updateDescription(event.description)
            is AddReportEvent.OnTakePhoto -> capturePhoto()
            is AddReportEvent.OnOpenCamera -> openCamera()
            is AddReportEvent.OnCloseCamera -> closeCamera()
            is AddReportEvent.OnRemoveImage -> removeImage(event.filename)
            AddReportEvent.ClearFields -> clearFields()
            AddReportEvent.NoCameraAllowed -> sendError(
                "No tiene permisos para usar la CÁMARA",
                SnackBarType.Warning
            )

            is AddReportEvent.OnImagePreview -> previewImage(event.filename)
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


    private fun submitReport(event: AddReportEvent.OnAddReport) {
        // Validar conexión
        if (!hasInternetConnection(context)) {
            sendError("No hay conexión a internet", SnackBarType.Error)
            return
        }

        // Validar título
        if (_uiState.value.title.isBlank()) {
            _uiState.update { it.copy(titleError = "El título es requerido") }
            return
        }

        // Validar imágenes
//        if (!_uiState.value.hasImages) {
//            sendError("Debes capturar al menos una imagen", SnackBarType.Warning)
//            return
//        }

        sendScreenEvent(ReportUiEvent.OnSendingReport)
        createReport(event)
    }

    private fun createReport(event: AddReportEvent.OnAddReport) {
        viewModelScope.launch {
            setLoadingState(true)

            // Obtener nombres de archivos
            val imageFilenames = _uiState.value.images.map { it.filename }

            val report = Report(
                title = event.title,
                description = event.detail,
                roundId = event.roundId,
                images = imageFilenames,
                imageType = "image/jpeg"
            )

            when (val result = addReportUseCase(report)) {
                is Result.Success -> {
                    val signedUrls = result.data.signedImages
                    Log.d(TAG, "Reporte creado: $signedUrls")
                    if( report.images.isNotEmpty()) uploadAllImagesToS3(result.data)
                    handleReportSuccess()
                }

                is Result.Failure -> {
                    handleReportError(result.error)
                }
            }

            setLoadingState(false)
        }
    }

    private suspend fun uploadAllImagesToS3(reportResponse: AddReportResponse) {
        val totalImages = _uiState.value.images.size
        var uploadedCount = 0
        _uiState.value.images.forEach { imageState ->

            val presignedUrl = reportResponse.getSignedUrlFor(imageState.filename)
            Log.d(TAG, "presignedUrl: $presignedUrl")
            when {
                presignedUrl == null -> {
                    Log.w(TAG, "URL no disponible para: ${imageState.filename}")
                }

                imageState.bitmap == null -> {
                    Log.w(TAG, "Bitmap no disponible para: ${imageState.filename}")
                }

                else -> {
                    when (val result = uploadPhotoUseCase(presignedUrl, imageState.bitmap)) {
                        is Result.Success -> {
                            uploadedCount++
                            Log.d(
                                TAG,
                                "Imagen uploadada: ${imageState.filename} ($uploadedCount/$totalImages)"
                            )
                        }

                        is Result.Failure -> {
                            Log.e(
                                TAG,
                                "Error uploadAllImagesToS3 ${imageState.filename}: ${result.error}"
                            )
                            handleReportError(result.error)
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleReportSuccess() {
        delay(REPORT_SUCCESS_DELAY)
        sendScreenEvent(ReportUiEvent.OnBackWithSuccess(true))
        clearFields()
    }

    // --- Photo Management ---

    private fun capturePhoto() {
        if (!_uiState.value.canAddMoreImages) {
            sendError(
                "Máximo ${AddReportUiState.MAX_IMAGES} imágenes permitidas",
                SnackBarType.Warning
            )
            return
        }

        viewModelScope.launch {
            setLoadingState(true)

            val bitmap = photoUseCase(imageCapture)
            if (bitmap != null) {
                addImageToState(bitmap)
            } else {
                sendError("Error al capturar la foto", SnackBarType.Error)
            }

            closeCamera()
            setLoadingState(false)
        }
    }

    private fun addImageToState(bitmap: Bitmap) {
        val filename = generateImageFilename()
        val imageUiState = ImageUiState(
            filename = filename,
            bitmap = bitmap
        )
        _uiState.update { currentState ->
            currentState.copy(
                images = currentState.images + imageUiState
            )
        }
        Log.d(TAG, "Imagen agregada: $filename (Total: ${_uiState.value.images.size})")
    }

    private fun removeImage(filename: String) {
        _uiState.update { currentState ->
            currentState.copy(
                images = currentState.images.filterNot { it.filename == filename }
            )
        }
        Log.d(TAG, "Imagen removida: $filename")
    }

    private fun previewImage(filename: String) {
        Log.d(TAG, "Preview image: $filename")
        // Find the image in state and emit event
        val imageState = _uiState.value.images.find { it.filename == filename }
        if (imageState != null) {
            sendScreenEvent(ReportUiEvent.OnShowImagePreview(filename))
        }
    }

    private fun generateImageFilename(): String {
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return "photo_${formatter.format(Date())}.jpg"
    }

    // --- UI State Management ---
    private fun updateTitle(title: String) {
        _uiState.update {
            it.copy(title = title, titleError = null)
        }
    }

    private fun updateDescription(description: String) {
        _uiState.update {
            it.copy(description = description)
        }
    }

    private fun openCamera() {
        _uiState.update { it.copy(openCamera = true) }
    }

    private fun closeCamera() {
        _uiState.update { it.copy(openCamera = false) }
    }

    private fun clearFields() {
        _uiState.update {
            AddReportUiState()
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = isLoading,
                hideFloatingActionButton = isLoading
            )
        }
    }

    // --- Event Handling ---
    private fun sendScreenEvent(event: ReportUiEvent) {
        viewModelScope.launch {
            when (event) {
                is OnError -> {
                    _uiState.update { it.copy(snackBarType = event.type) }
                }

                is OnSendingReport -> {
                    _uiState.update { it.copy(snackBarType = SnackBarType.Info) }
                }

                else -> Unit
            }
            _eventChannel.send(event)
        }
    }


    private fun handleReportError(error: DataError.Report) {
        Log.d(TAG, "handleReportError: $error")
        val message = when (error) {
            DataError.Report.REQUEST_TIMEOUT -> "Servidor no responde"
            DataError.Report.NO_INTERNET -> "No hay internet"
            DataError.Report.SERVICE_UNAVAILABLE -> "Servidor no disponible"
            DataError.Report.S3_DISABLE -> "Servicio No Disponible"
            DataError.Report.S3_INVALID_CREDENTIALS -> "Servicio No Disponible"
            DataError.Report.BUCKET_NOT_EXISTS -> "Servicio No Disponible"
            DataError.Report.S3_UNKNOWN_ERROR -> "Servicio No Disponible"
            DataError.Report.PHOTO_UPLOAD_FAILED -> "Servicio No Disponible"
            DataError.Report.S3_REQUEST_FAILED -> "Servicio No Disponible"
        }
        sendError(message, SnackBarType.Error)
    }


    private fun sendError(message: String, type: SnackBarType) {
        sendScreenEvent(OnError(message, type))
    }


}