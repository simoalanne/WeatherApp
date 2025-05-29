package com.example.weatherapp.model

import com.example.weatherapp.GsonProvider

data class MainUiState(
    val favoriteLocations: List<LocationWeather> = emptyList(),
    val previewLocation: LocationWeather? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorResId: Int? = null,
    val pageIndex: Int = 0,
)

/**
 * Data class representing a location and it's associated weather.
 * @param location The location containing lat, lon, localized names, etc.
 * @param weather The weather data for the location.
 * @param role The role of the location.
 */
data class LocationWeather(
    val location: LocationData,
    val weather: WeatherData?,
    val role: LocationRole,
)

data class LocationAndRole(
    val location: LocationData,
    val role: LocationRole
)

/**
 * Data class representing a location role.
 * - USER: The location is the user's current physical location. Weather should be fetched on app start.
 * - FAVORITE: The location is a favorite location. Weather should be fetched on app start.
 * - PREVIEW: When user is just looking at some weather and it's not user or favorite.
 */
enum class LocationRole {
    USER,
    FAVORITE,
    PREVIEW
}

fun LocationWeather.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        locationKey = "${location.englishName},${location.countryCode}",
        json = GsonProvider.gson.toJson(weather)
    )
}
