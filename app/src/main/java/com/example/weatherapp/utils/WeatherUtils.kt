package com.example.weatherapp.utils

import com.example.weatherapp.mapper.toWeatherData
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.WeatherAPI
import kotlin.math.roundToInt

fun getDominantIcon(iconIds: List<Int>): Int {
    return iconIds.groupingBy { it }
        .eachCount()
        .maxBy { it.value }
        .key
}

fun getMinMaxTemperature(dayForecasts: List<HourlyWeather>): Pair<Double, Double> {
    val minTemp = dayForecasts.minOf { it.temperature }
    val maxTemp = dayForecasts.maxOf { it.temperature }
    return Pair(minTemp, maxTemp)
}

fun formatTemp(temp: Double, round: Boolean = true, addSymbol: Boolean = true) =
    "${if (round) temp.roundToInt() else temp}${if (addSymbol) "°C" else ""}"

suspend fun fetchWeatherDataForCoordinates(coordinates: Coordinates): WeatherData {
    val response = WeatherAPI.service.getWeatherByCoordinates(
        coordinates.lat,
        coordinates.lon
    )
    return response.toWeatherData()
}
