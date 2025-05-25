package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.database.LocationDao
import com.example.weatherapp.model.toLocationAndRole
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.LocationAndRole
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.LocationWeather
import com.example.weatherapp.model.MainUiState
import com.example.weatherapp.model.toLocationEntity
import com.example.weatherapp.utils.fetchWeatherDataForCoordinates
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * The main view model for the app required by most of the screens. It holds the state of locations
 * and their associated weather data.
 *
 * Responsible for:
 * - Fetching the user's location and retrieving weather data for it
 * - Loading saved locations from the database and retrieving weather data for them where applicable
 * - Adding and deleting locations from the database
 * - Providing UI state data for most of the screens such as WeatherScreen and SearchScreen
 * - Keeping database and the UI state in sync
 */
class MainViewModel : ViewModel() {
    private lateinit var locationService: LocationService
    private lateinit var locationDao: LocationDao

    fun setLocationService(service: LocationService) {
        locationService = service
    }

    fun setLocationDao(dao: LocationDao) {
        locationDao = dao
    }

    var uiState by mutableStateOf(MainUiState())
        private set

    fun loadInitialData() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorResId = null)
                val userLocationDeferred =
                    async { runCatching { locationService.getUserLocation() }.getOrNull() }
                val savedLocationsDeferred = async { locationDao.getAll() }

                val userLocation = userLocationDeferred.await()
                Log.d("MainViewModel", "User location: $userLocation")
                val userLocationRole = userLocation?.let { LocationAndRole(it, LocationRole.USER) }

                val savedLocations =
                    savedLocationsDeferred.await().map { it.toLocationAndRole() }
                val locations = (listOfNotNull(userLocationRole) + savedLocations).map {
                    async {
                        val weatherData = fetchWeatherDataForCoordinates(
                            Coordinates(it.location.lat, it.location.lon)
                        )
                        if (weatherData == null) return@async null
                        LocationWeather(it.location, weatherData, it.role)
                    }
                }.awaitAll().filterNotNull()

                val filteredLocations = listOfNotNull(locations.firstOrNull()) + locations.filter {
                    it.location.englishName != locations.firstOrNull()?.location?.englishName
                }
                uiState = if (filteredLocations.isEmpty()) {
                    uiState.copy(errorResId = R.string.no_locations)
                } else {
                    uiState.copy(
                        favoriteLocations = filteredLocations,
                    )
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading initial data ${e.message}")
                uiState =
                    uiState.copy(errorResId = R.string.something_went_wrong)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun changePreviewToFavorite() {
        val previewLocation = uiState.previewLocation ?: return
        if (uiState.favoriteLocations.any { it.location.englishName == previewLocation.location.englishName }) {
            Log.d("MainViewModel", "Location already in favorites: ${previewLocation.location}")
            uiState = uiState.copy(previewLocation = null)
            return
        }

        viewModelScope.launch {
            try {
                locationDao.add(previewLocation.location.toLocationEntity())
                val updatedFavorites = uiState.favoriteLocations + previewLocation
                val newPageIndex = updatedFavorites.lastIndex

                uiState = uiState.copy(
                    favoriteLocations = updatedFavorites,
                    pageIndex = newPageIndex
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save preview as favorite", e)
                uiState = uiState.copy(errorResId = R.string.something_went_wrong)
            }
        }
    }

    fun clearPreview() {
        uiState = uiState.copy(previewLocation = null)
    }

    fun refreshWeather(index: Int = 0, changeIsRefreshing: Boolean = true) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isRefreshing = changeIsRefreshing, errorResId = null)
                val target =
                    if (uiState.previewLocation != null) uiState.previewLocation else uiState.favoriteLocations[index]
                if (target != null) {
                    val lastRefreshTime = target.weather.current.time
                    val utcOffsetSeconds = target.weather.meta.utcOffsetSeconds
                    val nextExpectedRefreshTime = lastRefreshTime.plusMinutes(15)
                    val currentTime =
                        LocalDateTime.now(ZoneOffset.UTC).plusSeconds(utcOffsetSeconds.toLong())
                    if (currentTime.isBefore(nextExpectedRefreshTime)) {
                        Log.d("MainViewModel", "Not refreshing, not enough time has passed")
                        return@launch
                    }
                    val weatherData = fetchWeatherDataForCoordinates(
                        Coordinates(target.location.lat, target.location.lon)
                    )
                    uiState = if (uiState.previewLocation != null) {
                        uiState.copy()
                    } else {
                        uiState.copy(
                            favoriteLocations = uiState.favoriteLocations.map {
                                if (it == target) {
                                    it.copy(weather = weatherData!!)
                                } else {
                                    it
                                }
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorResId = R.string.something_went_wrong)
            } finally {
                uiState = uiState.copy(isRefreshing = false)
            }
        }
    }

    fun removeFavoriteLocation(location: LocationData) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorResId = null)
                locationDao.delete(location.englishName, location.countryCode)
                val newLocations =
                    uiState.favoriteLocations.filter { locationWeather ->
                        locationWeather.location != location
                    }
                uiState = uiState.copy(favoriteLocations = newLocations)
                Log.d("MainViewModel", "Removed favorite location: $location")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error removing favorite location", e)
                uiState = uiState.copy(errorResId = R.string.something_went_wrong)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun previewLocation(locationData: LocationData) {
        try {
            viewModelScope.launch {
                uiState = uiState.copy(isLoading = true, errorResId = null)
                val weatherData = fetchWeatherDataForCoordinates(
                    Coordinates(locationData.lat, locationData.lon)
                )
                uiState = uiState.copy(
                    previewLocation = LocationWeather(
                        locationData,
                        weatherData!!,
                        LocationRole.FAVORITE
                    )
                )
            }
        } catch (_: Exception) {
            uiState = uiState.copy(errorResId = R.string.something_went_wrong)
        } finally {
            uiState = uiState.copy(isLoading = false)
        }
    }

    fun changePageIndex(index: Int) {
        uiState = uiState.copy(pageIndex = index)
    }

    fun locateUser() {
        viewModelScope.launch {
            try {
                val userLocation = LocationAndRole(
                    locationService.getUserLocation(),
                    LocationRole.USER
                )
                if (uiState.favoriteLocations.any { it.location == userLocation.location && it.role == LocationRole.USER }) {
                    Log.d("MainViewModel", "User location already in favorites, nothing to update")
                    return@launch
                }
                val userWeather = fetchWeatherDataForCoordinates(
                    Coordinates(userLocation.location.lat, userLocation.location.lon)
                )
                val userLocationWithWeather =
                    LocationWeather(
                        userLocation.location,
                        userWeather!!,
                        userLocation.role
                    )
                val newLocations = listOf(userLocationWithWeather) + uiState.favoriteLocations
                uiState = uiState.copy(
                    favoriteLocations = newLocations,
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error locating user", e)
                uiState = uiState.copy(errorResId = R.string.something_went_wrong)
            }
        }
    }
}
