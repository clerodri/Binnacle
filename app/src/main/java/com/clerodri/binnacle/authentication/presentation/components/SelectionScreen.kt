package com.clerodri.binnacle.authentication.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.clerodri.binnacle.R
import com.clerodri.binnacle.authentication.domain.model.Locality

/**
 * Author: Ronaldo R.
 * Date:  11/21/2025
 * Description: locality selection
 **/
@Composable
fun SelectionScreen(
    options: List<Locality>,
    selectedOption: Locality?,
    onOptionSelected: (Locality) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val composition2 by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.location)
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .windowInsetsPadding(WindowInsets.navigationBars),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section - Title and Dropdown
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(composition2 != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.Transparent)
                ) {
                    LottieAnimation(
                        composition = composition2,
                        isPlaying = true,
                        iterations = LottieConstants.IterateForever,
                        speed = 1f,
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Text(
                text = "Localidad",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )
            Text(
                text = "Seleccione una localidad para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            LocalityDropdownMenuCompact(
                options = options,
                selectedOption = selectedOption,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onOptionSelected = {
                    onOptionSelected(it)
                    expanded = false
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ButtonComponent(
                "Continuar",
                loginEnable = selectedOption != null,
            ) {
                onContinue()
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LocalityDropdownMenuCompact(
    options: List<Locality>,
    selectedOption: Locality?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (Locality) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        // Dropdown trigger with Row layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 2.dp,
                    color = if (expanded || selectedOption != null)
                        Color(0xFF2973B2).copy(0.3f)
                    else
                        Color(0xFF2973B2),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    if (selectedOption != null)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    else
                        Color.White
                )
                .clickable(enabled = options.isNotEmpty()) { onExpandedChange(!expanded) }
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text =  if (options.isEmpty()) "No hay urbanizaciones registradas."
                        else selectedOption?.name ?: "Seleccionar urbanización.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selectedOption != null) FontWeight.Bold else FontWeight.Normal,
                color = if (selectedOption != null)
                    MaterialTheme.colorScheme.secondary
                else
                    Color.Gray
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = if (selectedOption != null)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Gray
            )
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (option == selectedOption)
                                FontWeight.Bold
                            else
                                FontWeight.Normal,
                            color = if (option == selectedOption)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Black
                        )
                    },
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

