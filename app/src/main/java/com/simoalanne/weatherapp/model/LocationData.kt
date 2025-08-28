package com.simoalanne.weatherapp.model

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

    // the reason the default method isn't good enough is because the geocoding API(s) both support
    // geocoding down to address level but as this app only considers city level geocoding then obviously
    // addresses in the same city should be considered equal which won't be the case if lat and lon
    // is considered in equals check
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LocationData) return false
        return countryCode == other.countryCode && englishName == other.englishName
    }
}

// The existing room entity can't really be used in viewmodel state because it has the auto generated id
// and probably it's not a good idea to use entities directly in view models and rather just copy the data class
fun LocationData.toLocationEntity(): LocationEntity {
    return LocationEntity(
        englishName = englishName,
        finnishName = finnishName,
        lat = lat,
        lon = lon,
        countryCode = countryCode
    )
}
