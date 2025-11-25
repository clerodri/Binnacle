package com.clerodri.binnacle.addreport.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.clerodri.binnacle.addreport.presentation.ImageUiState

/**
 * Author: Ronaldo R.
 * Date:  11/24/2025
 * Description:
 **/
@Composable
fun ImagePreviewScreen(
    images: List<ImageUiState>,
    initialFilename: String,
    onBack: () -> Unit,
    onDeleteImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val initialIndex = images.indexOfFirst { it.filename == initialFilename }
    var currentIndex by remember { mutableIntStateOf(maxOf(0, initialIndex)) }

    val currentImage = if (currentIndex in images.indices) images[currentIndex] else return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 50.dp, start = 8.dp, end = 8.dp)
    ) {

        if (currentImage.bitmap != null) {
            AsyncImage(
                model = currentImage.bitmap,
                contentDescription = "Full preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK BUTTON
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // DELETE BUTTON
            IconButton(
                onClick = {
                    onDeleteImage(currentImage.filename)
                    onBack()
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.Red.copy(alpha = 0.6f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.DeleteForever,
                    contentDescription = "Delete image",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }


        Text(
            text = "${currentIndex + 1} / ${images.size}",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            textAlign = TextAlign.Center
        )


        if (currentIndex > 0) {
            IconButton(
                onClick = { currentIndex-- },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }


        if (currentIndex < images.size - 1) {
            IconButton(
                onClick = { currentIndex++ },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .background(
                        Color.White.copy(alpha = 0.3f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}