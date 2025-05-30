package com.example.weatherapp.model

/**
 * Data class representing the response from the Nominatim API.
 */
data class NominatimResponse(
    val lat: String,
    val lon: String,
    val address: NominatimAddress,
)

/**
 * Data class representing the address field from the Nominatim API.
 */
data class NominatimAddress(
    val city: String?,
    val town: String?,
    val village: String?,
    val municipality: String?,
    val state: String?,
    val country: String,
) {
    // this is what's passed to Geocoder forward geocoding. not the most idiomatic way to do geocoding
    // but good enough for this app and works surprisingly well.
    val geoAddress: String
        get() = "${city ?: town ?: village ?: municipality ?: ""}, ${if (state != null) "$state, " else ""} $country"
}
