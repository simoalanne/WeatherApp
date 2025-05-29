package com.example.weatherapp.network

import com.example.weatherapp.model.OpenMeteoResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("v1/forecast")
    suspend fun getWeatherByCoordinates(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,weathercode,precipitation_probability,is_day,wind_gusts_10m,relative_humidity_2m,apparent_temperature,wind_direction_10m",
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min,temperature_2m_mean,sunrise,sunset",
        @Query("timeformat") timeformat: String = "unixtime",
        @Query("past_days") pastDays: Int = 1,
        @Query("timezone") timezone: String = "auto",
        @Query("windspeed_unit") windspeedUnit: String = "ms"
    ): OpenMeteoResponse


    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: WeatherAPI = retrofit.create(WeatherAPI::class.java)
    }
}

