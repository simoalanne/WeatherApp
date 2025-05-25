package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.weatherapp.ui.composables.BackgroundImage
import com.example.weatherapp.ui.composables.PreviewWeatherAppBar
import com.example.weatherapp.ui.composables.WeatherPage
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.rememberCurrentLanguageCode
import com.example.weatherapp.viewmodel.MainViewModel

@Composable
fun PreviewWeatherScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    var isInitialLoad by remember { mutableStateOf(true) }
    LaunchedEffect(mainViewModel.uiState) {
        if (isInitialLoad) {
            isInitialLoad = false
        } else {
            navController.navigate("weather")
        }
    }
    val preview = mainViewModel.uiState.previewLocation ?: return
    val currentWeather = preview.weather
    val languageCode = rememberCurrentLanguageCode()
    val title = formatLocationName(preview.location, languageCode = languageCode)

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(isDay = currentWeather.current.isDay)

        Column(modifier = Modifier.fillMaxSize()) {
            PreviewWeatherAppBar(
                title = title,
                onBackPress = { navController.popBackStack() },
                onStarClick = { mainViewModel.changePreviewToFavorite() }
            )
            WeatherPage(
                locationWeather = preview,
                onRefresh = { mainViewModel.refreshWeather() }
            )
        }
    }
}
