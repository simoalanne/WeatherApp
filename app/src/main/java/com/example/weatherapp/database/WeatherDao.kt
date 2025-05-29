package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.WeatherEntity

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE locationKey = :key")
    suspend fun getWeather(key: String): WeatherEntity?

    @Query("SELECT * FROM weather")
    suspend fun getAllWeather(): List<WeatherEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeather(entity: WeatherEntity)

    @Query("DELETE FROM weather WHERE locationKey = :key")
    suspend fun deleteWeather(key: String)
}
