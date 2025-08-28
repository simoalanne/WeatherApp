package com.simoalanne.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simoalanne.weatherapp.R
import com.simoalanne.weatherapp.model.LocationWeather
import com.simoalanne.weatherapp.model.TempUnit
import com.simoalanne.weatherapp.utils.convertTemperature
import com.simoalanne.weatherapp.utils.rememberCurrentLocale
import com.simoalanne.weatherapp.viewmodel.AppPreferences
import java.time.format.TextStyle

/**
 * Composable for displaying the weather information for a location.
 *
 * @param locationWeather The weather information for the location.
 */
// TODO: Spacing is inconsistent should rather just use columns and spacedBy()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(
    locationWeather: LocationWeather
) {
    val currentWeather = remember(locationWeather) { locationWeather.weather }

    if (currentWeather == null) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val weather24Hours = remember(currentWeather) {
        currentWeather.dailyForecasts
            .flatMap { it.hourlyWeathers }
            .takeWhile { it.time.isBefore(currentWeather.current.time.plusHours(24)) }
    }

    val currentTime = rememberCurrentTime(currentWeather.meta.utcOffsetSeconds)
    val tempUnit = remember { AppPreferences.preferences.tempUnit }

    val dailyMinTemps = remember(currentWeather, tempUnit) {
        currentWeather.dailyForecasts.map { convertTemperature(it.minTemperature, tempUnit) }
    }
    val dailyMaxTemps = remember(currentWeather, tempUnit) {
        currentWeather.dailyForecasts.map { convertTemperature(it.maxTemperature, tempUnit) }
    }
    val dailyMeanTemps = remember(currentWeather, tempUnit) {
        currentWeather.dailyForecasts.map { convertTemperature(it.meanTemperature, tempUnit) }
    }

    val currentLocale = rememberCurrentLocale()
    val weekDays = remember(currentWeather) {
        currentWeather.dailyForecasts.map {
            it.date.dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale)
                .replaceFirstChar { char -> char.uppercaseChar() }
        }
    }

    val tempUnitSymbol = remember(tempUnit) {
        when (tempUnit) {
            TempUnit.CELSIUS -> "°C"
            TempUnit.FAHRENHEIT -> "°F"
            TempUnit.KELVIN -> "K"
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // enable scrolling
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WeatherInfo(
            current = currentWeather.current.temperature,
            min = currentWeather.dailyForecasts.first().minTemperature,
            max = currentWeather.dailyForecasts.first().maxTemperature,
            condition = stringResource(currentWeather.current.conditionId)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0f, 0f, 0f, 0.3f))
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.next_24_hours),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )
            WeatherList(hourlyWeathers = weather24Hours)
        }

        DailyForecasts(
            dailyForecasts = currentWeather.dailyForecasts,
            timezoneOffset = currentWeather.meta.utcOffsetSeconds
        )

        SunriseSunsetInfo(
            sunrise = currentWeather.current.sunrise,
            sunset = currentWeather.current.sunset,
            currentTime = currentTime,
            lastUpdated = currentWeather.current.time
        )

        TemperatureChart(
            dailyMinTemps = dailyMinTemps,
            dailyMaxTemps = dailyMaxTemps,
            dailyMeanTemps = dailyMeanTemps,
            weekDays = weekDays,
            tempUnit = tempUnitSymbol
        )

        // Spacer at bottom for nicer scrolling
        Spacer(Modifier.height(100.dp))
    }

}
