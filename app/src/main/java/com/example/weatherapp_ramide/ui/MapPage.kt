package com.example.weatherapp_ramide.ui

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.weatherapp_ramide.MainViewModel
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
    val recife = remember { MarkerState(recifePosition) }
    val caruaru = remember { MarkerState(LatLng(-8.27, -35.98)) }
    val joaoPessoa = remember { MarkerState(LatLng(-7.12, -34.84)) }
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
            viewModel.add("Cidade@${it.latitude}:${it.longitude}", location = it)
        },
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = hasLocationPermission)
    ) {
        viewModel.cities.forEach { city ->
            city.location?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = city.name,
                    snippet = "$location"
                )
            }
        }

        Marker(
            state = recife,
            title = "Recife",
            snippet = "Marcador em Recife",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        )
        Marker(
            state = caruaru,
            title = "Caruaru",
            snippet = "Marcador em Caruaru",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        )
        Marker(
            state = joaoPessoa,
            title = "Joao Pessoa",
            snippet = "Marcador em Joao Pessoa",
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        )
    }
}
