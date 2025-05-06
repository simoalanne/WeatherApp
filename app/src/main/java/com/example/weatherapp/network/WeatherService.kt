package com.example.weatherapp.network

import com.example.weatherapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

/**
 * Retrofit client for OpenWeatherMap API.
 *
 * In order for this to work you must add into local.properties the weather api base url.
 * This url can be either a direct connection to OpenWeatherMap or a proxy forwarding the
 * request to OpenWeatherMap.
 *
 * If securing the API key is not important you can add following line to local.properties:
 * WEATHER_API_BASE_URL="https://api.openweathermap.org/data/2.5/appid="your_api_key"
 *
 */
interface WeatherService {
    @GET("weather")
    suspend fun getWeatherByCityName(@Query("q") city: String): Any

    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Any

    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: WeatherService = retrofit.create(WeatherService::class.java)
    }
}
