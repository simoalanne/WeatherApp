package com.example.weatherapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val englishName: String,
    val finnishName: String?,
    val lat: Double,
    val lon: Double,
    val countryCode: String,
    val state: String?
)

fun LocationEntity.toLocationAndRole(): LocationAndRole {
    return LocationAndRole(
        LocationData(
        englishName = englishName,
        finnishName = finnishName,
        lat = lat,
        lon = lon,
        countryCode = countryCode,
        state = state
        ),
        LocationRole.FAVORITE
    )
}
