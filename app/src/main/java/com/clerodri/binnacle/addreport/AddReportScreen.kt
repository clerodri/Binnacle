package com.clerodri.binnacle.addreport

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.auth.presentation.components.componentShapes
import com.clerodri.binnacle.ui.theme.BackGroundAppColor


@Composable
fun AddReportScreen(
    addReportViewModel: AddReportViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val state by addReportViewModel.state.collectAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
//        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AddReportTopAppBar(
                R.string.report_screen_name,
                onBack = {
                    onBack()
                    addReportViewModel.clearFields()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onBack() },
                modifier = Modifier.padding(bottom = 50.dp, end = 8.dp)
            ) {
                Icon(Icons.Filled.Done, stringResource(id = R.string.save_report))
            }
        }

    ) { paddingValues ->
        AddReportContent(
            modifier = Modifier.padding(paddingValues),
            loading = state.isLoading,
            title = state.title,
            description = state.description,
            onTitleChanged = { addReportViewModel.updateTitle(it) },
            onDescriptionChanged = { addReportViewModel.updateDescription(it) }
        )
    }
}

@Composable
private fun AddReportContent(
    loading: Boolean,
    title: String,
    description: String,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier

) {
    if (loading) {
//        PullToRefreshBox(
//            isRefreshing = isRefreshing,
//            state = refreshingState,
//            onRefresh = { /* DO NOTHING */ },
//            content = { }
//        )
    } else {
        Column(
            modifier
                .fillMaxWidth()
                .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.vertical_margin))
        ) {
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BackGroundAppColor.copy(0.3f),
                unfocusedBorderColor = Color.Gray.copy(0.3f),
                cursorColor = MaterialTheme.colorScheme.onSecondary
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
                textStyle = MaterialTheme.typography.headlineSmall
                    .copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                colors = textFieldColors
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChanged,
                placeholder = { Text(stringResource(id = R.string.description_hint)) },
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth()
                    .clip(componentShapes.small),
                colors = textFieldColors
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportTopAppBar(@StringRes title: Int, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(title)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, stringResource(id = R.string.menu_back),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp),
                )
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(40.dp),
                    tint = BackGroundAppColor
                )
            }

        },
        modifier = Modifier.fillMaxWidth()
    )
}