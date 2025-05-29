package com.example.weatherapp.model

import androidx.compose.ui.graphics.ColorFilter

data class WeatherVisuals(
    val backgroundResId: Int,
    val lottieAnimationResId: Int? = null,
    val colorFilter: ColorFilter? = null
)
