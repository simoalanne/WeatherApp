package com.example.weatherapp.model

import java.time.LocalDateTime

/**
 * Data class representing the weather data, containing metadata about the location, the current
 * weather, and a list of hourly forecasts.
 */
data class WeatherData(
    val meta: Meta,
    val current: CurrentWeather,
    val hourlyForecasts: List<HourlyWeather>,
)

data class Meta(
    val timezoneOffsetInSeconds: Int,
    val sunriseSunsetTimes: Map<String, SunriseSunset> // key should be toString() of LocalDate

)

data class CurrentWeather(
    val time: LocalDateTime,
    val temperature: Double,
    val feelsLike: Double,
    val conditionId: Int,
    val iconId: Int,
    val humidityPercentage: Int,
    val windSpeed: Double,
    val airPressure: Int,
    val cloudinessPercentage: Int,
    val visibilityInMeters: Int,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime
)

data class HourlyWeather(
    val time: LocalDateTime,
    val temperature: Double,
    val iconId: Int,
    val rainProbability: Int
)

data class SunriseSunset(
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime
)
