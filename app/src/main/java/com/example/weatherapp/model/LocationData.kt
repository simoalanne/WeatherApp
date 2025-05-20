package com.example.weatherapp.model

/**
 * Data class representing the location data in both English and Finnish. Custom hashCode and equals
 * that ignore the lat and lon because a city can have multiple geocodes with slightly different
 * lat and lon
 *
 * @param englishName "City, State?, Country" in English
 * @param finnishName "City, State?, Country" in Finnish
 * @param lat The latitude of the location.
 * @param lon The longitude of the location.
 * @param countryCode The country code of the location. Used to fetch the flag image.
 */
data class LocationData(
    val englishName: String,
    val finnishName: String,
    val lat: Double,
    val lon: Double,
    val countryCode: String
) {
    override fun hashCode(): Int {
        return listOf(englishName, countryCode).hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocationData) return false
        return countryCode == other.countryCode && englishName == other.englishName
    }
}

fun LocationData.toLocationEntity(): LocationEntity {
    return LocationEntity(
        englishName = englishName,
        finnishName = finnishName,
        lat = lat,
        lon = lon,
        countryCode = countryCode
    )
}
