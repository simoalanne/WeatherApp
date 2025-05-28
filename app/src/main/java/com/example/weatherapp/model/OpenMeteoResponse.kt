package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    @SerializedName("current_weather")
    val currentWeather: CurrentWeatherResponse,
    @SerializedName("hourly")
    val hourlyWeather: HourlyWeatherResponse,
    @SerializedName("daily")
    val dailyWeather: DailyWeatherResponse
)

data class CurrentWeatherResponse(
    val time: Long,
    val temperature: Double,
    @SerializedName("weathercode")
    val weatherCode: Int,
    @SerializedName("is_day")
    val isDay: Int
)

data class HourlyWeatherResponse(
    val time: List<Long>,
    @SerializedName("temperature_2m")
    val temperature: List<Double>,
    @SerializedName("weathercode")
    val weatherCode: List<Int>,
    @SerializedName("is_day")
    val isDay: List<Int>,
    @SerializedName("precipitation_probability")
    val pop: List<Int>
)

data class DailyWeatherResponse(
    val time: List<Long>,
    @SerializedName("weathercode")
    val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max")
    val maxTemperature: List<Double>,
    @SerializedName("temperature_2m_min")
    val minTemperature: List<Double>,
    @SerializedName("temperature_2m_mean")
    val meanTemperature: List<Double>,
    @SerializedName("sunrise")
    val sunrise: List<Long>,
    @SerializedName("sunset")
    val sunset: List<Long>
)
