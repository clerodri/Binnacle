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
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.clerodri.binnacle.addreport.data.datasource.network.dto.AddReportResponse
import com.clerodri.binnacle.addreport.data.work.ImageUploadWorker
import com.clerodri.binnacle.addreport.domain.AddReportUseCase
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.addreport.domain.TakePhotoUseCase
import com.clerodri.binnacle.addreport.presentation.ReportUiEvent.OnError
import com.clerodri.binnacle.addreport.presentation.ReportUiEvent.OnSendingReport
import com.clerodri.binnacle.core.DataError
import com.clerodri.binnacle.core.Result
import com.clerodri.binnacle.core.components.SnackBarType
import com.clerodri.binnacle.core.di.WorkManagerSerializer
import com.clerodri.binnacle.util.flip
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
import java.util.concurrent.TimeUnit
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
    val isFrontCamera: Boolean = false,
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val TAG = "AddReportViewModel"
        private const val REPORT_SUCCESS_DELAY = 2000L
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
            is AddReportEvent.OnOpenCamera -> openCamera(event.isFrontCamera)
            is AddReportEvent.OnCloseCamera -> closeCamera()
            is AddReportEvent.OnRemoveImage -> removeImage(event.filename)
            AddReportEvent.ClearFields -> clearFields()
            AddReportEvent.NoCameraAllowed -> sendError(
                "No tiene permisos para usar la CÁMARA",
                SnackBarType.Warning
            )

            is AddReportEvent.OnImagePreview -> previewImage(event.filename)
            AddReportEvent.OnSwitchCamera -> switchCamera()
        }
    }

    suspend fun bindToCamera(
        applicationContext: Context,
        lifecycleOwner: LifecycleOwner,
        cameraSelector: CameraSelector
    ) {
        try {
            val processCameraProvider = ProcessCameraProvider.awaitInstance(applicationContext)

            processCameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, imageCapture, cameraPreviewUseCase
            )
            Log.d("CameraVM", "✅ Camera bound successfully")
            // Cancellation signals we're done with the camera
            try {
                awaitCancellation()
            } finally {
                processCameraProvider.unbindAll()
            }
        } catch (e: Exception) {
            Log.e("CameraVM", "❌ Camera binding failed: ${e.message}", e)
        }
    }


    private fun submitReport(event: AddReportEvent.OnAddReport) {
        if (!hasInternetConnection(context)) {
            sendError("No hay conexión a internet", SnackBarType.Error)
            return
        }
        if (_uiState.value.title.isBlank()) {
            _uiState.update { it.copy(titleError = "El título es requerido") }
            return
        }
        createReport(event)
    }

    private fun createReport(event: AddReportEvent.OnAddReport) {
        viewModelScope.launch {
            setLoadingState(true)
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

                    delay(2000)
                    setLoadingState(false)
                    if (report.images.isNotEmpty()) {
                        scheduleImageUpload(result.data)
                        Log.d(TAG, "📋 Upload scheduled with WorkManager")
                    }
                    handleReportSuccess()
                }

                is Result.Failure -> {
                    setLoadingState(false)
                    handleReportError(result.error)
                }
            }
        }
    }

    private fun scheduleImageUpload(reportResponse: AddReportResponse) {
        try {

            val imagesJson = WorkManagerSerializer.serializeImages(_uiState.value.images, context)
            val urlsJson = WorkManagerSerializer.serializeSignedUrls(reportResponse.signedImages)

            Log.d(TAG, "📦 Serialized images: $imagesJson")

            // Create work request with backoff retry policy
            val uploadWork = OneTimeWorkRequestBuilder<ImageUploadWorker>()
                .setInputData(
                    workDataOf(
                        ImageUploadWorker.KEY_IMAGES_JSON to imagesJson,
                        ImageUploadWorker.KEY_SIGNED_URLS_JSON to urlsJson
                    )
                ).setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15,
                    TimeUnit.SECONDS
                )
                .build()

            // Enqueue work
            WorkManager.getInstance(context).enqueueUniqueWork(
                "image_upload_${System.currentTimeMillis()}",
                androidx.work.ExistingWorkPolicy.KEEP,
                uploadWork
            )

            Log.d(TAG, "✅ Upload work enqueued")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to schedule upload: ${e.message}", e)
        }
    }

    private fun handleReportSuccess() {
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

            var bitmap = photoUseCase(imageCapture)
            if (bitmap != null) {
                if (_uiState.value.isFrontCamera) {
                    bitmap = bitmap.flip(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
                }
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

    private fun openCamera(isFrontCamera: Boolean) {
        _uiState.update { it.copy(openCamera = true, isFrontCamera = isFrontCamera) }
    }

    private fun closeCamera() {
        _uiState.update { it.copy(openCamera = false) }
    }

    private fun switchCamera() {
        Log.d(TAG, "Switching camera")
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
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