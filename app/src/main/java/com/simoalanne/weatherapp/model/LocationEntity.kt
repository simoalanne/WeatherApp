package com.simoalanne.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database representation of [LocationData]. use extension functions for each to convert to and from
 */
@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val englishName: String,
    val finnishName: String,
    val lat: Double,
    val lon: Double,
    val countryCode: String,
)

fun LocationEntity.toLocationAndRole(): LocationAndRole {
    return LocationAndRole(
        LocationData(
        englishName = englishName,
        finnishName = finnishName,
        lat = lat,
        lon = lon,
        countryCode = countryCode,
        ),
        LocationRole.FAVORITE
    )
}
