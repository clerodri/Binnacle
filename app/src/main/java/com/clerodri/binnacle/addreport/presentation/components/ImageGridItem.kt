package com.clerodri.binnacle.addreport.presentation.components


import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.clerodri.binnacle.ui.theme.BackGroundAppColor

/**
 * Author: Ronaldo R.
 * Date:  11/24/2025
 * Description:
 **/
@Composable
fun ImageGridItem(
    bitmap: Bitmap,
    filename: String,
    modifier: Modifier = Modifier,
    onRemoveClick: () -> Unit = {},
    onPreviewClick: (String) -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Gray.copy(alpha = 0.2f))
            .shadow(16.dp)
    ) {

        AsyncImage(
            model = bitmap,
            contentDescription = "Captured photo",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = { onPreviewClick(filename) },
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.TopCenter)
                .background(
                    Color.Transparent,
                    shape = CircleShape
                )
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = "Preview image",
                tint = BackGroundAppColor,
                modifier = Modifier.size(28.dp)
            )
        }

//        IconButton(
//            onClick = onRemoveClick,
//            modifier = Modifier
//                .size(24.dp)
//                .align(Alignment.TopEnd)
//                .background(Color.Black.copy(alpha = 0.6f), shape = CircleShape)
//                .padding(2.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Filled.Close,
//                contentDescription = "Remove image",
//                tint = Color.White,
//                modifier = Modifier
//                    .size(24.dp)
//                    .padding(2.dp)
//            )
//        }
    }
}