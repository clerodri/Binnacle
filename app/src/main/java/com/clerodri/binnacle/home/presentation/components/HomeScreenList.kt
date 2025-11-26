package com.clerodri.binnacle.home.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clerodri.binnacle.home.domain.model.Route
import com.clerodri.binnacle.home.presentation.HomeUiState
import com.clerodri.binnacle.ui.theme.BackGroundAppColor
import com.clerodri.binnacle.ui.theme.Primary
import kotlinx.coroutines.launch

/**
 * ✅ Refactored HomeScreenList with:
 * - Fixed navigation arrows
 * - Clean section-based design
 * - Progress indicator
 * - Better visual hierarchy
 */
@Composable
fun HomeScreenList(
    state: HomeUiState,
    routes: List<Route>,
    updateIndex: () -> Unit,
) {
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    if (routes.isEmpty()) return

    LaunchedEffect(state.currentIndex) {
        coroutineScope.launch {
            scrollState.animateScrollToItem(state.currentIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        ProgressHeader(
            currentIndex = state.currentIndex,
            totalRoutes = routes.size,
            isStarted = state.isStarted
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            NavigationArrow(
                isEnabled = state.currentIndex > 0,
                isLeft = true,
                onClick = {
                    coroutineScope.launch {
                        scrollState.animateScrollToItem(
                            (state.currentIndex - 1).coerceAtLeast(0)
                        )
                    }
                }
            )

            LazyRow(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 180.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(routes.size) { index ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500, easing = LinearEasing))
                                + slideInVertically(),
                        exit = fadeOut(animationSpec = tween(500, easing = FastOutSlowInEasing))
                                + slideOutVertically(),
                    ) {
                        RouteCard(
                            index = index,
                            item = routes[index],
                            isActive = index == state.currentIndex && state.isStarted,
                            isCompleted = index < state.currentIndex,
                            isLastItem = index == routes.size - 1,
                        )
                    }
                }
            }


            NavigationArrow(
                isEnabled = state.currentIndex < routes.size - 1 && state.isStarted,
                isLeft = false,
                onClick = { updateIndex() }
            )
        }
    }
}

// ✅ Progress Header Component
@Composable
fun ProgressHeader(
    currentIndex: Int,
    totalRoutes: Int,
    isStarted: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SECCIONES DE LA URBANIZACIÓN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Sección ${currentIndex + 1} de $totalRoutes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStarted) BackGroundAppColor else Color.Gray
            )

            // Progress bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color.LightGray, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = ((currentIndex + 1).toFloat() / totalRoutes))
                        .height(4.dp)
                        .background(BackGroundAppColor, RoundedCornerShape(2.dp))
                )
            }
        }
    }
}


@Composable
fun NavigationArrow(
    isEnabled: Boolean,
    isLeft: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (isEnabled) BackGroundAppColor else Color.Gray.copy(0.2f),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Icon(
            imageVector = if (isLeft) Icons.Filled.ChevronLeft else Icons.Filled.ChevronRight,
            contentDescription = if (isLeft) "Previous Section" else "Next Section",
            tint = if (isEnabled) Color.White else Color.Gray,
            modifier = Modifier.size(28.dp)
        )
    }
}


@Composable
fun RouteCard(
    index: Int,
    item: Route,
    isActive: Boolean,
    isCompleted: Boolean,
    isLastItem: Boolean,
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .heightIn(min = 180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = getCardBackgroundColor(isActive, isCompleted)
        ),
        border = getCardBorder(isActive, isCompleted)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            StatusBadge(isActive, isCompleted)
            Spacer(modifier = Modifier.height(12.dp))
            SectionNumber(index, isActive)
            Spacer(modifier = Modifier.height(12.dp))
            SectionName(item.name, isActive, isCompleted)
            Spacer(modifier = Modifier.height(0.dp))
            StatusIcon(isActive, isCompleted, isLastItem)
        }
    }
}

@Composable
private fun getCardBackgroundColor(isActive: Boolean, isCompleted: Boolean): Color {
    return when {
        isActive -> BackGroundAppColor.copy(0.15f)
        isCompleted -> Primary.copy(0.1f)
        else -> Color.White
    }
}


private fun getCardBorder(
    isActive: Boolean,
    isCompleted: Boolean
): androidx.compose.foundation.BorderStroke? {
    if (!isActive && !isCompleted) return androidx.compose.foundation.BorderStroke(
        1.dp,
        Color.LightGray
    )

    return androidx.compose.foundation.BorderStroke(
        2.dp,
        if (isActive) BackGroundAppColor else Primary
    )
}

@Composable
private fun StatusBadge(isActive: Boolean, isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .background(
                color = getBadgeBackgroundColor(isActive, isCompleted),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = getBadgeText(isActive, isCompleted),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = getBadgeTextColor(isActive, isCompleted)
        )
    }
}

private fun getBadgeBackgroundColor(isActive: Boolean, isCompleted: Boolean): Color {
    return when {
        isCompleted -> Primary.copy(0.3f)
        isActive -> BackGroundAppColor.copy(0.3f)
        else -> Color.Transparent
    }
}

private fun getBadgeText(isActive: Boolean, isCompleted: Boolean): String {
    return when {
        isCompleted -> "✓ Completado"
        isActive -> "● Activo"
        else -> "○ Pendiente"
    }
}

private fun getBadgeTextColor(isActive: Boolean, isCompleted: Boolean): Color {
    return when {
        isCompleted -> Primary
        isActive -> BackGroundAppColor
        else -> Color.Gray
    }
}


@Composable
private fun SectionNumber(index: Int, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(
                color = if (isActive) BackGroundAppColor else Primary.copy(0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${index + 1}",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color.White else Primary
        )
    }
}

@Composable
private fun SectionName(name: String, isActive: Boolean, isCompleted: Boolean) {
    Text(
        text = name,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isActive || isCompleted) Color.Black else Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Composable
private fun StatusIcon(isActive: Boolean, isCompleted: Boolean, isLastItem: Boolean) {
    when {
        isCompleted -> {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Completed",
                tint = Primary,
                modifier = Modifier.size(32.dp)
            )
        }

        isLastItem && isActive -> {
            Text(
                text = "Última Sección",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BackGroundAppColor
            )
        }
    }
}