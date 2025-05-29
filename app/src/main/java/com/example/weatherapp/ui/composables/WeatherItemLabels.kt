package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.WeatherInfoOption
import com.example.weatherapp.model.WindSpeedUnit
import com.example.weatherapp.viewmodel.AppPreferences

@Composable
fun WeatherItemLabels() {
    val selected = AppPreferences.preferences.selectedWeatherInfoOptions
    val windSpeedUnit = when (AppPreferences.preferences.windSpeedUnit) {
        WindSpeedUnit.METERS_PER_SECOND -> "m/s"
        WindSpeedUnit.KILOMETERS_PER_HOUR -> "km/h"
        WindSpeedUnit.MILES_PER_HOUR -> "mph"
    }
    val tempUnit = when (AppPreferences.preferences.tempUnit) {
        TempUnit.CELSIUS -> "°C"
        TempUnit.FAHRENHEIT -> "°F"
        TempUnit.KELVIN -> "K"
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        WeatherStatText(stringResource(R.string.time))
        if (WeatherInfoOption.WEATHER_ICON in selected) {
            Box(modifier = Modifier.height(20.dp)) {
                WeatherStatText(stringResource(R.string.weather_icon))
            }
        }
        if (WeatherInfoOption.TEMPERATURE in selected) {
            WeatherStatText("${stringResource(R.string.temperature)} $tempUnit")
        }
        if (WeatherInfoOption.FEELS_LIKE in selected) {
            WeatherStatText(stringResource(R.string.feels_like))
        }
        if (WeatherInfoOption.WIND_DIRECTION in selected) {
            WeatherStatText(stringResource(R.string.wind_direction))
        }
        if (WeatherInfoOption.WIND_GUSTS in selected) {
            WeatherStatText("${stringResource(R.string.wind_gusts)} $windSpeedUnit")
        }
        if (WeatherInfoOption.PROBABILITY_OF_PRECIPITATION in selected) {
            WeatherStatText("${stringResource(R.string.probability_of_precipitation)}%")
        }
        if (WeatherInfoOption.HUMIDITY in selected)
            WeatherStatText("${stringResource(R.string.humidity)}%")
    }
}