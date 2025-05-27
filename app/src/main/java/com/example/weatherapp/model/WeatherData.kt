package com.example.weatherapp.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class WeatherData(
    val meta: Meta,
    val current: CurrentWeather,
    val dailyForecasts: List<DailyWeather>
)

data class Meta(
    val utcOffsetSeconds: Int,
)

data class CurrentWeather(
    val time: LocalDateTime,
    val temperature: Double,
    val weatherIconId: Int,
    val conditionId: Int,
    val isDay: Boolean,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime
)

data class DailyWeather(
    val date: LocalDate,
    val weatherIconId: Int,
    val maxTemperature: Double,
    val minTemperature: Double,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime,
    val hourlyWeathers: List<HourlyWeather>
)

data class HourlyWeather(
    val time: LocalDateTime,
    val temperature: Double,
    val weatherIconId: Int,
    val pop: Int
)
