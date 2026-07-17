package com.example.weatherapp_ramide.ui

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.weatherapp_ramide.MainViewModel
import com.example.weatherapp_ramide.model.Weather
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapPage(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val recifePosition = LatLng(-8.05, -34.9)
    val camPosState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(recifePosition, 10f)
    }
    val context = LocalContext.current
    val hasLocationPermission =
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = camPosState,
        onMapClick = {
            viewModel.addCity(it)
        },
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission)
    ) {
        viewModel.cities.forEach { city ->
            city.location?.let { location ->
                val weather = viewModel.weather(city.name)
                val desc = if (weather == Weather.LOADING) "Carregando clima..." else weather.desc
                Marker(
                    state = MarkerState(position = location),
                    title = city.name,
                    snippet = desc
                )
            }
        }
    }
}
