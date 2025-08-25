package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weatherapp.model.WeatherPreset
import com.example.weatherapp.model.WeatherVisualsObject
import com.example.weatherapp.ui.composables.BackgroundImage
import com.example.weatherapp.ui.composables.WelcomeCta

@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackgroundImage(WeatherVisualsObject.visualsForPreset(WeatherPreset.entries.random()))
        WelcomeCta(onGetStartedClick = onOnboardingComplete)
    }
}
