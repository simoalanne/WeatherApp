package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the response from https://api.openweathermap.org/data/2.5/weather/
 */
data class WeatherResponse(
    @SerializedName("weather")
    val weatherCondition: List<WeatherCondition>,
    @SerializedName("main")
    val weatherInfo: WeatherInfo,
    val visibility: Int,
    val wind: Wind,
    val rain: RainHourly?,
    val snow: SnowHourly?,
    val clouds: Clouds,
    @SerializedName("dt")
    val timestamp: Long,
)

/**
 * Data class representing the response from https://api.openweathermap.org/data/2.5/forecast/
 */
data class ForecastResponse(
    @SerializedName("list")
    val forecastList: List<Forecast>,
    @SerializedName("city")
    val cityInfo: CityInfo
)

data class Forecast(
    @SerializedName("dt")
    val timestamp: Long,
    @SerializedName("main")
    val weatherInfo: WeatherInfo,
    @SerializedName("weather")
    val weatherCondition: List<WeatherCondition>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    @SerializedName("pop")
    val rainProbability: Double,
    val rain: RainThreeHours?,
    val snow: SnowThreeHours?
)

data class CityInfo(
    val name: String,
    @SerializedName("coord")
    val coordinates: Coordinates,
    @SerializedName("country")
    val countryCode: String,
    val population: Int,
    val timezone: Int
)

data class Coordinates(
    val lon: Double,
    val lat: Double
)

data class WeatherCondition(
    @SerializedName("main")
    val condition: String,
    val description: String,
    @SerializedName("id")
    val code: Int
)

data class WeatherInfo(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("pressure")
    val airPressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Double,
    @SerializedName("deg")
    val direction: Int
)

data class RainHourly(
    @SerializedName("1h")
    val rainfallAmount: Double
)

data class SnowHourly(
    @SerializedName("1h")
    val snowfallAmount: Double
)

data class RainThreeHours(
    @SerializedName("3h")
    val rainfallAmount: Double
)

data class SnowThreeHours(
    @SerializedName("3h")
    val snowfallAmount: Double
)

data class Clouds(
    @SerializedName("all")
    val cloudinessPercentage: Int
)
