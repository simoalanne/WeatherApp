package com.simoalanne.weatherapp.model

import com.simoalanne.weatherapp.GsonProvider

/**
 * Data class representing the main UI state.
 * @param favoriteLocations The list of favorite locations.
 * @param previewLocation The preview location.
 * @param isLoading Whether the UI is loading.
 * @param isRefreshing Whether the UI is refreshing weather data.
 * @param errorResId The resource ID of the error message.
 * @param pageIndex The current page index.
 */
// The isRefreshing is no longer important. the errorResId should rather be an enum value
// additionally generic error here doesn't even mean much. errors on individual weather level would
// make more sense.
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
// role has no need to be enum since it has shrunk down to just user and favorite. could be boolean instead
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
 * Data class representing a location role. could be replaced by boolean.
 */
enum class LocationRole {
    USER,
    FAVORITE,
}

/**
 * Extension function to convert a [LocationWeather] to a [WeatherEntity]. the weather data is simply
 * converted to raw json string so it can be stored in the database.
 */
fun LocationWeather.toWeatherEntity(): WeatherEntity {
    return WeatherEntity(
        locationKey = "${location.englishName},${location.countryCode}",
        json = GsonProvider.gson.toJson(weather)
    )
}
