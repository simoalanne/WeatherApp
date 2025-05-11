package com.example.weatherapp.model

/**
 * Helper class for representing the location type. Used to determine what api function to call
 *
 */
sealed class LocationType {
    data class City(val name: String) : LocationType()
    data class Coordinates(val lat: Double, val lon: Double) : LocationType()
}