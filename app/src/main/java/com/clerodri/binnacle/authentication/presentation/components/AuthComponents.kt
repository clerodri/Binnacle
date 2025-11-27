package com.clerodri.binnacle.authentication.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.clerodri.binnacle.R
import com.clerodri.binnacle.ui.theme.BgColor
import com.clerodri.binnacle.ui.theme.Primary
import com.clerodri.binnacle.ui.theme.Secondary
import com.clerodri.binnacle.ui.theme.TextColor


@Composable
fun LogoApp(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.header3),
        contentDescription = "Header",
        modifier = modifier,
    )
}

@Composable
fun TitleApp(value: String, version: String) {
    Text(
        text = value,
        color = Color.White,
        fontWeight = FontWeight.ExtraBold,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 8.dp),
        textAlign = TextAlign.Center
    )
    Text(
        text = version,
        color = Color.White.copy(alpha = 0.7f),
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            fontStyle = FontStyle.Normal
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        textAlign = TextAlign.Center
    )
}


@Composable
fun TitleGuard() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.guard_title_label),
            color = Color.Black,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.height(48.dp))

    }
}

@Composable
fun TitleAdmin(comeBack: () -> Unit, composition: LottieComposition?) {
    TextButton(
        onClick = comeBack,
        modifier = Modifier.padding(bottom = 8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor =  Color(0xFF2973B2)
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Volver")
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {

            LottieAnimation(
                composition = composition,
                isPlaying = true,
                iterations = LottieConstants.IterateForever,
                speed = 1f,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


@Composable
fun CedulaFieldComponent(
    value: String,
    labelValue: String,
    painterResource: Painter,
    identifierError: String?,
    onIdentifierChange: (String) -> Unit
) {


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
        isError = identifierError != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        maxLines = 1,
        value = value,
        onValueChange = {
            onIdentifierChange(it)
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        }
    )
    ErrorMessage(identifierError)
}


@Composable
fun EmailTextComponent(
    value: String,
    emailError: String?,
    labelValue: String,
    painterResource: Painter,
    onEmailChange: (String) -> Unit
) {

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
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        isError = emailError != null,
        maxLines = 1,
        value = value,
        onValueChange = {
            onEmailChange(it)
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        }

    )
    ErrorMessage(emailError)
}

@Composable
fun PasswordFieldComponent(
    value: String,
    passwordError: String?,
    labelValue: String,
    painterResource: Painter,
    onPasswordChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }
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
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        keyboardActions = KeyboardActions {
            focusManager.clearFocus()
        },
        maxLines = 1,
        isError = passwordError != null,
        value = value,
        onValueChange = {
            onPasswordChange(it)
        },
        leadingIcon = {
            Icon(painter = painterResource, contentDescription = "")
        },
        trailingIcon = {
            val icon =
                if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description =
                if (isPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                    R.string.show_password
                )
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(imageVector = icon, contentDescription = description, tint = Color.Gray)
            }
        }
    )
    ErrorMessage(passwordError)
}


@Composable
fun ErrorMessage(value: String?) {
    if (value != null) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.error, // Use error color
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}


@Composable
fun ButtonComponent(value: String, loginEnable: Boolean, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp),
        contentPadding = PaddingValues(),
        enabled = loginEnable,
        colors = ButtonDefaults.buttonColors(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(48.dp)
                .background(
                    brush = if (loginEnable) Brush.horizontalGradient(listOf(Secondary, Primary))
                    else Brush.horizontalGradient(
                        listOf(
                            Color.Gray.copy(alpha = 0.3f),
                            Color.Gray.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(50.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DividerTextComponent() {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Text(
            text = stringResource(R.string.or), fontSize = 14.sp, color = TextColor,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}


@Composable
fun ClickableAdminTextComponent(modifier: Modifier, onClick: (String) -> Unit) {
    val initialText = stringResource(R.string.eres_administrador)
    val adminText = stringResource(R.string.login_admin_text)

    val annotatedString = buildAnnotatedString {
        append(initialText)
        withStyle(style = SpanStyle(color = Primary)) {
            pushStringAnnotation(tag = adminText, annotation = adminText)
            append(adminText)
        }

    }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(text = annotatedString,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { position ->
                    textLayoutResult?.let { layoutResult ->
                        val offset = layoutResult.getOffsetForPosition(position)
                        annotatedString
                            .getStringAnnotations(offset, offset)
                            .firstOrNull()
                            ?.let { span ->
                                if (span.item == adminText) {
                                    onClick(adminText)
                                }
                            }
                    }
                }
            }
            .padding(top = 8.dp),
        style = MaterialTheme.typography.bodyMedium,
        onTextLayout = { layoutResult ->
            textLayoutResult = layoutResult
        }
    )

}