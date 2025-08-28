package com.simoalanne.weatherapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.simoalanne.weatherapp.model.WeatherEntity

/**
 * Data Access Object for the [WeatherEntity] class. contains methods for:
 * - getting weather for a location key
 * - getting all weather
 * - adding weather
 * - deleting weather
 * - deleting all weather
 */
@Dao
interface WeatherDao {
    /**
     * Get weather for a location key from the database.
     *
     * @param key The location key (englishName,countryCode).
     * @return The weather for the location key or null if not found.
     */
    @Query("SELECT * FROM weather WHERE locationKey = :key")
    suspend fun getWeather(key: String): WeatherEntity?

    /**
     * Get all weather from the database.
     *
     * @return A list of all weather.
     */
    @Query("SELECT * FROM weather")
    suspend fun getAllWeather(): List<WeatherEntity>

    /**
     * Upsert (Update or Insert) weather into the database.
     *
     * @param entity The weather to add.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeather(entity: WeatherEntity)

    /**
     * Delete weather for a location key from the database.
     *
     * @param key The location key (englishName,countryCode).
     */
    @Query("DELETE FROM weather WHERE locationKey = :key")
    suspend fun deleteWeather(key: String)

    @Query("DELETE FROM weather")
    suspend fun deleteAllWeather()
}
