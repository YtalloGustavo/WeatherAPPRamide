package com.example.weatherapp_ramide.model

import com.google.android.gms.maps.model.LatLng
import java.util.UUID

data class City(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val weather: String? = null,
    val location: LatLng? = null
)
