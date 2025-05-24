package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class NominatimResponse(
    val lat: String,
    val lon: String,
    val address: NominatimAddress,
)

data class NominatimAddress(
    val city: String?,
    val town: String?,
    val village: String?,
    val municipality: String?,
    val state: String?,
    val country: String,
) {
    val geoAddress: String
        get() = "${city ?: town ?: village ?: municipality ?: ""}, ${if (state != null) "$state, " else ""} $country"
}
