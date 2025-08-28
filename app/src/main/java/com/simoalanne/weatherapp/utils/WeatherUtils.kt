package com.simoalanne.weatherapp.utils

import com.simoalanne.weatherapp.mapper.toWeatherData
import com.simoalanne.weatherapp.model.Coordinates
import com.simoalanne.weatherapp.model.HourlyWeather
import com.simoalanne.weatherapp.model.TempUnit
import com.simoalanne.weatherapp.model.WeatherData
import com.simoalanne.weatherapp.model.WindSpeedUnit
import com.simoalanne.weatherapp.network.WeatherAPI
import com.simoalanne.weatherapp.viewmodel.AppPreferences
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

fun formatWindSpeed(windSpeed: Double) =
    when (AppPreferences.preferences.windSpeedUnit) {
        WindSpeedUnit.METERS_PER_SECOND -> windSpeed.roundToInt()
        WindSpeedUnit.KILOMETERS_PER_HOUR -> (windSpeed * 3.6).roundToInt()
        WindSpeedUnit.MILES_PER_HOUR -> (windSpeed * 2.237).roundToInt()
    }