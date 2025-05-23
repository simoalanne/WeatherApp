package com.example.weatherapp.utils

import android.app.LocaleManager
import android.content.Context
import android.os.LocaleList
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationDisplayAccuracy
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration
import com.example.weatherapp.model.TempUnit

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
    languageCode: String = "en",
): String {
    val useFinnish = languageCode == "fi"
    val nameParts = (if (useFinnish) location.finnishName else location.englishName).split(", ")

    val (name, state, country) = when (nameParts.size) {
        3 -> Triple("${nameParts[0]},", "${nameParts[1]},", nameParts[2])
        2 -> Triple("${nameParts[0]},", "", nameParts[1])
        else -> Triple(nameParts[0], "", "")
    }

    return when (accuracy) {
        LocationDisplayAccuracy.CITY -> name
        LocationDisplayAccuracy.CITY_AND_COUNTRY -> "$name $country"
        LocationDisplayAccuracy.CITY_AND_STATE_AND_COUNTRY -> "$name $state $country"
    }
}

@Composable
fun rememberCurrentLanguageCode(): String {
    val config = LocalConfiguration.current
    return remember(config) {
        config.locales[0].language
    }
}

@Composable
fun rememberCurrentLocale(): Locale {
    val config = LocalConfiguration.current
    return remember(config) {
        config.locales[0]
    }
}

fun changeAppLanguage(context: Context, languageTag: String) {
    val localeManager = context.getSystemService(LocaleManager::class.java)
    val localeList = LocaleList.forLanguageTags(languageTag)
    localeManager.applicationLocales = localeList
}

fun getAppLanguage(context: Context): String {
    val localeManager = context.getSystemService(LocaleManager::class.java)
    val locales = localeManager.applicationLocales
    return locales.get(0)?.language ?: Locale.getDefault().language
}
