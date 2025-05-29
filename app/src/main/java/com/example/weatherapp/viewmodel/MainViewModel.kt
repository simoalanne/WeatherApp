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
import com.example.weatherapp.database.WeatherDao
import com.example.weatherapp.model.toLocationAndRole
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.LocationAndRole
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.LocationWeather
import com.example.weatherapp.model.MainUiState
import com.example.weatherapp.model.toLocationEntity
import com.example.weatherapp.model.toWeather
import com.example.weatherapp.model.toWeatherEntity
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
    private lateinit var weatherDao: WeatherDao

    fun setLocationService(service: LocationService) {
        locationService = service
    }

    fun setLocationDao(dao: LocationDao) {
        locationDao = dao
    }

    fun setWeatherDao(dao: WeatherDao) {
        weatherDao = dao
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
                val userLocationRole = userLocation?.let { LocationAndRole(it, LocationRole.USER) }
                val savedLocations = savedLocationsDeferred.await().map { it.toLocationAndRole() }

                val allLocations = listOfNotNull(userLocationRole) + savedLocations

                val cachedWeathers = weatherDao.getAllWeather()

                val weatherLocationList = allLocations.map { location ->
                    val cachedWeather = cachedWeathers.find {
                        it.locationKey == "${location.location.englishName},${location.location.countryCode}"
                    }

                    LocationWeather(
                        location = location.location,
                        weather = cachedWeather?.toWeather(),
                        role = location.role
                    )
                }

                uiState = uiState.copy(
                    favoriteLocations = weatherLocationList,
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading initial data: ${e.message}")
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
                    favoriteLocations = updatedFavorites, pageIndex = newPageIndex
                )
                weatherDao.upsertWeather(previewLocation.toWeatherEntity())
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to save preview as favorite", e)
                uiState = uiState.copy(errorResId = R.string.something_went_wrong)
            }
        }
    }

    fun clearPreview() {
        uiState = uiState.copy(previewLocation = null)
    }

    fun refreshWeather(changeIsRefreshing: Boolean = true) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isRefreshing = changeIsRefreshing, errorResId = null)

                val isPreview = uiState.previewLocation != null
                val target = uiState.previewLocation
                    ?: uiState.favoriteLocations.getOrNull(uiState.pageIndex)
                    ?: return@launch

                val needsRefresh = when (val weather = target.weather) {
                    null -> true // No data yet, must fetch
                    else -> {
                        val lastRefreshTime = weather.current.time
                        val utcOffsetSeconds = weather.meta.utcOffsetSeconds
                        val nextExpectedRefreshTime = lastRefreshTime.plusMinutes(15)
                        val currentTime =
                            LocalDateTime.now(ZoneOffset.UTC).plusSeconds(utcOffsetSeconds.toLong())
                        currentTime.isAfter(nextExpectedRefreshTime)
                    }
                }

                if (!needsRefresh) {
                    Log.d("MainViewModel", "Not refreshing — data still fresh.")
                    return@launch
                }

                val weatherData = fetchWeatherDataForCoordinates(
                    Coordinates(target.location.lat, target.location.lon)
                ) ?: return@launch // In case fetch failed silently

                uiState = if (isPreview) {
                    uiState.copy(previewLocation = target.copy(weather = weatherData))
                } else {
                    uiState.copy(
                        favoriteLocations = uiState.favoriteLocations.map {
                            if (it == target) it.copy(weather = weatherData) else it
                        }
                    )
                }

                weatherDao.upsertWeather(
                    LocationWeather(target.location, weatherData, target.role).toWeatherEntity()
                )

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
                val newLocations = uiState.favoriteLocations.filter { locationWeather ->
                    locationWeather.location != location
                }
                weatherDao.deleteWeather("${location.englishName},${location.countryCode}")
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
                        locationData, weatherData!!, LocationRole.FAVORITE
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
                    locationService.getUserLocation(), LocationRole.USER
                )
                if (uiState.favoriteLocations.any { it.location == userLocation.location && it.role == LocationRole.USER }) {
                    Log.d(
                        "MainViewModel", "User location already in favorites, nothing to update"
                    )
                    return@launch
                }
                val userWeather = fetchWeatherDataForCoordinates(
                    Coordinates(userLocation.location.lat, userLocation.location.lon)
                )
                val userLocationWithWeather = LocationWeather(
                    userLocation.location, userWeather!!, userLocation.role
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
