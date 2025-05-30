package com.example.weatherapp.ui.composables

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationWeather
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.utils.convertTemperature
import com.example.weatherapp.utils.rememberCurrentLocale
import com.example.weatherapp.viewmodel.AppPreferences
import kotlinx.coroutines.delay
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(
    locationWeather: LocationWeather
) {
    val currentWeather = locationWeather.weather
    if (currentWeather == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }
    val weather24Hours =
        currentWeather.dailyForecasts.flatMap { it.hourlyWeathers }.takeWhile {
            it.time.isBefore(currentWeather.current.time.plusHours(24))
        }
    val currentTime = rememberCurrentTime(currentWeather.meta.utcOffsetSeconds)
    val tempUnit = AppPreferences.preferences.tempUnit

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Margin(margin = 20)
        WeatherInfo(
            current = currentWeather.current.temperature,
            min = currentWeather.dailyForecasts.first().minTemperature,
            max = currentWeather.dailyForecasts.first().maxTemperature,
            condition = stringResource(currentWeather.current.conditionId)
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
        SunriseSunsetInfo(
            sunrise = currentWeather.current.sunrise,
            sunset = currentWeather.current.sunset,
            currentTime = currentTime,
            lastUpdated = currentWeather.current.time
        )
        Margin(margin = 8)
        TemperatureChart(
            dailyMinTemps = currentWeather.dailyForecasts.map {
                convertTemperature(
                    it.minTemperature,
                    tempUnit
                )
            },
            dailyMaxTemps = currentWeather.dailyForecasts.map {
                convertTemperature(
                    it.maxTemperature,
                    tempUnit
                )
            },
            dailyMeanTemps = currentWeather.dailyForecasts.map {
                convertTemperature(
                    it.meanTemperature,
                    tempUnit
                )
            },
            weekDays = currentWeather.dailyForecasts.map {
                it.date.dayOfWeek.getDisplayName(TextStyle.SHORT, rememberCurrentLocale())
                    .lowercase().replaceFirstChar { char -> char.uppercase() }
            },
            tempUnit = when (tempUnit) {
                TempUnit.CELSIUS -> "°C"
                TempUnit.FAHRENHEIT -> "°F"
                TempUnit.KELVIN -> "K"
            }
        )
        // WeatherStatsGrid(current = currentWeather.current)
        Margin(margin = 100) // For better scroll at bottom
    }
}
