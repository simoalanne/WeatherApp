package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.weatherapp.model.OpenMeteoCodes
import com.example.weatherapp.model.WeatherPreset
import com.example.weatherapp.model.WeatherVisualsObject
import com.example.weatherapp.ui.composables.AppBar
import com.example.weatherapp.ui.composables.BackgroundImage
import com.example.weatherapp.ui.composables.WeatherPage
import com.example.weatherapp.ui.composables.WelcomeCta
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
    val isEmptyOrLoading = uiState.favoriteLocations.isEmpty() || uiState.isLoading

    if (isEmptyOrLoading) {
        var randomPreset by remember { mutableStateOf(WeatherPreset.entries.random()) }
        LaunchedEffect(Unit) {
            while (true) {
                delay(10000)
                randomPreset = WeatherPreset.entries.filter { it != randomPreset }.random()
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            BackgroundImage(
                WeatherVisualsObject.visualsForPreset(randomPreset)
            )
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White)
            }
            if (uiState.favoriteLocations.isEmpty() && !uiState.isLoading) {
                WelcomeCta { navController.navigate("search") }
            }
        }
        return
    }

    val pageIndex = navController.currentBackStackEntry
        ?.arguments?.getInt("pageIndex") ?: uiState.pageIndex

    val pagerState = rememberPagerState(
        initialPage = pageIndex,
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
    val currentIndex = pagerState.currentPage
    LaunchedEffect(currentIndex) {
        while (true) {
            Log.d("WeatherScreen", "Refreshing weather for page $currentIndex")
            // index needed here since there could be a race condition where the page index in vm
            // is not updated when this is called
            mainViewModel.refreshWeather(currentIndex)
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
                onSearchIconPress = {
                    mainViewModel.setAllButCurrentWeatherToNull()
                    navController.navigate("search")
                },
                onSettingsIconPress = {
                    mainViewModel.setAllButCurrentWeatherToNull()
                    navController.navigate("settings")
                },
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
