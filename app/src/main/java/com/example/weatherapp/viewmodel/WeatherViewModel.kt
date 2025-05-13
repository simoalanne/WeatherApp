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
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.GeocodeEntry
import com.example.weatherapp.model.Location
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.GeocodeAPI
import com.example.weatherapp.network.SunriseSunsetAPI
import com.example.weatherapp.network.WeatherAPI
import com.example.weatherapp.utils.getLocalDateTimeFromUnixTimestamp
import kotlinx.coroutines.launch
import com.example.weatherapp.R

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

    fun fetchWeatherData(coordinates: Coordinates, location: Location) {
        viewModelScope.launch {
            try {
                _isLoading = true
                _error = null
                _geocodeEntries.clear()
                val weatherResponse = WeatherAPI.service.getWeatherByCoordinates(
                    lat = coordinates.lat,
                    lon = coordinates.lon
                )
                val forecastResponse = WeatherAPI.service.getForecastByCoordinates(
                    lat = coordinates.lat,
                    lon = coordinates.lon
                )
                val (lat, lon) = forecastResponse.cityInfo.coordinates

                val startDate = getLocalDateTimeFromUnixTimestamp(
                    weatherResponse.timestamp,
                    forecastResponse.cityInfo.timezone
                ).toLocalDate().minusDays(1).toString()

                val endDate = getLocalDateTimeFromUnixTimestamp(
                    forecastResponse.forecastList.last().timestamp,
                    forecastResponse.cityInfo.timezone
                ).toLocalDate().toString()

                val sunriseSunsetResponse = SunriseSunsetAPI.service.getSunriseSunsetRange(
                    lat,
                    lon,
                    startDate,
                    endDate
                )

                val weatherData = mapApiResponsesToWeatherData(
                    weatherResponse,
                    forecastResponse,
                    sunriseSunsetResponse,
                    location
                )
                _weather = weatherData
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
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
                // If entry has same name, country code and state, only keep the first occurrence
                val uniqueEntries = GeocodeAPI.service.geocode(cityName)
                    .groupBy {
                        Location(
                            name = it.name,
                            countryCode = it.countryCode,
                            state = it.state
                        )
                    }
                    .map { (_, entries) -> entries.first() }
                if (uniqueEntries.isEmpty()) {
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
        fetchWeatherData(Coordinates(61.4991, 23.7871), Location("Tampere", "FI", null))
    }

    fun resetGeocodeEntries() {
        _geocodeEntries.clear()
        _error = null
    }
}
