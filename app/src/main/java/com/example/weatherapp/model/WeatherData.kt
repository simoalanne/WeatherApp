package com.example.weatherapp.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Data class representing the weather data the UI will display.
 *
 * @param meta The metadata for the weather data.
 * @param current The current weather data.
 * @param dailyForecasts The daily weather data.
 */
data class WeatherData(
    val meta: Meta,
    val current: CurrentWeather,
    val dailyForecasts: List<DailyWeather>
)

/**
 * Meta data of the weather data. Could expand later to include more information which is why
 * it's a separate class.
 */
data class Meta(
    val utcOffsetSeconds: Int,
)

/**
 * Data class representing the current weather data.
 */
data class CurrentWeather(
    val time: LocalDateTime,
    val temperature: Double,
    val weatherIconId: Int,
    val conditionId: Int,
    val isDay: Boolean,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
    val weatherCode: Int
)

/**
 * Data class representing the daily weather data.
 */
data class DailyWeather(
    val date: LocalDate,
    val weatherIconId: Int,
    val maxTemperature: Double,
    val minTemperature: Double,
    val meanTemperature: Double,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
    val hourlyWeathers: List<HourlyWeather>
)

/**
 * Data class representing the hourly weather data.
 *
 * @param time The time of the weather data.
 * @param temperature The temperature of the weather data.
 * @param weatherIconId The resource ID of the weather icon.
 * @param pop The Probability of Precipitation (change of rain). Percentage.
 * @param windGusts The wind gusts of the weather data. Should be in meters per second.
 * @param windDirection The wind direction of the weather data. Should be in degrees.
 * @param humidity The humidity of the weather data. Percentage.
 * @param feelsLike The feels like temperature of the weather data.
 *
 */
data class HourlyWeather(
    val time: LocalDateTime,
    val temperature: Double,
    val weatherIconId: Int,
    val pop: Int,
    val windGusts: Double,
    val windDirection: Int,
    val humidity: Int,
    val feelsLike: Double
)
