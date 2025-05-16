package com.example.weatherapp.utils

import androidx.compose.runtime.Composable
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationDisplayAccuracy
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration

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

private fun getCountryNameFromCode(countryCode: String, locale: Locale): String =
    Locale("", countryCode).getDisplayCountry(locale)


/**
 * Formats a location object into more user friendly text.
 *
 * @param location the location object to format
 * @param accuracy what level of accuracy to use when formatting the location
 * @return a formatted String
 */
fun formatLocationName(
    location: LocationData,
    accuracy: LocationDisplayAccuracy = LocationDisplayAccuracy.CITY_AND_COUNTRY,
    locale: Locale
): String {

    val locationName = (if (locale.language.lowercase() == "fi") location.finnishName else location.englishName)
        ?: location.englishName
    val country = getCountryNameFromCode(location.countryCode, locale)
    val state = if (location.state != null) "${location.state}," else ""
    return when (accuracy) {
        LocationDisplayAccuracy.CITY -> locationName
        LocationDisplayAccuracy.CITY_AND_COUNTRY -> "$locationName, $country"
        LocationDisplayAccuracy.CITY_AND_STATE_AND_COUNTRY -> "$locationName, $state $country"
    }
}

@Composable
fun getCurrentLocale(): Locale = LocalConfiguration.current.locales.get(0)
