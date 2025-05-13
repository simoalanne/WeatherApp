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
    val iconResNightId: Int,
    val conditionMap: Map<Int, Int>
) {
    Thunderstorm(200..232, R.drawable.thunderstorm, R.drawable.thunderstorm, mapOf(
        200 to R.string.weather_condition_thunderstorm_light_rain,
        201 to R.string.weather_condition_thunderstorm_rain,
        202 to R.string.weather_condition_thunderstorm_heavy_rain,
        210 to R.string.weather_condition_light_thunderstorm,
        211 to R.string.weather_condition_thunderstorm,
        212 to R.string.weather_condition_heavy_thunderstorm,
        221 to R.string.weather_condition_ragged_thunderstorm,
        230 to R.string.weather_condition_thunderstorm_light_drizzle,
        231 to R.string.weather_condition_thunderstorm_drizzle,
        232 to R.string.weather_condition_thunderstorm_heavy_drizzle
    )),
    Drizzle(300..321, R.drawable.drizzle, R.drawable.drizzle_night, mapOf(
        300 to R.string.weather_condition_light_intensity_drizzle,
        301 to R.string.weather_condition_drizzle,
        302 to R.string.weather_condition_heavy_intensity_drizzle,
        310 to R.string.weather_condition_light_intensity_drizzle_rain,
        311 to R.string.weather_condition_drizzle_rain,
        312 to R.string.weather_condition_heavy_intensity_drizzle_rain,
        313 to R.string.weather_condition_shower_rain_and_drizzle,
        314 to R.string.weather_condition_heavy_shower_rain_and_drizzle,
        321 to R.string.weather_condition_shower_drizzle
    )),
    Rain(500..531, R.drawable.rain, R.drawable.rain, mapOf(
    500 to R.string.weather_condition_light_rain,
    501 to R.string.weather_condition_moderate_rain,
    502 to R.string.weather_condition_heavy_intensity_rain,
    503 to R.string.weather_condition_very_heavy_rain,
    504 to R.string.weather_condition_extreme_rain,
    511 to R.string.weather_condition_freezing_rain,
    520 to R.string.weather_condition_light_intensity_shower_rain,
    521 to R.string.weather_condition_shower_rain,
    522 to R.string.weather_condition_heavy_intensity_shower_rain,
    531 to R.string.weather_condition_ragged_shower_rain
    )),
    Snow(600..622, R.drawable.snow, R.drawable.snow, mapOf(
        600 to R.string.weather_condition_light_snow,
        601 to R.string.weather_condition_snow,
        602 to R.string.weather_condition_heavy_snow,
        611 to R.string.weather_condition_sleet,
        612 to R.string.weather_condition_light_shower_sleet,
        613 to R.string.weather_condition_shower_sleet,
        615 to R.string.weather_condition_light_rain_and_snow,
        616 to R.string.weather_condition_rain_and_snow,
        620 to R.string.weather_condition_light_shower_snow,
        621 to R.string.weather_condition_shower_snow,
        622 to R.string.weather_condition_heavy_shower_snow
    )),
    Clear(800..800, R.drawable.sun, R.drawable.moon, mapOf(
        800 to R.string.weather_condition_clear_sky
    )),
    MostlyClear(801..802, R.drawable.few_clouds_day, R.drawable.few_clouds_night,
        mapOf(
            801 to R.string.weather_condition_few_clouds,
            802 to R.string.weather_condition_scattered_clouds
        )
    ),
    Clouds(802..804, R.drawable.clouds, R.drawable.clouds_night,
        mapOf(
            803 to R.string.weather_condition_broken_clouds,
            804 to R.string.weather_condition_overcast_clouds
        )
    );

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

        fun getConditionString(code: Int): Int {
            val entry = entries.find { code in it.codeRange }
            return entry?.conditionMap?.get(code) ?: R.string.weather_condition_unknown_condition
        }
    }
}
