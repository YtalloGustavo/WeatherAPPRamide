package com.example.weatherapp_ramide.model

data class Forecast(
    val date: String,
    val weather: String,
    val tempMin: Double,
    val tempMax: Double,
    val imgUrl: String,
)
