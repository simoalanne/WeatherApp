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
class MainViewModel(
    private val locationService: LocationService,
    private val locationDao: LocationDao,
    private val weatherDao: WeatherDao,
    private val onInitialDataLoaded: (doesUserHaveFavoriteLocations: Boolean) -> Unit = {}
) : ViewModel() {

    var uiState by mutableStateOf(MainUiState())
        private set

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
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
                }.distinctBy { it.location.englishName }

                uiState = uiState.copy(
                    favoriteLocations = weatherLocationList,
                )

                // This should never trigger but it's possible to have the data corrupted needing to
                // fully delete all of it
            } catch (e: NullPointerException) {
                Log.e("MainViewModel", "NullPointerException: ${e.message}, deleting all data")
                weatherDao.deleteAllWeather()
                locationDao.deleteAll()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading initial data: ${e.message}")
            } finally {
                uiState = uiState.copy(isLoading = false)
                Log.d("MainViewModel", "Initial data loaded")
                onInitialDataLoaded(uiState.favoriteLocations.isNotEmpty())
            }
        }
    }

    fun changePreviewToFavorite() {
        uiState = uiState.copy(errorResId = null)
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

    fun refreshWeather(index: Int = 0) {
        viewModelScope.launch {
            try {
                val isPreview = uiState.previewLocation != null
                val target = uiState.previewLocation ?: uiState.favoriteLocations.getOrNull(index)
                ?: return@launch

                val cachedWeather = target.weather
                    ?: weatherDao.getWeather("${target.location.englishName},${target.location.countryCode}")
                        ?.toWeather()
                val needsRefresh = when (cachedWeather) {
                    null -> true
                    else -> {
                        val lastRefreshTime = cachedWeather.current.time
                        val utcOffsetSeconds = cachedWeather.meta.utcOffsetSeconds
                        val nextExpectedRefreshTime = lastRefreshTime.plusMinutes(15)
                        val currentTime =
                            LocalDateTime.now(ZoneOffset.UTC).plusSeconds(utcOffsetSeconds.toLong())
                        currentTime.isAfter(nextExpectedRefreshTime)
                    }
                }

                if (target.weather != null && !needsRefresh) {
                    return@launch
                }

                val weatherData = if (needsRefresh) {
                    fetchWeatherDataForCoordinates(
                        Coordinates(target.location.lat, target.location.lon)
                    ) ?: cachedWeather
                } else {
                    cachedWeather
                }

                uiState = if (isPreview) {
                    uiState.copy(previewLocation = target.copy(weather = weatherData))
                } else {
                    uiState.copy(
                        favoriteLocations = uiState.favoriteLocations.map {
                            if (it == target) it.copy(weather = weatherData) else it
                        })
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
                uiState = uiState.copy(
                    favoriteLocations = newLocations,
                    pageIndex = uiState.pageIndex.coerceAtMost(newLocations.lastIndex)
                )
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
                if (weatherData == null) {
                    uiState = uiState.copy(errorResId = R.string.something_went_wrong)
                    return@launch
                }
                uiState = uiState.copy(
                    previewLocation = LocationWeather(
                        locationData, weatherData, LocationRole.FAVORITE
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
        val leftNeighbor = (index - 1).coerceAtLeast(0)
        val rightNeighbor = (index + 1).coerceAtMost(uiState.favoriteLocations.lastIndex)

        uiState = uiState.copy(
            pageIndex = index,
            favoriteLocations = uiState.favoriteLocations.mapIndexed { i, locationWeather ->
                if (i != index && i != leftNeighbor && i != rightNeighbor) {
                    Log.d(
                        "MainViewModel",
                        "Freeing weather data for ${locationWeather.location.englishName}"
                    )
                    locationWeather.copy(weather = null)
                } else {
                    refreshWeather(i)
                    locationWeather
                }
            })
    }

    fun setAllButCurrentWeatherToNull() {
        uiState = uiState.copy(
            favoriteLocations = uiState.favoriteLocations.mapIndexed { index, locationWeather ->
                if (index != uiState.pageIndex) {
                    locationWeather.copy(weather = null)
                } else {
                    locationWeather
                }
            })
    }

    fun clearError() {
        uiState = uiState.copy(errorResId = null)
    }

    fun locateUser() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(errorResId = null)
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
                if (userWeather == null) {
                    uiState = uiState.copy(errorResId = R.string.something_went_wrong)
                    return@launch
                }
                val userLocationWithWeather = LocationWeather(
                    userLocation.location, userWeather, userLocation.role
                )
                val newLocations =
                    listOf(userLocationWithWeather) + uiState.favoriteLocations.distinctBy { it.location.englishName }
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
