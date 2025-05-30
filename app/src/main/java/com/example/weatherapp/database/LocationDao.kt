package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.LocationEntity

/**
 * Data Access Object for the [LocationEntity] class. contains methods for:
 * - getting all locations
 * - adding a location
 * - deleting a location
 * - deleting all locations
 */
@Dao
interface LocationDao {
    /**
     * Get all locations from the database.
     */
    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<LocationEntity>

    /**
     * Adds a location to the database.
     *
     * @param location The location to add.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(location: LocationEntity)

    /**
     * Deletes a location from the database.
     *
     * @param englishName The english name of the location to delete.
     * @param countryCode The country code of the location to delete.
     */
    @Query("DELETE FROM locations WHERE englishName = :englishName AND countryCode = :countryCode")
    suspend fun delete(englishName: String, countryCode: String)

    /**
     * Deletes all locations from the database. This should only be called if there is a issue with
     * the database data.
     */
    @Query("DELETE FROM locations")
    suspend fun deleteAll()
}
