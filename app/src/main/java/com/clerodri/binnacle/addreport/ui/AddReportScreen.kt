package com.clerodri.binnacle.addreport.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.clerodri.binnacle.addreport.domain.Report
import com.clerodri.binnacle.authentication.presentation.components.componentShapes
import com.clerodri.binnacle.core.components.SnackBarComponent
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.util.formatCurrentDateTime
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddReportScreen(
    addReportViewModel: AddReportViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    routeId: Int,
    roundId: Int,
    localityId: Int
) {
    val state by addReportViewModel.state.collectAsStateWithLifecycle()
    val snackHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (state.openCamera) {
        CameraScreen(
            Modifier
                .fillMaxSize()
                .background(Color.Black),
            onCloseCamara = { addReportViewModel.onReportEvent(AddReportEvent.OnCloseCamera) },
            addReportViewModel = addReportViewModel
        )
    } else {
        Scaffold(modifier = modifier.fillMaxSize(), snackbarHost = {
            SnackBarComponent(
                snackHostState, modifier = Modifier.padding(bottom = 0.dp)
            )
        }, topBar = {
            AddReportTopAppBar(R.string.report_screen_name,
                onBack = {
                    addReportViewModel.onReportEvent(AddReportEvent.OnNavigateToHome)
                    onBack()
                }, openCamera = {
                    if (cameraPermissionState.status.isGranted) {
                        addReportViewModel.onReportEvent(AddReportEvent.OnOpenCamera)
                    } else {
                        addReportViewModel.onReportEvent(AddReportEvent.NoCameraAllowed)
                    }

                }
            )
        }, floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 200.dp, end = 10.dp),
                onClick = {
                    if (state.title.isNotBlank() && state.description.isNotBlank()) {
                        addReportViewModel.onReportEvent(
                            AddReportEvent.OnAddReport(
                                Report(
                                    state.title, state.description, routeId, roundId, localityId,
                                    state.bitmap
                                )
                            )
                        )
                    }
                }
            ) {
                Icon(Icons.Filled.Done, stringResource(id = R.string.save_report))
            }
        }

        ) { paddingValues ->

            AddReportContent(modifier = Modifier.padding(paddingValues),
                title = state.title,
                description = state.description,
                onTitleChanged = { addReportViewModel.onReportEvent(AddReportEvent.OnUpdateTitle(it)) },
                onDescriptionChanged = {
                    addReportViewModel.onReportEvent(
                        AddReportEvent.OnUpdateDescription(
                            it
                        )
                    )
                })


        }
    }


    LaunchedEffect(Unit) {
        addReportViewModel.getEventChannel().collect { event ->
            when (event) {

                ReportUiEvent.onBack -> {
                    onBack()
                }

                is ReportUiEvent.onError -> {

                    coroutineScope.launch {
                        snackHostState.showSnackbar(
                            message = event.message, duration = SnackbarDuration.Short
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
            cursorColor = MaterialTheme.colorScheme.onSecondary
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
                .fillMaxWidth()
                .clip(componentShapes.small),
            onValueChange = onTitleChanged,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.title_hint),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            maxLines = 1,
            colors = textFieldColors
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            placeholder = { Text(stringResource(id = R.string.description_hint)) },
            modifier = Modifier
                .fillMaxSize()
                .height(200.dp)
                .clip(componentShapes.small),
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportTopAppBar(@StringRes title: Int, onBack: () -> Unit, openCamera: () -> Unit) {
    TopAppBar(title = { Text(text = stringResource(title)) }, navigationIcon = {
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