package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.mapper.mapApiResponsesToWeatherData
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.SunriseSunsetAPI
import com.example.weatherapp.network.WeatherAPI
import com.example.weatherapp.utils.getLocalDateTimeFromUnixTimestamp
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private var _weather by mutableStateOf<WeatherData?>(null)
    private var _error by mutableStateOf<String?>(null)

    val weather: WeatherData? get() = _weather
    val error: String? get() = _error

    fun fetchWeatherData(city: String) {
        viewModelScope.launch {
            try {
                val weatherResponse = WeatherAPI.service.getWeatherByCityName(city)
                val forecastResponse = WeatherAPI.service.getForecastByCityName(city)
                val (lon, lat) = forecastResponse.cityInfo.coordinates


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
                    sunriseSunsetResponse
                )
                _weather = weatherData
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather", e)
                _error = "Something went wrong..."
            }
        }
    }

    init {
        // TODO: the initial fetched city should come from persistent storage
        fetchWeatherData("Tampere")
    }
}
