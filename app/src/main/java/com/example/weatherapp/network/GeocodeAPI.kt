package com.example.weatherapp.network

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.model.GeocodeResponseEntry
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeAPI {
    /**
     * Geocode a location name into geographic coordinates.
     */
    @GET("direct")
    suspend fun geocode(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = BuildConfig.GEOCODING_API_LIMIT.toInt(),
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    ): List<GeocodeResponseEntry>

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = BuildConfig.GEOCODING_API_LIMIT.toInt(),
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY
    ): List<GeocodeResponseEntry>

    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.GEOCODING_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: GeocodeAPI = retrofit.create(GeocodeAPI::class.java)
    }
}
