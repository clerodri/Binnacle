package com.clerodri.binnacle.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.core.components.HeadingTextComponent

@Composable
fun LoginTestScreen(){
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp),

    )    {
       Column(modifier = Modifier.fillMaxSize()) {
//           NormalTextComponent( value = stringResource(id = R.string.title_guard))
           HeadingTextComponent(value = stringResource(id = R.string.app_name))
           Spacer(modifier = Modifier.height(20.dp))
//           CedulaFieldComponent(labelValue = stringResource(id = R.string.cedula),
//               painterResource(id = R.drawable.ic_cedula))
//           EmailTextComponent(
//               labelValue = stringResource(id = R.string.email),
//               painterResource = painterResource(id = R.drawable.ic_email)
//           )
//           PasswordFieldComponent(
//               labelValue = stringResource(id = R.string.password),
//               painterResource = painterResource(id = R.drawable.ic_password)
//           )
//           Spacer(modifier = Modifier.height(80.dp))
//           ButtonComponent(value = stringResource(id = R.string.login))
//           CheckBoxComponent(value = stringResource(R.string.remember_password))
       }
    }
}


@Preview
@Composable
fun  LoginTestScreenTPreview(){
    LoginTestScreen()

}