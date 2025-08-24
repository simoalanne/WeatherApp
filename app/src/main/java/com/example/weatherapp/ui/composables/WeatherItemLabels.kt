package com.example.weatherapp.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.WeatherInfoOption
import com.example.weatherapp.model.WindSpeedUnit
import com.example.weatherapp.viewmodel.AppPreferences

/**
 * Composable for displaying the labels for the weather information in hourly forecast items.
 */
@Composable
fun WeatherItemLabels() {
    val selected = AppPreferences.preferences.selectedWeatherInfoOptions
    Log.d("WeatherItemLabels", "selected: $selected")
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

    val labelsOptions = setOf(WeatherInfoOption.LABELS_AS_TEXT, WeatherInfoOption.LABELS_AS_ICONS)
    if (selected.intersect(labelsOptions).isEmpty()) return
    val showText = WeatherInfoOption.LABELS_AS_TEXT in selected
    val backgroundModifier = if (showText) Modifier else Modifier
        .clip(MaterialTheme.shapes.small)
        .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.1f))
        .padding(8.dp)

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = backgroundModifier
    ) {
        WeatherStatTextOrIcon(
            if (showText)
                DisplayType.Text(stringResource(R.string.time))
            else DisplayType.Icon(
                Icons.Filled.AccessTime
            )
        )
        if (WeatherInfoOption.WEATHER_ICON in selected) {
            WeatherStatTextOrIcon(
                if (showText)
                    DisplayType.Text(stringResource(R.string.weather_icon), cellHeight = 20.dp)
                else DisplayType.Icon(Icons.Filled.WbSunny, cellHeight = 20.dp)
            )
        }
        if (WeatherInfoOption.TEMPERATURE in selected) {
            WeatherStatTextOrIcon(
                if (showText)
                    DisplayType.Text("${stringResource(R.string.temperature)} $tempUnit")
                else DisplayType.Icon(Icons.Filled.Thermostat)
            )
        }
        if (WeatherInfoOption.FEELS_LIKE in selected) {
            WeatherStatTextOrIcon(
                if (showText)
                    DisplayType.Text("${stringResource(R.string.feels_like)} $tempUnit")
                else DisplayType.Icon(ImageVector.vectorResource(R.drawable.feels_like))
            )
        }
        if (WeatherInfoOption.WIND_DIRECTION in selected) {
            WeatherStatTextOrIcon(
                if (showText)
                    DisplayType.Text(stringResource(R.string.wind_direction), cellHeight = 24.dp)
                else DisplayType.Icon(Icons.Filled.NearMe, cellHeight = 24.dp)
            )
        }
        if (WeatherInfoOption.WIND_GUSTS in selected) {
            WeatherStatTextOrIcon(
                if (showText)
                    DisplayType.Text("${stringResource(R.string.wind_gusts)} $windSpeedUnit")
                else DisplayType.Icon(Icons.Filled.Air)
            )
        }
        if (WeatherInfoOption.PROBABILITY_OF_PRECIPITATION in selected) {
            WeatherStatTextOrIcon(
                if (showText)
                    DisplayType.Text(stringResource(R.string.probability_of_precipitation))
                else DisplayType.Icon(ImageVector.vectorResource(R.drawable.pop))
            )
        }
        if (WeatherInfoOption.HUMIDITY in selected) {
            WeatherStatTextOrIcon(
                if (showText)
                    DisplayType.Text("${stringResource(R.string.humidity)}%")
                else DisplayType.Icon(Icons.Filled.WaterDrop)
            )
        }
    }
}