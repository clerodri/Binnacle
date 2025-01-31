package com.clerodri.binnacle.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.R
import com.clerodri.binnacle.core.components.NormalTextField
import com.clerodri.binnacle.ui.theme.TextColor


@Composable
fun HomeTitleComponent(modifier: Modifier = Modifier ,painter: Painter, value:String) {



}


@Composable
fun HomeTimerComponent(modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Icon(modifier = Modifier
            .padding(8.dp)
            .size(40.dp),
            painter = painterResource(id = R.drawable.ic_timer),
            contentDescription = null)
        Text(
            text = "00:00:00",
            modifier = Modifier
                .heightIn()
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal
            ),
            color = Color.Black,
            textAlign = TextAlign.Start
        )
    }

}

@Composable
fun HomeDividerTextComponent() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
            text = stringResource(R.string.rondas), fontSize = 14.sp, color = TextColor,
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




//@Preview
//@Composable
//fun HomeTitleComponentPreview(){
////    HomeTitleComponent(Modifier.fillMaxWidth(),
////        painter = painterResource(id = R.drawable.ic_user)
////    )
//
//}

//@Composable
//fun HeadingTextComponent(value: String) {
//    Text(
//        text = value,
//        modifier = Modifier.heightIn(),
//        style = TextStyle(
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold,
//            fontStyle = FontStyle.Normal
//        ),
//        color = Color.Black,
//        textAlign = TextAlign.Center
//    )
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBarComponent(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,

    ) {
    TopAppBar(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(100.dp)),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        ),
        title = {
//            Text(
//                "Search your notes",
//                color = TextColor,
//                fontSize = 17.sp
//            )
//            HomeTitleComponent(Modifier.fillMaxWidth(),
//                painter = painterResource(id = R.drawable.ic_user)
//            )
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp, end = 8.dp)
                    .size(27.dp),
            )
        },

        windowInsets = WindowInsets(top = 0.dp)
    )
}

