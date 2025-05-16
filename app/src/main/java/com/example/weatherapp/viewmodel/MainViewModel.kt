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
import com.example.weatherapp.location.UserLocationProvider
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.LocationAndRole
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.LocationWeather
import com.example.weatherapp.model.MainUiState
import com.example.weatherapp.model.WeatherUIStatus
import com.example.weatherapp.model.toLocationData
import com.example.weatherapp.model.toLocationEntity
import com.example.weatherapp.network.GeocodeAPI
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
    private lateinit var userLocationProvider: UserLocationProvider
    private lateinit var locationDao: LocationDao

    fun setUserLocationProvider(provider: UserLocationProvider) {
        userLocationProvider = provider
    }

    fun setLocationDao(dao: LocationDao) {
        locationDao = dao
    }

    var uiState by mutableStateOf(MainUiState())
        private set

    fun loadInitialData() {
        Log.d("MainViewModel", "Loading initial data")
        viewModelScope.launch {
            try {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                val userCoordsDeferred = async { userLocationProvider.getCurrentLocation() }
                val savedLocationsDeferred = async { locationDao.getAll() }

                val userCoords = userCoordsDeferred.await()
                val userLocation = if (userCoords != null) {
                    LocationAndRole(
                        GeocodeAPI.service.reverseGeocode(userCoords.lat, userCoords.lon)
                            .first()
                            .toLocationData(),
                        LocationRole.USER
                    )
                } else null

                val savedLocations =
                    savedLocationsDeferred.await().map { it.toLocationAndRole() }

                val locations = (listOfNotNull(userLocation) + savedLocations).map {
                    async {
                        val weatherData = fetchWeatherDataForCoordinates(
                            Coordinates(it.location.lat, it.location.lon)
                        )
                        LocationWeather(it.location, weatherData, it.role)
                    }
                }.awaitAll()
                uiState = if (locations.isEmpty()) {
                    uiState.copy(uiStatus = WeatherUIStatus.EMPTY)
                } else {
                    uiState.copy(
                        uiStatus = WeatherUIStatus.SUCCESS,
                        locations = locations,
                        currentLocation = locations.first()
                    )
                }
            } catch (e: Exception) {
                uiState =
                    uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            }
        }
    }

    fun addNewLocation(locationWeather: LocationWeather) {
        if (locationWeather.role != LocationRole.FAVORITE) {
            Log.e(
                "MainViewModel",
                "Invalid location role. Expected FAVORITE, got ${locationWeather.role}"
            )
        }

        if (uiState.locations.any { it.location == locationWeather.location }) {
            Log.e(
                "MainViewModel",
                "Location already exists in list as: ${locationWeather.location}"
            )
        }

        if (uiState.locations.count { it.role == LocationRole.FAVORITE }
            >= (BuildConfig.MAX_FAVORITE_LOCATIONS.toIntOrNull() ?: 5)) {
            Log.e(
                "MainViewModel",
                "Maximum number of favorite locations reached (${BuildConfig.MAX_FAVORITE_LOCATIONS})"
            )
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                locationDao.add(locationWeather.toLocationEntity())
                val newLocations = listOf(locationWeather) + uiState.locations
                uiState = uiState.copy(locations = newLocations, uiStatus = WeatherUIStatus.SUCCESS)
            } catch (e: Exception) {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            }
        }
    }

    fun refreshCurrentLocation() {
        Log.d("MainViewModel", "Refreshing current location")
        viewModelScope.launch {
            try {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.REFRESHING)
                val currentLocation = uiState.currentLocation
                if (currentLocation != null) {
                    val weatherData = fetchWeatherDataForCoordinates(
                        Coordinates(currentLocation.location.lat, currentLocation.location.lon)
                    )
                    uiState =
                        uiState.copy(currentLocation = currentLocation.copy(weather = weatherData))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            } finally {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.SUCCESS)
            }
        }
    }

    fun deleteLocation(locationWeather: LocationWeather) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                locationDao.delete(locationWeather.toLocationEntity())
                val newLocations = uiState.locations - locationWeather
                uiState = uiState.copy(locations = newLocations)
            } catch (e: Exception) {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            } finally {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.SUCCESS)
            }
        }
    }

    fun previewLocation(locationData: LocationData) {
        Log.d("MainViewModel", "Previewing location: $locationData")
        try {
            viewModelScope.launch {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                val weatherData = fetchWeatherDataForCoordinates(
                    Coordinates(locationData.lat, locationData.lon)
                )
                uiState = uiState.copy(
                    currentLocation = LocationWeather(
                        locationData,
                        weatherData,
                        LocationRole.PREVIEW
                    ),
                    uiStatus = WeatherUIStatus.SUCCESS
                )
            }
        } catch (_: Exception) {
            uiState = uiState.copy(uiStatus = WeatherUIStatus.ERROR)
        }
    }

    fun changeCurrentLocation(index: Int) {
        Log.d("MainViewModel", "Changing current location to: $index")
        try {
            uiState = uiState.copy(currentLocation = uiState.locations[index])
        } catch (_: IndexOutOfBoundsException) {
            Log.e(
                "MainViewModel",
                "Invalid index. Expected 0 - ${uiState.locations.size - 1}, got $index"
            )
            uiState = uiState.copy(uiStatus = WeatherUIStatus.ERROR)
        }
    }

    fun locateUser() {
        Log.d("MainViewModel", "Locating user")
        viewModelScope.launch {
            try {
                val userCoords = userLocationProvider.getCurrentLocation()
                if (userCoords != null) {
                    val userLocation = LocationAndRole(
                        GeocodeAPI.service.reverseGeocode(userCoords.lat, userCoords.lon)
                            .first()
                            .toLocationData(),
                        LocationRole.USER
                    )
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
                }
            } catch (e: Exception) {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            }
        }
    }
}
