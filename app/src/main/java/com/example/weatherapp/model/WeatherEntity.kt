package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherapp.GsonProvider

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey val locationKey: String, // englishName,countryCode
    val json: String,
    val timestamp: Long
)

fun WeatherEntity.toWeather(): WeatherData {
    return GsonProvider.gson.fromJson(json, WeatherData::class.java)
}
