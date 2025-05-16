package com.example.weatherapp.model

data class MainUiState(
    val locations: List<LocationWeather> = emptyList(),
    val currentLocation: LocationWeather? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorRecourseId: Int? = null // TODO: Should be a enum class instead for better control
)

/**
 * Data class representing a location and it's associated weather.
 * @param location The location containing lat, lon, localized names, etc.
 * @param weather The weather data for the location.
 * @param role The role of the location.
 */
data class LocationWeather(
    val location: LocationData,
    val weather: WeatherData,
    val role: LocationRole,
)

data class LocationAndRole(
    val location: LocationData,
    val role: LocationRole
)

fun LocationWeather.toLocationEntity(): LocationEntity {
    return LocationEntity(
        englishName = location.englishName,
        finnishName = location.finnishName,
        lat = location.lat,
        lon = location.lon,
        countryCode = location.countryCode,
        state = location.state,
    )
}

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
