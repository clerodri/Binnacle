package com.clerodri.binnacle.home.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.clerodri.binnacle.R

/**
 * Author: Ronaldo R.
 * Date:  11/26/2025
 * Description: Use this componet when u have 4 options menu
 **/

@Composable
fun CustomBottomBar(
) {

    val items = listOf(
        R.drawable.ic_account,
        R.drawable.ic_transfer,
        R.drawable.ic_payment,
        R.drawable.ic_explore

    )

    val labels = listOf(
        "Account",
        "Transfer",
        "Payment",
        "Explore"
    )

    // Use rememberSaveable to retain state across configuration changes
    var selectedIndex = rememberSaveable { mutableIntStateOf(0) }

    // Create RioBottomNavItemData for the bottom navigation buttons
    val buttons = items.mapIndexed { index, icon ->
        RioBottomNavItemData(
            imageVector = ImageVector.vectorResource(icon),
            selected = index == selectedIndex.intValue,
            onClick = { selectedIndex.intValue = index },
            label = labels[index]
        )
    }
    NavigationBar(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .shadow(
                elevation = 20.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

            ), containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
    {
        BottomNavigationBar(buttons = buttons)

    }
}

@Composable
fun BottomNavigationBar(buttons: List<RioBottomNavItemData>) {
    RioBottomNavigation(
        fabIcon = ImageVector.vectorResource(id = R.drawable.ic_qr)  ,
        buttons = buttons,
        fabSize = 70.dp,
        barHeight = 70.dp,
        selectedItemColor = Color.Gray,
        fabBackgroundColor = Color.Blue,
    )
}