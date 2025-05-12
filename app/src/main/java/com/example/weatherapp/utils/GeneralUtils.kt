package com.example.weatherapp.utils

import com.example.weatherapp.model.Accuracy
import com.example.weatherapp.model.Location
import java.util.Locale

/**
 * Returns the interpolation weights for a given step. For example:
 * getInterpolationWeights(1, 3) = (0.33, 0.67)
 *
 * @param currentStep the current step of the interpolation
 * @param gapBetween the difference between start and end values. e.g hours 15:00 - 12:00 = 3
 * @return Pair of weights (startWeight, endWeight) summing to 1.0
 */
fun getInterpolationWeights(currentStep: Int, gapBetween: Int): Pair<Double, Double> {
    val fraction = currentStep.toDouble() / gapBetween.toDouble()
    val endWeight = fraction
    val startWeight = 1.0 - fraction
    return Pair(startWeight, endWeight)
}

private fun getCountryNameFromCode(countryCode: String): String =
    Locale("", countryCode).getDisplayCountry(Locale.ENGLISH)


/**
 * Formats a location object into more user friendly text.
 *
 * @param location the location object to format
 * @param accuracy what level of accuracy to use when formatting the location
 * @return a formatted String
 */
fun formatLocationName(
    location: Location,
    accuracy: Accuracy = Accuracy.LOCATION_AND_COUNTRY
): String {
    val country = getCountryNameFromCode(location.countryCode)
    return when (accuracy) {
        Accuracy.LOCATION -> location.name
        Accuracy.LOCATION_AND_COUNTRY -> "${location.name}, $country"
        Accuracy.LOCATION_AND_STATE_AND_COUNTRY -> "${location.name}, ${location.state?.let { "$it, " } ?: ""} $country"
    }
}
