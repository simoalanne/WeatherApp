package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.mapper.mapApiResponsesToWeatherData
import com.example.weatherapp.model.GeocodeEntry
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.GeocodeAPI
import com.example.weatherapp.network.SunriseSunsetAPI
import com.example.weatherapp.network.WeatherAPI
import kotlinx.coroutines.launch
import com.example.weatherapp.R
import com.example.weatherapp.model.Coordinates
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.async
import java.time.LocalDate

// TODO: This is doing way too much work. No separation of concerns whatsoever in sight.
// TODO: This should be refactored ASAP before adding more features.
class WeatherViewModel() : ViewModel() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    /**
     * Main activity should call this method to set the fused location provider client.
     * @param client The fused location provider client.
     */
    fun setFusedLocationProviderClient(client: FusedLocationProviderClient) {
        fusedLocationProviderClient = client
    }

    private var _weather by mutableStateOf<WeatherData?>(null)
    private val _geocodeEntries: SnapshotStateList<GeocodeEntry> = mutableStateListOf()
    private var _error by mutableStateOf<Int?>(null)
    private var _isLoading by mutableStateOf(false)
    private var _previousCityName by mutableStateOf<String?>(null)
    private var _userLocation by mutableStateOf<GeocodeEntry?>(null)

    val weather: WeatherData? get() = _weather
    val geocodeEntries: List<GeocodeEntry> get() = _geocodeEntries
    val error: Int? get() = _error
    val isLoading: Boolean get() = _isLoading
    val userLocation: GeocodeEntry? get() = _userLocation

    fun fetchWeatherData(location: GeocodeEntry) {
        viewModelScope.launch {
            try {
                _isLoading = true
                _error = null
                _geocodeEntries.clear()

                val weatherDeferred = async {
                    WeatherAPI.service.getWeatherByCoordinates(
                        lat = location.lat,
                        lon = location.lon
                    )
                }

                val forecastDeferred = async {
                    WeatherAPI.service.getForecastByCoordinates(
                        lat = location.lat,
                        lon = location.lon
                    )
                }

                // start and end date can be hardcoded because forecast last entry is after 5 days
                // then minus 1 day for start is because we want to access the before "now"
                // sunrise and sunset times that can possibly fall in previous day.
                val startDate = LocalDate.now().minusDays(1).toString()
                val endDate = LocalDate.now().plusDays(5).toString()

                val sunriseSunsetDeferred = async {
                    SunriseSunsetAPI.service.getSunriseSunsetRange(
                        location.lat,
                        location.lon,
                        startDate,
                        endDate
                    )
                }

                // Calls can run in parallel because they are not dependent on each other
                val weatherResponse = weatherDeferred.await()
                val forecastResponse = forecastDeferred.await()
                val sunriseSunsetResponse = sunriseSunsetDeferred.await()

                val weatherData = mapApiResponsesToWeatherData(
                    weatherResponse,
                    forecastResponse,
                    sunriseSunsetResponse,
                    location
                )
                _weather = weatherData
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather data", e)
                _error = R.string.something_went_wrong
            } finally {
                _isLoading = false
            }
        }
    }


    fun fetchGeocodeEntries(cityName: String) {
        if (cityName.isBlank()) {
            _geocodeEntries.clear()
            _error = R.string.city_name_cannot_be_empty
            return
        }
        if (cityName == _previousCityName) {
            return
        }
        _previousCityName = cityName
        viewModelScope.launch {
            _isLoading = true
            try {
                _geocodeEntries.clear()
                _error = null
                val entries = GeocodeAPI.service.geocode(cityName).map {
                    GeocodeEntry(
                        it.name,
                        it.lat,
                        it.lon,
                        it.countryCode,
                        it.state,
                        it.localizedNames
                    )
                }.distinct()

                if (entries.isEmpty()) {
                    Log.d("WeatherViewModel", "No results for $cityName")
                    _error = R.string.no_results
                } else {
                    _geocodeEntries.addAll(entries)
                    _error = null
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching geocode entries", e)
                _error = R.string.something_went_wrong
            } finally {
                _isLoading = false
            }
        }
    }

    fun fetchWeatherDataForCurrentLocation() {
        _userLocation.let {
            if (it != null) {
                fetchWeatherData(it)
            } else {
                Log.e(
                    "WeatherViewModel",
                    "Do not call this method if location hasn't been resolved"
                )
            }
        }
    }

    /**
     * Tries to locate user and fetch weather data if fetchWeather is true.
     * The UI should NOT call this function if permissions are not granted.
     *
     * @param fetchWeather Whether to fetch weather data after locating user.
     */
    fun locateUser(fetchWeather: Boolean = false) {
        try {
            val client = fusedLocationProviderClient
            if (client == null) return
            client.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModelScope.launch {
                        val entry = reverseGeocode(Coordinates(location.latitude, location.longitude)).firstOrNull()
                        _userLocation = entry
                        if (fetchWeather && entry != null) {
                            fetchWeatherData(entry)
                        }
                    }
                } else {
                    Log.e(
                        "WeatherViewModel",
                        "Could not get location — either unavailable or permissions missing"
                    )
                    if (fetchWeather) {
                        fetchWeatherData(
                            GeocodeEntry(
                                "Tampere",
                                61.4991,
                                23.7871,
                                "FI",
                                null,
                                null
                            )
                        )
                    }
                }
            }.addOnFailureListener {
                Log.e("WeatherViewModel", "Location error: ${it.message}")
                if (fetchWeather) {
                    fetchWeatherData(GeocodeEntry("Tampere", 61.4991, 23.7871, "FI", null, null))
                }
            }
        } catch (e: SecurityException) {
            Log.d("WeatherViewModel", "Location error: ${e.message}")
        }
    }

    suspend fun reverseGeocode(coordinates: Coordinates): List<GeocodeEntry> {
        val geocodeEntries = GeocodeAPI.service.reverseGeocode(coordinates.lat, coordinates.lon)
        return geocodeEntries.map {
            GeocodeEntry(
                it.name,
                it.lat,
                it.lon,
                it.countryCode,
                it.state,
                it.localizedNames
            )
        }.distinct()
    }


    fun resetGeocodeEntries() {
        _geocodeEntries.clear()
        _previousCityName = null
    }
}
