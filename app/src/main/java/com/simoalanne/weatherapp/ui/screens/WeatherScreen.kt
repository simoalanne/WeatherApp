package com.simoalanne.weatherapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.simoalanne.weatherapp.model.OpenMeteoCodes
import com.simoalanne.weatherapp.model.WeatherPreset
import com.simoalanne.weatherapp.model.WeatherVisualsObject
import com.simoalanne.weatherapp.ui.composables.AppBar
import com.simoalanne.weatherapp.ui.composables.BackgroundImage
import com.simoalanne.weatherapp.ui.composables.WeatherPage
import com.simoalanne.weatherapp.utils.formatLocationName
import com.simoalanne.weatherapp.utils.rememberCurrentLanguageCode
import com.simoalanne.weatherapp.viewmodel.AppPreferences
import com.simoalanne.weatherapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun WeatherScreen(
    navController: NavController, mainViewModel: MainViewModel
) {
    val uiState = mainViewModel.uiState
    val pageIndex =
        navController.currentBackStackEntry?.arguments?.getInt("pageIndex") ?: uiState.pageIndex

    if (uiState.favoriteLocations.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = pageIndex, pageCount = { uiState.favoriteLocations.size })

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

    val currentIndex = pagerState.currentPage
    LaunchedEffect(currentIndex) {
        while (true) {
            // index needed here since there could be a race condition where the page index in vm
            // is not updated when this is called
            mainViewModel.refreshWeather(currentIndex)
            delay(2500)
        }
    }

    val locationWeather =
        uiState.favoriteLocations[currentIndex.coerceAtMost(uiState.favoriteLocations.size - 1)]
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
                        currentWeather.current.weatherCode, currentWeather.current.isDay
                    )
                )
            }
        )
        Column(modifier = Modifier.fillMaxSize()) {
            AppBar(
                title = title, onSearchIconPress = {
                    mainViewModel.setAllButCurrentWeatherToNull()
                    navController.navigate("search")
                }, onSettingsIconPress = {
                    mainViewModel.setAllButCurrentWeatherToNull()
                    navController.navigate("settings")
                }, totalPages = uiState.favoriteLocations.size, currentPage = pagerState.currentPage
            )
            HorizontalPager(state = pagerState) { pageIndex ->
                WeatherPage(
                    locationWeather = uiState.favoriteLocations[pageIndex]
                )
            }
        }
    }
}
