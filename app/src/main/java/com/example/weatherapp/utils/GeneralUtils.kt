package com.example.weatherapp.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.weatherapp.model.GeoSearchFilterMode
import com.example.weatherapp.model.GeocodeEntry
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
    location: GeocodeEntry,
    accuracy: GeoSearchFilterMode,
    locale: Locale
): String {

    val locationName = location.localizedNames?.get(locale.language.lowercase()) ?: location.name
    val country = getCountryNameFromCode(location.countryCode, locale)
    // Generally state just clutters the entry for most countries so it should be displayed
    // only for countries where the same location actually can exist in multiple states
    // for now this hardcoded list handles those countries
    val shouldDisplayState = location.countryCode in GeocodeEntry.alwaysDisplayState
    val state =
        if (location.state != null) {
            "${location.state},"
        } else {
            ""
        }
    return when (accuracy) {
        GeoSearchFilterMode.BEST_MATCH -> "$locationName, ${if (shouldDisplayState) state else ""} $country"
        GeoSearchFilterMode.MOST_RELEVANT -> "$locationName, $state $country"
        GeoSearchFilterMode.ALL_RESULTS -> "$locationName, $state $country"
    }
}

@Composable
fun getCurrentLocale(): Locale = LocalContext.current.resources.configuration.locales.get(0)

