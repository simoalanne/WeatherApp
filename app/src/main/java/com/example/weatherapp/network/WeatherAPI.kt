package com.example.weatherapp.network

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.model.ForecastResponse
import com.example.weatherapp.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    /**
     * Get current weather by city name.
     */
    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    ): WeatherResponse

    /**
     * Get current weather by coordinates.
     */
    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    ): WeatherResponse

    /**
     * Get 5 day / 3 hour forecast by city name.
     */
    @GET("forecast")
    suspend fun getForecastByCityName(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    ): ForecastResponse

    /**
     * Get 5 day / 3 hour forecast by coordinates.
     */
    @GET("forecast")
    suspend fun getForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    ): ForecastResponse

    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: WeatherAPI = retrofit.create(WeatherAPI::class.java)
    }
}