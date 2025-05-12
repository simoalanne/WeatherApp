package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.ui.composables.AppBar
import com.example.weatherapp.ui.composables.BackgroundImage
import com.example.weatherapp.ui.composables.DailyForecasts
import com.example.weatherapp.ui.composables.Margin
import com.example.weatherapp.ui.composables.WeatherStatsGrid
import com.example.weatherapp.ui.composables.WeatherInfo
import com.example.weatherapp.ui.composables.WeatherList
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.isDay
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.utils.truncateToHours

@Composable
fun WeatherScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
    val weatherData = weatherViewModel.weather
    val scrollState = rememberScrollState()

    if (weatherData != null) {
        val now = weatherData.hourlyForecasts.first().time
        val weather24Hours = weatherData.hourlyForecasts.takeWhile {
            truncateToHours(it.time).isBefore(now.plusHours(24)) || truncateToHours(it.time) == now.plusHours(24)
        }
        val timeZone = weatherData.meta.timezoneOffsetInSeconds / 3600
        BackgroundImage(isDay = isDay(now, weatherData.meta.sunriseSunsetTimes))
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppBar(
                title = "${formatLocationName(weatherData.meta.location)} (UTC${if (timeZone >= 0) "+" else ""}$timeZone)",
                lastUpdated = weatherData.current.time.toLocalTime().toString().substring(0, 5),
                timezoneOffset = weatherData.meta.timezoneOffsetInSeconds,
                // TODO: The effect should be animated like fonts getting smaller than just sudden change
                collapseHeader = scrollState.value > 150,
                onSearchIconPress =  { navController.navigate("search") }
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Margin(margin = 20)
                WeatherInfo(
                    current = weatherData.current.temperature,
                    min = weather24Hours.minOf { it.temperature },
                    max = weather24Hours.maxOf { it.temperature },
                    condition = weatherData.current.condition,
                    round = true
                )
                Margin(margin = 20)
                WeatherList(hourlyWeathers = weather24Hours)
                Margin(margin = 8)
                DailyForecasts(allHourlyForecasts = weatherData.hourlyForecasts, weatherData.meta.timezoneOffsetInSeconds)
                Margin(margin = 8)
                WeatherStatsGrid(current = weatherData.current)
                Margin(margin = 100) // Should be possible to scroll further down so the last elements are better viewable
            }
        }
    } else {
        Text(text = "No weather data available")
    }
}

