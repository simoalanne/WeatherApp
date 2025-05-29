package com.example.weatherapp.model

import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.example.weatherapp.R

object WeatherVisualsObject {
    private val cloudyColorFilterDay =
        ColorFilter.tint(Color.Black.copy(alpha = 0.3f), BlendMode.Darken)
    private val cloudyColorFilterNight =
        ColorFilter.tint(Color.Black.copy(alpha = 0.6f), BlendMode.Darken)

    private val visualsMap: Map<WeatherPreset, WeatherVisuals> = mapOf(
        WeatherPreset.CLEAR_DAY to WeatherVisuals(backgroundResId = R.drawable.day_clear_bg),
        WeatherPreset.CLEAR_NIGHT to WeatherVisuals(backgroundResId = R.drawable.night_clear_bg),

        WeatherPreset.CLOUDY_DAY to WeatherVisuals(
            backgroundResId = R.drawable.day_cloudy_bg,
            colorFilter = cloudyColorFilterDay
        ),
        WeatherPreset.CLOUDY_NIGHT to WeatherVisuals(
            backgroundResId = R.drawable.night_cloudy_bg,
            colorFilter = cloudyColorFilterNight
        ),

        WeatherPreset.RAINY_DAY to WeatherVisuals(
            backgroundResId = R.drawable.day_cloudy_bg,
            colorFilter = cloudyColorFilterDay,
            lottieAnimationResId = R.raw.rain
        ),
        WeatherPreset.RAINY_NIGHT to WeatherVisuals(
            backgroundResId = R.drawable.night_cloudy_bg,
            colorFilter = cloudyColorFilterNight,
            lottieAnimationResId = R.raw.rain
        ),

        WeatherPreset.SNOWY_DAY to WeatherVisuals(
            backgroundResId = R.drawable.day_cloudy_bg,
            colorFilter = cloudyColorFilterDay,
            lottieAnimationResId = R.raw.snow
        ),
        WeatherPreset.SNOWY_NIGHT to WeatherVisuals(
            backgroundResId = R.drawable.night_cloudy_bg,
            colorFilter = cloudyColorFilterNight,
            lottieAnimationResId = R.raw.snow
        ),

        WeatherPreset.STORMY to WeatherVisuals(
            backgroundResId = R.drawable.storm_bg,
            lottieAnimationResId = R.raw.rain
        )
    )

    fun visualsForPreset(preset: WeatherPreset): WeatherVisuals {
        return visualsMap[preset] ?: visualsMap[WeatherPreset.CLEAR_DAY]!!
    }
}
