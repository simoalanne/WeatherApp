package com.simoalanne.weatherapp.model

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.simoalanne.weatherapp.R

/**
 * Singleton object containing the weather visuals for each weather preset.
 */
object WeatherVisualsObject {
    private val cloudyColorFilterDay =
        ColorFilter.tint(Color.Black.copy(alpha = 0.3f), BlendMode.Darken)

    private val visualsMap: Map<WeatherPreset, WeatherVisuals> = mapOf(
        WeatherPreset.CLEAR_DAY to WeatherVisuals(backgroundResId = R.drawable.day_clear_bg),
        WeatherPreset.CLEAR_NIGHT to WeatherVisuals(backgroundResId = R.drawable.night_clear_bg),

        WeatherPreset.CLOUDY_DAY to WeatherVisuals(
            backgroundResId = R.drawable.day_cloudy_bg,
            colorFilter = cloudyColorFilterDay
        ),
        WeatherPreset.CLOUDY_NIGHT to WeatherVisuals(
            backgroundResId = R.drawable.night_cloudy_bg,
        ),

        WeatherPreset.RAINY_DAY to WeatherVisuals(
            backgroundResId = R.drawable.day_cloudy_bg,
            colorFilter = cloudyColorFilterDay,
            lottieAnimationResId = R.raw.rain
        ),
        WeatherPreset.RAINY_NIGHT to WeatherVisuals(
            backgroundResId = R.drawable.night_cloudy_bg,
            lottieAnimationResId = R.raw.rain
        ),

        WeatherPreset.SNOWY_DAY to WeatherVisuals(
            backgroundResId = R.drawable.day_cloudy_bg,
            colorFilter = cloudyColorFilterDay,
            lottieAnimationResId = R.raw.snow
        ),
        WeatherPreset.SNOWY_NIGHT to WeatherVisuals(
            backgroundResId = R.drawable.night_cloudy_bg,
            lottieAnimationResId = R.raw.snow
        ),

        WeatherPreset.STORMY to WeatherVisuals(
            backgroundResId = R.drawable.storm_bg,
            lottieAnimationResId = R.raw.rain
        )
    )

    /**
     * Returns the weather visuals for the given weather preset or "CLEAR_DAY" if the preset is
     * not found. For dynamic weather visuals the DYNAMIC enum should not be passed here but resolved
     * through [OpenMeteoCodes] enums method
     */
    fun visualsForPreset(preset: WeatherPreset): WeatherVisuals {
        return visualsMap[preset] ?: visualsMap[WeatherPreset.CLEAR_DAY]!!
    }
}
