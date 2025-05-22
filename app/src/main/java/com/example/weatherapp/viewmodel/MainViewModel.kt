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
import kotlinx.coroutines.launch

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
                uiState = if (locations.isEmpty()) {
                    uiState.copy(errorResId = R.string.no_locations)
                } else {
                    uiState.copy(
                        favoriteLocations = locations
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

    fun addFavoriteLocation(locationData: LocationData) {
        if (uiState.favoriteLocations.any { it.location == locationData }) {
            Log.e("MainViewModel", "Location already in favorites: $locationData")
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorResId = null)
                locationDao.add(locationData.toLocationEntity())
                val weatherData = fetchWeatherDataForCoordinates(
                    Coordinates(locationData.lat, locationData.lon)
                )
                val newLocations = uiState.favoriteLocations + LocationWeather(
                    locationData,
                    weatherData!!,
                    LocationRole.FAVORITE
                )
                uiState =
                    uiState.copy(favoriteLocations = newLocations)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error adding new favorite location", e)
                uiState = uiState.copy(errorResId = R.string.something_went_wrong)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun refreshWeather(index: Int = 0) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isRefreshing = true, errorResId = null)
                val target =
                    if (uiState.previewLocation != null) uiState.previewLocation else uiState.favoriteLocations[index]
                if (target != null) {
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
                        LocationRole.PREVIEW
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
        uiState = uiState.copy(pageIndex = index, previewLocation = null)
    }

    /*
    fun locateUser() {
        Log.d("MainViewModel", "Locating user")
        viewModelScope.launch {
            try {
                val userLocation = LocationAndRole(
                    locationService.getUserLocation(),
                    LocationRole.USER
                )
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                val userWeather = fetchWeatherDataForCoordinates(
                    Coordinates(userLocation.location.lat, userLocation.location.lon)
                )
                val userLocationWithWeather =
                    LocationWeather(
                        userLocation.location,
                        userWeather,
                        userLocation.role
                    )
                val newLocations = listOf(userLocationWithWeather) + uiState.locations
                uiState = uiState.copy(
                    locations = newLocations,
                    uiStatus = WeatherUIStatus.SUCCESS
                )
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error locating user", e)
                uiState = uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            }
        }
    } */
}
