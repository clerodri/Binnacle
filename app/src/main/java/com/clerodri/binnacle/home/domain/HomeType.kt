package com.clerodri.binnacle.home.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector
import com.clerodri.binnacle.R

enum class HomeType(
    val title: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    Home(title = R.string.rondas_label,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    Check(title = R.string.checkin_label,
        selectedIcon = Icons.Filled.VerifiedUser,
        unselectedIcon = Icons.Outlined.VerifiedUser
    ),
    Report(title = R.string.report_label,
        selectedIcon = Icons.Filled.BookmarkAdded,
        unselectedIcon = Icons.Outlined.BookmarkAdded
        )
}