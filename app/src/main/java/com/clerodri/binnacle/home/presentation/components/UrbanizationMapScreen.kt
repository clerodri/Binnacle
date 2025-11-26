package com.clerodri.binnacle.addreport.presentation.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * Composable for displaying a specific urbanization location on Google Maps
 */
@Composable
fun UrbanizationMapScreen2(
    latitude: Double,
    longitude: Double,
    title: String = "Urbanization",
    boundaryPoints: List<LatLng>,  // ✅ The red polygon points
    modifier: Modifier = Modifier
) {
    // ✅ Create bounds from boundary points
    val bounds = LatLngBounds.builder().apply {
        boundaryPoints.forEach { include(it) }
    }.build()

    // ✅ Camera will fit the bounds with padding
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bounds.center, 16.3f)
    }

    // ✅ Update camera to fit bounds
    LaunchedEffect(bounds) {
        try {
            val update = CameraUpdateFactory.newLatLngBounds(bounds, 100) // 100dp padding
            cameraPositionState.move(update)
        } catch (e: Exception) {
            Log.e("MapScreen", "Error updating camera: ${e.message}")
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.TERRAIN,
            maxZoomPreference = 17.5f,  // Max zoom allowed
            minZoomPreference = 16.3f,
            isBuildingEnabled = false,
            isIndoorEnabled = false,
            isMyLocationEnabled = false,
            isTrafficEnabled = false,
        )
    ) {
        // ✅ Draw red boundary polygon
        Polyline(
            points = boundaryPoints,
            color = Color.Red,
            width = 2f
        )

        // ✅ Center marker
        Marker(
            state = rememberMarkerState(position = bounds.center),
            title = title
        )
    }
}

@Composable
fun UrbanizationMapWithBoundary(
    latitude: Double,
    longitude: Double,
    title: String = "Urbanization",
    boundaryPoints: List<LatLng> = emptyList(),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
) {
    val locationLatLng = LatLng(latitude, longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(locationLatLng, 16.2f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        // Center marker
        Marker(
            state = rememberMarkerState(position = locationLatLng),
            title = title
        )

        if (boundaryPoints.isNotEmpty()) {
            Polyline(
                points = boundaryPoints,
                color = Color.Blue,
                width = 3f
            )
        }
    }
}


@Composable
fun UrbanizationMapScreen(
    latitude: Double,
    longitude: Double,
    title: String = "Urbanization",
    boundaryPoints: List<LatLng>,
    modifier: Modifier = Modifier
) {
    val bounds = LatLngBounds.builder().apply {
        boundaryPoints.forEach { include(it) }
    }.build()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bounds.center, 16f)
    }

    Column(modifier = modifier.fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 0.dp))
    {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Lat: $latitude, Lon: $longitude",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        // Map
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isBuildingEnabled = false,
                isIndoorEnabled = false,
                isTrafficEnabled = false
            )
        ) {
            Polyline(
                points = boundaryPoints,
                color = Color.Red,
                width = 2f
            )

            Marker(
                state = rememberMarkerState(position = bounds.center),
                title = title
            )
        }
    }
}