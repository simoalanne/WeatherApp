package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

data class GeocodeResponseEntry(
    val name: String,
    val lat: Double,
    val lon: Double,
    @SerializedName("country")
    val countryCode: String,
    val state: String?,
    @SerializedName("local_names")
    val localizedNames: Map<String, String>?
) {

    private fun normalizedNames(): Set<String> {
        return (listOf(name) + (localizedNames?.values ?: emptyList()))
            .map { it.trim().lowercase() }
            .toSet()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GeocodeResponseEntry) return false

        if (countryCode != other.countryCode) return false
        if (state != other.state) return false

        if (normalizedNames().intersect(other.normalizedNames()).isEmpty()) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return listOf(countryCode, state).hashCode()
    }
}

fun GeocodeResponseEntry.toLocationData() =
    LocationData(
        englishName = name,
        finnishName = localizedNames?.get("fi"),
        lat = lat,
        lon = lon,
        countryCode = countryCode,
        state = state
    )

enum class GeoSearchFilterMode {
    BEST_MATCH,
    MOST_RELEVANT,
    ALL_RESULTS
}
