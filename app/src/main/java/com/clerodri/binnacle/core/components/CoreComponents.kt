package com.clerodri.binnacle.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.auth.presentation.components.componentShapes
import com.clerodri.binnacle.ui.theme.BgColor
import com.clerodri.binnacle.ui.theme.Primary


@Composable
fun NormalTextField(labelValue: String, painterResource: Painter) {
    var textValue by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(componentShapes.small),
        label = { Text(text = labelValue) },
        colors = TextFieldDefaults.colors(
            focusedLabelColor = Primary,
            cursorColor = Color.Gray,
            focusedIndicatorColor = Primary,
            focusedContainerColor = BgColor,
            unfocusedContainerColor = BgColor
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true,

        maxLines = 1,
        value = textValue,
        onValueChange = {
            textValue = it
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        }
    )
}

@Composable
fun BigSpinner(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun HeadingTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(),
        style = TextStyle(
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Normal
        ),
        color = Color.Black,
        textAlign = TextAlign.Center
    )
}


@Composable
fun NormalTextComponent(value: String) {
    Text(
        text = value,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        color = Color.Black,
        textAlign = TextAlign.Center
    )
}