package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.ui.composables.AppBar
import com.example.weatherapp.ui.composables.BackgroundImage
import com.example.weatherapp.ui.composables.DailyForecasts
import com.example.weatherapp.ui.composables.Margin
import com.example.weatherapp.ui.composables.WeatherInfo
import com.example.weatherapp.ui.composables.WeatherList
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.R
import com.example.weatherapp.model.OpenMeteoCodes
import com.example.weatherapp.model.WeatherUIStatus
import com.example.weatherapp.utils.getCurrentLocale
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel
) {
    val uiStatus = mainViewModel.uiState.uiStatus
    Log.d("WeatherScreen", "WeatherScreen: $uiStatus")
    when (uiStatus) {
        WeatherUIStatus.LOADING -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.loading))
                CircularProgressIndicator()
            }
        }

        WeatherUIStatus.SUCCESS, WeatherUIStatus.REFRESHING -> {
            val currentLocation = mainViewModel.uiState.currentLocation
            val currentWeather = currentLocation?.weather ?: return
            val scrollState = rememberScrollState()

            val weather24Hours =
                currentWeather.dailyForecasts.flatMap { it.hourlyWeathers }.takeWhile {
                    it.time.isBefore(currentWeather.current.time.plusHours(24))
                }
            BackgroundImage(isDay = currentWeather.current.isDay)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppBar(
                    title = "${
                        formatLocationName(
                            currentLocation.location,
                            locale = getCurrentLocale()
                        )
                    } (UTC${if (currentWeather.meta.utcOffsetSeconds > 0) "+" else ""}${currentWeather.meta.utcOffsetSeconds / 3600})",
                    lastUpdated = currentWeather.current.time.toLocalTime().toString()
                        .substring(0, 5),
                    timezoneOffset = currentWeather.meta.utcOffsetSeconds,
                    collapseHeader = scrollState.value > 150,
                    onSearchIconPress = { navController.navigate("search") }
                )
                PullToRefreshBox(
                    isRefreshing = uiStatus == WeatherUIStatus.REFRESHING,
                    onRefresh = {
                        mainViewModel.refreshCurrentLocation()
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Margin(margin = 20)
                        WeatherInfo(
                            current = currentWeather.current.temperature,
                            min = weather24Hours.minOf { it.temperature },
                            max = weather24Hours.maxOf { it.temperature },
                            condition = stringResource(currentWeather.current.conditionId),
                            round = true
                        )
                        Margin(margin = 20)
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Color(
                                        red = 0f,
                                        green = 0f,
                                        blue = 0f,
                                        alpha = 0.3f
                                    )
                                )
                                .padding(horizontal = 8.dp, vertical = 16.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = stringResource(R.string.next_24_hours),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.5f),
                                    thickness = 0.5f.dp
                                )
                            }
                            WeatherList(hourlyWeathers = weather24Hours)
                        }
                        Margin(margin = 8)
                        DailyForecasts(
                            currentWeather.dailyForecasts,
                            currentWeather.meta.utcOffsetSeconds
                        )
                        Margin(margin = 8)
                        // WeatherStatsGrid(current = currentWeather.current)
                        Margin(margin = 100) // Should be possible to scroll further down so the last elements are better viewable
                    }
                }
            }
        }

        WeatherUIStatus.EMPTY, WeatherUIStatus.ERROR -> {
            LaunchedEffect(Unit) {
                navController.navigate("search")
            }
        }
    }
}
