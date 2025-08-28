package com.simoalanne.weatherapp.model

import com.google.gson.annotations.SerializedName
import kotlin.math.round

/**
 * Data class representing the response from the Open-Meteo API.
 */
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

/**
 * Data class representing the current weather field.
 */
data class CurrentWeatherResponse(
    val time: Long,
    val temperature: Double,
    @SerializedName("weathercode")
    val weatherCode: Int,
    @SerializedName("is_day")
    val isDay: Int,
)

/**
 * Data class representing the hourly weather field.
 */
data class HourlyWeatherResponse(
    val time: List<Long>,
    @SerializedName("temperature_2m")
    val temperature: List<Double>,
    @SerializedName("weathercode")
    val weatherCode: List<Int>,
    @SerializedName("is_day")
    val isDay: List<Int>,
    @SerializedName("precipitation_probability")
    val pop: List<Int>,
    @SerializedName("wind_gusts_10m")
    val windGusts: List<Double>,
    @SerializedName("wind_direction_10m")
    val windDirection: List<Int>,
    @SerializedName("relative_humidity_2m")
    val humidity: List<Int>,
    @SerializedName("apparent_temperature")
    val feelsLike: List<Double>
) {
    val flippedWindDirection: List<Int>
        get() = windDirection.map {
            val flipped = (it + 180) % 360 // the wind direction is changed from "from" to "to" because most weather apps seem to do this
            val rounded = round(flipped / 45.0) * 45 // round so it matches a compass direction
            rounded.toInt()
        }
}

/**
 * Data class representing the daily weather field.
 */
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
