package com.example.weatherapp.model

data class LocationData(
    val englishName: String,
    val finnishName: String?,
    val lat: Double,
    val lon: Double,
    val countryCode: String,
    val state: String?,
)

fun LocationData.toLocationEntity(): LocationEntity {
    return LocationEntity(
        englishName = englishName,
        finnishName = finnishName,
        lat = lat,
        lon = lon,
        countryCode = countryCode,
        state = state,
    )
}
