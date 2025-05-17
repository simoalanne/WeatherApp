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
        @Query("hourly") hourly: String = "temperature_2m,weathercode,is_day",
        @Query("daily") daily: String = "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset",
        @Query("timeformat") timeformat: String = "unixtime",
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponse


    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: WeatherAPI = retrofit.create(WeatherAPI::class.java)
    }
}

