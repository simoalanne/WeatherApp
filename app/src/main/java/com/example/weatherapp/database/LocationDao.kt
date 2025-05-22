package com.example.weatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.model.LocationEntity

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    suspend fun getAll(): List<LocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(location: LocationEntity)

    @Query("DELETE FROM locations WHERE englishName = :englishName AND countryCode = :countryCode")
    suspend fun delete(englishName: String, countryCode: String)
}
