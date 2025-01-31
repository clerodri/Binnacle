package com.clerodri.binnacle.location.presentation

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clerodri.binnacle.location.domain.LocationTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationTracker: LocationTracker
) :ViewModel(){

    var currentLocation by mutableStateOf<Location?>(null)

    fun getCurrentLocation() {

        viewModelScope.launch(Dispatchers.IO) {
            currentLocation = locationTracker.getCurrentLocation() // Location

        }
    }
}