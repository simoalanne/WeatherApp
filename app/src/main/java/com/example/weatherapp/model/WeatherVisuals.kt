package com.example.weatherapp.model

import androidx.compose.ui.graphics.ColorFilter

/**
 * Data class representing the weather visuals.
 *
 * @param backgroundResId The resource ID of the background image.
 * @param lottieAnimationResId The resource ID of the Lottie animation or null if not needed.
 * @param colorFilter The color filter to apply to the background image or null if not needed.
 */
data class WeatherVisuals(
    val backgroundResId: Int,
    val lottieAnimationResId: Int? = null,
    val colorFilter: ColorFilter? = null
)
