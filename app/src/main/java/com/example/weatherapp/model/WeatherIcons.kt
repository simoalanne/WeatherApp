package com.example.weatherapp.model

import android.util.Log
import com.example.weatherapp.R

/**
 * Enum class for custom weather icons replacing the default ones from OpenWeatherMap.
 * Some weather conditions have different icon for day and night. The icons are resolved
 * from the weather code in the OpenWeatherMap API response.
 */
enum class WeatherIcons(
    val codeRange: IntRange,
    val iconResDayId: Int,
    val iconResNightId: Int
) {
    Thunderstorm(200..232, R.drawable.thunderstorm, R.drawable.thunderstorm),
    Drizzle(300..321, R.drawable.drizzle, R.drawable.drizzle_night),
    Rain(500..531, R.drawable.rain, R.drawable.rain),
    Snow(600..622, R.drawable.snow, R.drawable.snow),
    Clear(800..800, R.drawable.sun, R.drawable.moon),
    MostlyClear(801..802, R.drawable.few_clouds_day, R.drawable.few_clouds_night),
    Clouds(802..804, R.drawable.clouds, R.drawable.clouds_night);

    companion object {
        fun fromCode(code: Int, isDay: Boolean): Int {
            val entry = entries.find { code in it.codeRange }
            if (entry == null) {
                Log.e("WeatherIcons", "No matching icon found for code: $code")
                return if (isDay) R.drawable.sun else R.drawable.moon
            }
            return if (isDay) entry.iconResDayId else entry.iconResNightId
        }

        fun getDailyVariantFromIcon(iconId: Int): Int {
            val entry = entries.find { it.iconResDayId == iconId || it.iconResNightId == iconId }
            return entry?.iconResDayId ?: iconId
        }
    }
}
