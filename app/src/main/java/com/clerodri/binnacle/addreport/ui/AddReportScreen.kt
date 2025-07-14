package com.clerodri.binnacle.addreport.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clerodri.binnacle.R
import com.clerodri.binnacle.authentication.presentation.components.componentShapes
import com.clerodri.binnacle.core.components.PrimaryButton
import com.clerodri.binnacle.core.components.SnackBarComponent
import com.clerodri.binnacle.core.components.SnackBarType
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.util.formatCurrentDateTime
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddReportScreen(
    viewModel: AddReportViewModel,
    modifier: Modifier = Modifier,
    onBack: (Boolean) -> Unit,
    roundId: Int,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (state.openCamera) {
        CameraScreen(
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            onCloseCamara = { viewModel.onReportEvent(AddReportEvent.OnCloseCamera) },
            addReportViewModel = viewModel
        )
    } else {
        Scaffold(modifier = modifier.fillMaxSize().imePadding(), snackbarHost = {
            SnackBarComponent(
                snackHostState,
                modifier = Modifier.padding(bottom = 0.dp),
                type = state.snackBarType ?: SnackBarType.Error
            )
        }, topBar = {
            AddReportTopAppBar(
                R.string.report_screen_name,
                onBack = {
                    viewModel.onReportEvent(AddReportEvent.ClearFields)
                    onBack(false)
                }, openCamera = {
                    if (cameraPermissionState.status.isGranted) {
                        viewModel.onReportEvent(AddReportEvent.OnOpenCamera)
                    } else {
                        viewModel.onReportEvent(AddReportEvent.NoCameraAllowed)
                    }

                }
            )
        }, floatingActionButton = {
            if (!state.hideFloatingActionButton) {
                PrimaryButton(
                    text = "Notificar",
                    onClick = {
                        viewModel.onReportEvent(
                            AddReportEvent.OnAddReport(state.title, state.description, roundId)
                        )
                        // onBack()
                    },
                    modifier = Modifier.padding(16.dp).navigationBarsPadding()
                )
            }
        }, floatingActionButtonPosition = FabPosition.Center,

        ) { paddingValues ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AddReportContent(
                    modifier = Modifier.fillMaxSize(),
                    title = state.title,
                    titleError = state.titleError,
                    description = state.description,
                    onTitleChanged = {
                        viewModel.onReportEvent(AddReportEvent.OnUpdateTitle(it))
                    },
                    onDescriptionChanged = {
                        viewModel.onReportEvent(AddReportEvent.OnUpdateDescription(it))
                    }
                )

                // Overlay loading spinner when isLoading is true
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .align(Alignment.Center)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.Center),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        viewModel.getEventChannel().collect { event ->
            when (event) {
                is ReportUiEvent.OnError -> {
                    coroutineScope.launch {
                        snackHostState.showSnackbar(
                            message = event.message, duration = SnackbarDuration.Short
                        )
                    }
                }

                is ReportUiEvent.OnBackWithSuccess -> {
                    onBack(event.isSuccess) // Back with success status
                }

                is ReportUiEvent.OnSendingReport -> {
                    coroutineScope.launch {
                        snackHostState.showSnackbar(
                            message = "Enviando....!", duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddReportContent(
    title: String,
    titleError: String?,
    description: String,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier

) {

    Column(
        modifier
            .fillMaxSize()
            .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.vertical_margin))
    ) {
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BackGroundAppColor.copy(0.3f),
            unfocusedBorderColor = Color.Gray.copy(0.3f),
            cursorColor = Color.Black
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 5.dp),
            text = formatCurrentDateTime(),
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 12.sp,
            textAlign = TextAlign.End
        )
        OutlinedTextField(
            value = title,
            modifier = Modifier
                .fillMaxWidth(),
            onValueChange = onTitleChanged,
            label = {
                Text(
                    text = stringResource(id = R.string.title_hint),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light)
                )
            },
            textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color.Black),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            maxLines = 1,
            colors = textFieldColors,
            isError = titleError != null
        )
        ErrorMessage(titleError)
        OutlinedTextField(
            value = description,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            onValueChange = onDescriptionChanged,
            label = {
                Text(
                    text = stringResource(id = R.string.description_hint),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Light)
                )
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold,color = Color.Black.copy(0.5f)),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            colors = textFieldColors,
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportTopAppBar(@StringRes title: Int, onBack: () -> Unit, openCamera: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(title)) }, navigationIcon = {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp),
            )
        }
    }, actions = {
        IconButton(onClick = { openCamera() }) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp),
                tint = BackGroundAppColor
            )
        }

    }, modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ErrorMessage(value: String?) {
    if (value != null) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 0.dp)
        )
    }
}