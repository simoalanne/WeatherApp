package com.example.weatherapp.model

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.example.weatherapp.R

/**
 * Enum class for custom weather icons replacing the default ones from OpenWeatherMap.
 * Some weather conditions have different icon for day and night. The icons are resolved
 * from the weather code in the OpenWeatherMap API response.
 */
enum class OpenMeteoCodes(
    val codes: Set<Int>,
    val iconResDayId: Int,
    val iconResNightId: Int,
    val conditionMap: Map<Int, Int>
) {
    ClearSky(
        codes = setOf(0),
        iconResDayId = R.drawable.sun,
        iconResNightId = R.drawable.moon,
        conditionMap = mapOf(
            0 to R.string.weather_condition_clear_sky
        )
    ),
    MostlyClear(
        codes = setOf(1, 2),
        iconResDayId = R.drawable.few_clouds_day,
        iconResNightId = R.drawable.few_clouds_night,
        conditionMap = mapOf(
            1 to R.string.weather_condition_mainly_clear,
            2 to R.string.weather_condition_partly_cloudy,
        )
    ),
    Cloudy(
        codes = setOf(3),
        iconResDayId = R.drawable.clouds,
        iconResNightId = R.drawable.clouds_night,
        conditionMap = mapOf(
            3 to R.string.weather_condition_overcast
        )
    ),
    Fog(
        codes = setOf(45, 48),
        iconResDayId = R.drawable.fog,
        iconResNightId = R.drawable.fog,
        conditionMap = mapOf(
            45 to R.string.weather_condition_fog,
            48 to R.string.weather_condition_depositing_rime_fog
        )
    ),
    Drizzle(
        codes = setOf(51, 53, 55),
        iconResDayId = R.drawable.drizzle,
        iconResNightId = R.drawable.drizzle_night,
        conditionMap = mapOf(
            51 to R.string.weather_condition_light_drizzle,
            53 to R.string.weather_condition_moderate_drizzle,
            55 to R.string.weather_condition_dense_drizzle
        )
    ),
    FreezingDrizzle(
        codes = setOf(56, 57),
        iconResDayId = R.drawable.drizzle,
        iconResNightId = R.drawable.drizzle_night,
        conditionMap = mapOf(
            56 to R.string.weather_condition_light_freezing_drizzle,
            57 to R.string.weather_condition_dense_freezing_drizzle
        )
    ),
    Rain(
        codes = setOf(61, 63, 65),
        iconResDayId = R.drawable.rain,
        iconResNightId = R.drawable.rain,
        conditionMap = mapOf(
            61 to R.string.weather_condition_slight_rain,
            63 to R.string.weather_condition_moderate_rain,
            65 to R.string.weather_condition_heavy_rain
        )
    ),
    FreezingRain(
        codes = setOf(66, 67),
        iconResDayId = R.drawable.rain,
        iconResNightId = R.drawable.rain,
        conditionMap = mapOf(
            66 to R.string.weather_condition_light_freezing_rain,
            67 to R.string.weather_condition_heavy_freezing_rain
        )
    ),
    SnowFall(
        codes = setOf(71, 73, 75),
        iconResDayId = R.drawable.snow,
        iconResNightId = R.drawable.snow,
        conditionMap = mapOf(
            71 to R.string.weather_condition_slight_snowfall,
            73 to R.string.weather_condition_moderate_snowfall,
            75 to R.string.weather_condition_heavy_snowfall
        )
    ),
    SnowGrains(
        codes = setOf(77),
        iconResDayId = R.drawable.snow,
        iconResNightId = R.drawable.snow,
        conditionMap = mapOf(
            77 to R.string.weather_condition_snow_grains
        )
    ),
    RainShowers(
        codes = setOf(80, 81, 82),
        iconResDayId = R.drawable.rain,
        iconResNightId = R.drawable.rain,
        conditionMap = mapOf(
            80 to R.string.weather_condition_slight_rain_showers,
            81 to R.string.weather_condition_moderate_rain_showers,
            82 to R.string.weather_condition_violent_rain_showers
        )
    ),
    SnowShowers(
        codes = setOf(85, 86),
        iconResDayId = R.drawable.snow,
        iconResNightId = R.drawable.snow,
        conditionMap = mapOf(
            85 to R.string.weather_condition_slight_snow_showers,
            86 to R.string.weather_condition_heavy_snow_showers
        )
    ),
    Thunderstorm(
        codes = setOf(95, 96, 99),
        iconResDayId = R.drawable.thunderstorm,
        iconResNightId = R.drawable.thunderstorm,
        conditionMap = mapOf(
            95 to R.string.weather_condition_slight_or_moderate_thunderstorm,
            96 to R.string.weather_condition_thunderstorm_with_slight_hail,
            99 to R.string.weather_condition_thunderstorm_with_heavy_hail
        )
    );

    companion object {
        fun getConditionFromCode(code: Int): Int {
            val entry = entries.find { code in it.codes }
            return entry?.conditionMap?.get(code) ?: R.string.weather_condition_clear_sky
        }

        fun getIconFromCode(code: Int, isDay: Boolean): Int {
            val entry = entries.find { code in it.codes }
            return if (isDay) entry?.iconResDayId ?: R.drawable.sun else entry?.iconResNightId
                ?: R.drawable.moon
        }

        fun presetForWeatherCode(code: Int, isDay: Boolean): WeatherPreset {
            return when (code) {
                0, 1 -> if (isDay) WeatherPreset.CLEAR_DAY else WeatherPreset.CLEAR_NIGHT

                2, 3, in 45..48 -> if (isDay) WeatherPreset.CLOUDY_DAY else WeatherPreset.CLOUDY_NIGHT

                in 51..67, in 80..82 -> if (isDay) WeatherPreset.RAINY_DAY else WeatherPreset.RAINY_NIGHT

                in 71..77, in 85..86 -> if (isDay) WeatherPreset.SNOWY_DAY else WeatherPreset.SNOWY_NIGHT

                in 95..99 -> WeatherPreset.STORMY

                else -> if (isDay) WeatherPreset.CLEAR_DAY else WeatherPreset.CLEAR_NIGHT
            }
        }
    }
}
