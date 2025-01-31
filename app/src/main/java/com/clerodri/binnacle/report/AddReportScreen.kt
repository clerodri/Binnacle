package com.clerodri.binnacle.report

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun AddReportScreen() {
    Scaffold {paddingValues ->

        Text("Report Screen", modifier = Modifier.padding(paddingValues), fontSize = 26.sp);
    }
}