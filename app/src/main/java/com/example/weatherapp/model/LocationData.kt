package com.example.weatherapp.model

data class LocationData(
    val englishName: String,
    val finnishName: String?,
    val lat: Double,
    val lon: Double,
    val countryCode: String,
    val state: String?,
)

