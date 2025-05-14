package com.example.weatherapp.model

import android.util.Log
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
)

/**
 * This is essentially the same data class as GeocodeResponseEntry but with customized
 * equals and hashCode methods. the raw API data class should not be customized so that's why
 * this is separate.
 */
data class GeocodeEntry(
    val name: String,
    val lat: Double,
    val lon: Double,
    val countryCode: String,
    val state: String?,
    val localizedNames: Map<String, String>?
) {

    // normalize both the name and the localized aliases and convert to set for fast lookup
    // in the equals method
    private val allNames = (listOf(name) + (localizedNames?.values ?: emptyList()))
        .map { it.trim().lowercase() }
        .toSet()

    /**
     * Customized equals method that considers two entries equal if they have:
     * - the same country code
     * - the same state (if state is in the data)
     * - their names or localized names have any intersection
     *
     * This is just the baseline and more filtering can be done on the UI level.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is GeocodeEntry) return false

        if (countryCode != other.countryCode) return false

        if (state != other.state) return false

        if (allNames.intersect(other.allNames).isEmpty()) {
            Log.d("names: ", "$name, ${other.name} are not equal")
            Log.d("DEBUG", "other.allNames = ${other.allNames} this.allNames = ${this.allNames}")
            return false
        }

        return true
    }

    /**
     * Customized hashCode method that hashes the country code and state.
     */
    override fun hashCode(): Int {
        return listOf(countryCode, state).hashCode()
    }

    companion object {
        val alwaysDisplayState = listOf("US")
    }
}


enum class GeoSearchFilterMode {
    BEST_MATCH,
    MOST_RELEVANT,
    ALL_RESULTS
}
