package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.GsonProvider

/**
 * Entity representing a weather data in the database.
 *
 * @param locationKey The key of the location (englishName,countryCode).
 * @param json The JSON representation of the weather data.
 */
@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val locationKey: String,
    val json: String
)

fun WeatherEntity.toWeather(): WeatherData {
    return GsonProvider.gson.fromJson(json, WeatherData::class.java)
}
