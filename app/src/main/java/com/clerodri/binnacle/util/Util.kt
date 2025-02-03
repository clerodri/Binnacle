package com.clerodri.binnacle.util

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.auth.presentation.admin.AdminViewModel
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.Primary
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun IdentifierField(identifier:String, identifierError:String?, onIdentifierChange: (String) -> Unit){

    OutlinedTextField(value = identifier,
        onValueChange = { onIdentifierChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        placeholder = { Text(text = "Cedula", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        isError = identifierError != null,
        maxLines = 1,
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ))
    if (identifierError != null) {
        Text(
            text = identifierError,
            color = MaterialTheme.colorScheme.error, // Use error color
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@Composable
fun EmailField(email:String, onEmailChange: (String) -> Unit, emailError:String?){

    OutlinedTextField(value = email,
        onValueChange = { onEmailChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        placeholder = { Text(text = "email", color = Color.Gray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ))
    if (emailError != null) {
        Text(
            text = emailError,
            color = MaterialTheme.colorScheme.error, // Use error color
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}

@Composable
fun PasswordField(password:String, passwordError:String? , onPasswordChange: (String) -> Unit){
    var isPasswordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = { onPasswordChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .background(Color.White),
        placeholder = { Text(text = "password", color = Color.Gray) },
        isError = passwordError != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation() ,
        singleLine = true,
        maxLines = 1,
        trailingIcon = {
            val icon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
            val description = if (isPasswordVisible) "Hide password" else "Show password"
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(imageVector = icon, contentDescription = description, tint = Color.Gray)
            }
        },

        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
        ))
    if (passwordError != null) {
        Text(
            text = passwordError,
            color = MaterialTheme.colorScheme.error, // Use error color
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }
}
@Composable
fun Container(content: @Composable () -> Unit){
    Box(
        Modifier
            .fillMaxSize()
            .background(BackGroundAppColor))
    {
        content()
    }
}

//@Composable
//fun Header(modifier: Modifier) {
//    Box(modifier = modifier)
//    {
//        Column(Modifier.align(Alignment.Center)) {
////            NameApp()
//            HeaderImage(Modifier.padding(bottom = 36.dp).align(Alignment.CenterHorizontally))
//        }
//    }
//}

//@Composable
//fun TitleApp(value:String, version:String) {
//    Text(
//        text = value,
//        color = Color.White,
//        fontWeight = FontWeight.ExtraBold,
//        fontSize = 24.sp
//    )
//    Text(
//        text = version,
//        color = Color.White.copy(alpha = 0.7f),
//        fontSize = 12.sp
//    )
//}

@Composable
fun ButtonLogin(loginEnable:Boolean, onLoginSelected:() -> Unit) {
    Button (
        onClick = {
            onLoginSelected()
        },
        modifier = Modifier
            .padding(30.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1BBC65),
            disabledContainerColor = Color(0xFF79E9AD),
            contentColor = Color.White,
            disabledContentColor = Color.White
        ),
        enabled = loginEnable,
        shape = RoundedCornerShape(4.dp)
    )   { Text("Iniciar Sesion") }
}


@Composable
fun CheckBoxComponent(value: String){
    Row(modifier = Modifier.fillMaxWidth()
        .heightIn(56.dp)
        .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val checkedState = remember { mutableStateOf(false) }
        Checkbox(checked = checkedState.value,
            onCheckedChange = {
                checkedState.value  != checkedState.value
            }
        )
        Text( text = "Recordar Contrasena?",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 20.dp),
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = Color.Black,
            textAlign = TextAlign.Center)
//        NormalTextComponent(value = value)

    }
}


@Composable
fun SettingIcon(modifier: Modifier, adminViewModel: AdminViewModel) {
    var isExpanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(
            onClick = { isExpanded = true },
            modifier = modifier
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Settings",
                tint = Color.White
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    isExpanded = false
//                    authViewModel.onAction(GuardScreenEvent.GoToLoginAdmin)
                },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Admin login",
                            tint = BackGroundAppColor,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "ADMIN",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            )


        }

    }


}


/*
    //                Spacer(modifier = Modifier.height(16.dp))
    // GoogleMapComponent()
//                    Text(text = "Ruta LATITUDE ${viewModel.currentLocation?.latitude}")
//                    Text(text = "Ruta LONGITUDE ${viewModel.currentLocation?.longitude}")
//                    viewModel.currentLocation?.let { GoogleMapComponent(location = it) }


//            LazyColumn(
//                modifier = Modifier.fillMaxSize(),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                contentPadding = PaddingValues(top = contentPadding.calculateTopPadding()),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//            ) {
//                item {
//                    HomeTimerComponent(modifier = Modifier.fillMaxWidth())
//                    HomeDividerTextComponent()
//                }
//                items(10){
//                    Box(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                            .heightIn(100.dp)
//                            .fillMaxWidth()
//                            .clip(RoundedCornerShape(20.dp))
//                            .background(Primary)
//                    )
//
//                    {
//                        Spacer(modifier = Modifier.height(16.dp))
//                    }
//                }
//
//            }
*/



@Composable
fun RouteItemComponent(modifier: Modifier = Modifier, value: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 50.dp)
            .heightIn(100.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Primary)
    )

    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Text(
                text = value,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 26.sp,
                fontStyle = FontStyle.Italic,
                color = Color.LightGray,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

    }

}


@Composable
fun GoogleMapComponent(modifier: Modifier = Modifier, location: Location) {
    val singapore = LatLng(location.latitude, location.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 16f)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = singapore),
            title = "Laguna dorada",
            snippet = "Marker in Laguna dorada"
        )
    }
}
