package com.example.weatherapp.ui.composables

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationWeather
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.rememberCurrentLanguageCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(
    locationWeather: LocationWeather,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    navController: NavController
) {
    val currentLocation = locationWeather.location
    val currentWeather = locationWeather.weather
    val scrollState = rememberScrollState()
    val languageCode = rememberCurrentLanguageCode()

    val weather24Hours =
        currentWeather.dailyForecasts.flatMap { it.hourlyWeathers }.takeWhile {
            it.time.isBefore(currentWeather.current.time.plusHours(24))
        }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        BackgroundImage(isDay = currentWeather.current.isDay)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(
                title = "${
                    formatLocationName(
                        currentLocation,
                        languageCode = languageCode
                    )
                } (UTC${if (currentWeather.meta.utcOffsetSeconds > 0) "+" else ""}${currentWeather.meta.utcOffsetSeconds / 3600})",
                lastUpdated = currentWeather.current.time.toLocalTime().toString()
                    .substring(0, 5),
                timezoneOffset = currentWeather.meta.utcOffsetSeconds,
                collapseHeader = scrollState.value > 150,
                onSearchIconPress = { navController.navigate("search") }
            )
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    onRefresh()
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
}
