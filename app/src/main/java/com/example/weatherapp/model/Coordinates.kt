package com.example.weatherapp.model

/**
 * Data class representing the coordinates of a location.
 */
// TODO: this is unnecessary passing lat and lon separately like app mostly does already is better
data class Coordinates(
    val lat: Double,
    val lon: Double
)
