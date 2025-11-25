package com.clerodri.binnacle.addreport.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.addreport.presentation.AddReportUiState
import com.clerodri.binnacle.addreport.presentation.ImageUiState
import kotlinx.coroutines.launch

/**
 * Author: Ronaldo R.
 * Date:  11/24/2025
 * Description:
 **/

@Composable
fun ImagesHorizontalScroll(
    images: List<ImageUiState>,
    onRemoveImage: (String) -> Unit = {},
    onPreviewImage: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) {
        Text(
            text = "No hay imágenes capturadas",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(16.dp)
        )
        return
    }

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val canScrollLeft = remember(scrollState) {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0
        }
    }

    val canScrollRight = remember(scrollState) {
        derivedStateOf {
            scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size < images.size
        }
    }

    val needsScrolling = remember(images.size) {
        derivedStateOf {
            images.size > 3
        }
    }


    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = "Imágenes capturadas (${images.size}/${AddReportUiState.MAX_IMAGES})",
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (needsScrolling.value && canScrollLeft.value) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            // Scroll left by 200 pixels
                            scrollState.animateScrollToItem(
                                maxOf(0, scrollState.firstVisibleItemIndex - 1)
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.3f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Scroll left",
                        tint = Color.Black
                    )
                }
            } else if (needsScrolling.value) {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.1f),  // Very faint
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }


            LazyRow(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),  // ✅ Padding between items
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 8.dp
                )
            ) {
                items(images.size) { index ->
                    val imageState = images[index]

                    if (imageState.bitmap != null) {
                        ImageGridItem(
                            bitmap = imageState.bitmap,
                            filename = imageState.filename,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            onRemoveClick = {
                                onRemoveImage(imageState.filename)
                            },
                            onPreviewClick = {
                                onPreviewImage(it)
                            }
                        )
                    }
                }
            }

            if (needsScrolling.value && canScrollRight.value) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            // Scroll right by 200 pixels
                            scrollState.animateScrollToItem(
                                minOf(
                                    images.size - 1,
                                    scrollState.firstVisibleItemIndex + 1
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.3f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Scroll right",
                        tint = Color.Black
                    )
                }
            } else if (needsScrolling.value) {
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.1f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}