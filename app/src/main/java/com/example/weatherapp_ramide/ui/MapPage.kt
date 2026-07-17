package com.example.weatherapp_ramide.ui

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.example.weatherapp_ramide.MainViewModel
import com.example.weatherapp_ramide.R
import com.example.weatherapp_ramide.model.Weather
import com.google.android.gms.maps.model.BitmapDescriptorFactory
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
            if (city.location != null) {
                val location = city.location!!
                val weather = viewModel.weather(city.name)

                val image = weather.bitmap
                    ?: getDrawable(context, R.drawable.loading)!!.toBitmap()

                val marker = BitmapDescriptorFactory
                    .fromBitmap(image.scale(120, 120))

                val desc = if (weather == Weather.LOADING)
                    "Carregando clima..." else weather.desc

                Marker(
                    state = MarkerState(position = location),
                    icon = marker,
                    title = city.name,
                    snippet = desc
                )
            }
        }
    }
}
