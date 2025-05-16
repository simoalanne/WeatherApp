package com.example.weatherapp.utils

import com.example.weatherapp.mapper.mapApiResponsesToWeatherData
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.SunriseSunsetAPI
import com.example.weatherapp.network.WeatherAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate

suspend fun fetchWeatherDataForCoordinates(coords: Coordinates): WeatherData = coroutineScope {
    val (lat, lon) = coords

    val weatherDeferred = async {
        WeatherAPI.service.getWeatherByCoordinates(lat, lon)
    }

    val forecastDeferred = async {
        WeatherAPI.service.getForecastByCoordinates(lat, lon)
    }

    val startDate = LocalDate.now().minusDays(1).toString()
    val endDate = LocalDate.now().plusDays(5).toString()

    val sunriseSunsetDeferred = async {
        SunriseSunsetAPI.service.getSunriseSunsetRange(lat, lon, startDate, endDate)
    }

    val weatherResponse = weatherDeferred.await()
    val forecastResponse = forecastDeferred.await()
    val sunriseSunsetResponse = sunriseSunsetDeferred.await()

    return@coroutineScope mapApiResponsesToWeatherData(
        weatherResponse,
        forecastResponse,
        sunriseSunsetResponse
    )
}
