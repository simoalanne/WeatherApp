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
import com.example.weatherapp.utils.getLocalDateTimeFromUnixTimestamp
import kotlinx.coroutines.launch
import com.example.weatherapp.R
import kotlinx.coroutines.async
import java.time.LocalDate

class WeatherViewModel : ViewModel() {

    private var _weather by mutableStateOf<WeatherData?>(null)
    private val _geocodeEntries: SnapshotStateList<GeocodeEntry> = mutableStateListOf()
    private var _error by mutableStateOf<Int?>(null)
    private var _isLoading by mutableStateOf(false)
    private var _previousCityName by mutableStateOf<String?>(null)

    val weather: WeatherData? get() = _weather
    val geocodeEntries: List<GeocodeEntry> get() = _geocodeEntries
    val error: Int? get() = _error
    val isLoading: Boolean get() = _isLoading

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
                val uniqueEntries = GeocodeAPI.service.geocode(cityName).map {
                    GeocodeEntry(
                        it.name,
                        it.lat,
                        it.lon,
                        it.countryCode,
                        it.state,
                        it.localizedNames
                    )
                }.toSet().toList()
                if (uniqueEntries.isEmpty()) {
                    Log.d("WeatherViewModel", "No results for $cityName")
                    _error = R.string.no_results
                } else {
                    _geocodeEntries.addAll(uniqueEntries)
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

    init {
        // TODO: the initial fetched city should come from persistent storage
        fetchWeatherData(GeocodeEntry("Tampere", 61.4991, 23.7871, "FI", null, null))
    }

    fun resetGeocodeEntries() {
        _geocodeEntries.clear()
    }
}
