package com.example.weatherapp.utils

import com.example.weatherapp.mapper.toWeatherData
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.WeatherAPI
import com.example.weatherapp.viewmodel.AppPreferences
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

fun formatTemp(temp: Double, addUnit: Boolean = true): String {
    val tempUnit = AppPreferences.preferences.tempUnit
    val tempSymbol = if (addUnit) {
        when (tempUnit) {
            TempUnit.CELSIUS -> "°"
            TempUnit.FAHRENHEIT -> "°"
            TempUnit.KELVIN -> "K"
        }
    } else ""

    val convertedTemp = convertTemperature(temp, tempUnit)
    return "${convertedTemp.roundToInt()}$tempSymbol"
}

suspend fun fetchWeatherDataForCoordinates(coordinates: Coordinates): WeatherData? {
    val response = try {
        WeatherAPI.service.getWeatherByCoordinates(
            coordinates.lat,
            coordinates.lon
        )
    } catch (_: Exception) {
        return null
    }
    return response.toWeatherData()
}

/**
 * Converts a temperature from celsius to fahrenheit or kelvin. the starting unit should always
 * be celsius.
 *
 * @param temperature the temperature to convert
 * @param unit the unit to convert to fahrenheit or kelvin
 */
fun convertTemperature(temperature: Double, unit: TempUnit): Double {
    return when (unit) {
        TempUnit.CELSIUS -> temperature // Already in celsius
        TempUnit.FAHRENHEIT -> (temperature * 9 / 5) + 32
        TempUnit.KELVIN -> temperature + 273.15

    }
}