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
)

/**
 * This is essentially the same data class as GeocodeResponseEntry but with customized
 * equals and hashCode methods. Because how Gson creates objects it skips the default
 * constructor which then means private variables will be set to null which
 * then in turn causes issues later on. Additionally usually the raw data class for the API
 * should not be used in the UI
 */
data class GeocodeEntry(
    val name: String,
    val lat: Double,
    val lon: Double,
    val countryCode: String,
    val state: String?,
    val localizedNames: Map<String, String>?
) {
    private val normalizedName = name.trim().lowercase()
    private val normalizedLocalizedNames =
        localizedNames?.map { it.value.trim().lowercase() }?.toSet() ?: emptySet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GeocodeEntry) return false

        if (normalizedName != other.normalizedName) return false
        if (normalizedName !in other.normalizedLocalizedNames) return false
        if (countryCode != other.countryCode) return false
        if (countryCode in stateMatters && state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        return if (countryCode in stateMatters) {
            listOf(normalizedName, countryCode, state).hashCode()
        } else {
            listOf(normalizedName, countryCode).hashCode()
        }
    }

    companion object {
        val stateMatters = listOf("US", "CA", "AU")
    }
}



enum class Accuracy {
    LOCATION,
    LOCATION_AND_COUNTRY,
    LOCATION_AND_STATE_AND_COUNTRY
}