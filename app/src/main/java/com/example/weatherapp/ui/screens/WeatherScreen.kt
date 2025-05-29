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
                .background(MaterialTheme.colorScheme.background),

            ) {
            Text(
                stringResource(R.string.loading),
                color = MaterialTheme.colorScheme.onBackground
            )
            LinearProgressIndicator()

        }
        return
    }

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

    LaunchedEffect(Unit) {
        delay(1000)
        mainViewModel.clearPreview()
    }

    LaunchedEffect(pagerState.currentPage) {
        mainViewModel.changePageIndex(pagerState.currentPage)
    }

    val currentIndex = pagerState.currentPage.coerceIn(uiState.favoriteLocations.indices)
    val locationWeather = uiState.favoriteLocations[currentIndex]
    val currentWeather = locationWeather.weather
    if (currentWeather == null) return
    val languageCode = rememberCurrentLanguageCode()
    val title = formatLocationName(locationWeather.location, languageCode = languageCode)
    Log.d("WeatherScreen", "WeatherScreen conditionid: ${currentWeather.current.conditionId}")
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage(
            if (AppPreferences.preferences.selectedBackgroundPreset != WeatherPreset.DYNAMIC) {
                WeatherVisualsObject.visualsForPreset(AppPreferences.preferences.selectedBackgroundPreset)
            } else {
                currentWeather.current.weatherVisuals
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
                    locationWeather = uiState.favoriteLocations[pageIndex],
                    onRefresh = { mainViewModel.refreshWeather(pageIndex) }
                )
            }
        }
    }
}
