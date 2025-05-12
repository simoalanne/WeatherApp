package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class GeocodeEntry(
    val name: String,
    val lat: Double,
    val lon: Double,
    @SerializedName("country")
    val countryCode: String,
    val state: String?,
    // This could be useful for localization but for now not required
    // @SerializedName("local_names")
    //val localizedNames: Map<String, String>
)

data class Location(val name: String, val countryCode: String, val state: String?)

enum class Accuracy {
    LOCATION,
    LOCATION_AND_COUNTRY,
    LOCATION_AND_STATE_AND_COUNTRY
}