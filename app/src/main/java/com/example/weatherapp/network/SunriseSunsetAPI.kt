package com.example.weatherapp.network

import com.example.weatherapp.model.SunriseSunsetResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SunriseSunsetAPI {

    /**
     * Get sunrise and sunset times as Unix timestamps (UTC) for a range of dates.
     */
    @GET("json")
    suspend fun getSunriseSunsetRange(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("date_start") startDate: String,
        @Query("date_end") endDate: String,
        @Query("time_format") timeFormat: String = "unix",
        @Query("timezone") timezone: String = "UTC"
    ): SunriseSunsetResponse

    companion object {
        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.sunrisesunset.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: SunriseSunsetAPI = retrofit.create(SunriseSunsetAPI::class.java)
    }
}
