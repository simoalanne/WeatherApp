package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.model.OpenMeteoCodes
import com.example.weatherapp.model.WeatherPreset
import com.example.weatherapp.model.WeatherVisualsObject
import com.example.weatherapp.ui.composables.AppBar
import com.example.weatherapp.ui.composables.BackgroundImage
import com.example.weatherapp.ui.composables.WeatherPage
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.rememberCurrentLanguageCode
import com.example.weatherapp.viewmodel.AppPreferences
import com.example.weatherapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun WeatherScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val uiState = mainViewModel.uiState
    Log.d("WeatherScreen", "WeatherScreen: $uiState")

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                stringResource(R.string.loading),
                color = MaterialTheme.colorScheme.onBackground
            )
            LinearProgressIndicator()
        }
        return
    }

    // If no favorite locations, navigate to search
    if (uiState.favoriteLocations.isEmpty()) {
        LaunchedEffect(Unit) {
            navController.navigate("search")
        }
        return
    }

    val pagerState = rememberPagerState(
        initialPage = uiState.pageIndex,
        pageCount = { uiState.favoriteLocations.size }
    )

    // when user adds a preview as favorite that causes navigation to this screen,
    // however cause there is navigation delay this needs to be delayed to avoid
    // showing the preview screen UI with false state when it's still navigating to here
    LaunchedEffect(Unit) {
        delay(1000)
        mainViewModel.clearPreview()
    }

    // sync vm state with pager state
    LaunchedEffect(pagerState.currentPage) {
        mainViewModel.changePageIndex(pagerState.currentPage)
    }

    // query refresh every 10 seconds or when the page changes
    val currentIndex = pagerState.currentPage.coerceIn(uiState.favoriteLocations.indices)
    LaunchedEffect(currentIndex) {
        while (true) {
            Log.d("WeatherScreen", "Refreshing weather for page $currentIndex")
            mainViewModel.refreshWeather()
            delay(10_000)
        }
    }

    val locationWeather = uiState.favoriteLocations[currentIndex]
    val currentWeather = locationWeather.weather

    val languageCode = rememberCurrentLanguageCode()
    val title = formatLocationName(locationWeather.location, languageCode = languageCode)

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(
            if (AppPreferences.preferences.selectedBackgroundPreset != WeatherPreset.DYNAMIC || currentWeather == null) {
                WeatherVisualsObject.visualsForPreset(AppPreferences.preferences.selectedBackgroundPreset)
            } else {
                WeatherVisualsObject.visualsForPreset(
                    OpenMeteoCodes.presetForWeatherCode(
                        currentWeather.current.weatherCode,
                        currentWeather.current.isDay
                    )
                )
            }
        )
        Column(modifier = Modifier.fillMaxSize()) {
            AppBar(
                title = title,
                onSearchIconPress = { navController.navigate("search") },
                onSettingsIconPress = { navController.navigate("settings") },
                totalPages = uiState.favoriteLocations.size,
                currentPage = pagerState.currentPage
            )
            HorizontalPager(state = pagerState) { pageIndex ->
                WeatherPage(
                    locationWeather = uiState.favoriteLocations[pageIndex]
                )
            }
        }
    }
}
