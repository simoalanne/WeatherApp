package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.database.LocationDao
import com.example.weatherapp.model.toLocationAndRole
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.LocationAndRole
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.LocationWeather
import com.example.weatherapp.model.MainUiState
import com.example.weatherapp.model.WeatherUIStatus
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
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                val userLocationDeferred =
                    async { runCatching { locationService.getUserLocation() }.getOrNull() }
                val savedLocationsDeferred = async { locationDao.getAll() }

                val userLocation = userLocationDeferred.await()
                val userLocationRole = userLocation?.let { LocationAndRole(it, LocationRole.USER) }

                val savedLocations =
                    savedLocationsDeferred.await().map { it.toLocationAndRole() }
                val locations = (listOfNotNull(userLocationRole) + savedLocations).map {
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
                Log.d("MainViewModel", "Initial data loaded: $uiState")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading initial data", e)
                uiState =
                    uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            } catch (e: SecurityException) {
                Log.e("MainViewModel", "Error loading initial data", e)
                uiState =
                    uiState.copy(uiStatus = WeatherUIStatus.ERROR)
            }
        }
    }

    fun addFavoriteLocation(locationData: LocationData) {
        if (uiState.locations.any { it.location == locationData }) {
            Log.e(
                "MainViewModel",
                "Location already exists in list as: ${uiState.locations.find { it.location == locationData }}"
            )
            return
        }

        viewModelScope.launch {
            try {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                locationDao.add(locationData.toLocationEntity())
                val weatherData = fetchWeatherDataForCoordinates(
                    Coordinates(locationData.lat, locationData.lon)
                )
                val newLocations = uiState.locations + LocationWeather(
                    locationData,
                    weatherData,
                    LocationRole.FAVORITE
                )
                Log.d("MainViewModel", "New locations: ${newLocations.map { it.location }}")
                uiState =
                    uiState.copy(locations = newLocations, uiStatus = WeatherUIStatus.SUCCESS)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error adding new favorite location", e)
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

    fun removeFavoriteLocation(location: LocationData) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(uiStatus = WeatherUIStatus.LOADING)
                locationDao.delete(location.englishName, location.countryCode)
                val newLocations =
                    uiState.locations.filter { locationWeather ->
                        locationWeather.location != location
                    }
                uiState = uiState.copy(locations = newLocations)
                Log.d("MainViewModel", "Removed favorite location: $location")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error removing favorite location", e)
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
    }
}
