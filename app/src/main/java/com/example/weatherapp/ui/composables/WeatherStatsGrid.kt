package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.R
/*
@Composable
fun WeatherStatsGrid(current: CurrentWeather) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeatherStatBox(
                icon = Icons.Default.Thermostat,
                label = stringResource(R.string.feels_like),
                value = current.feelsLike,
                unit = "°",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.WaterDrop,
                label = stringResource(R.string.humidity),
                value = current.humidityPercentage.toDouble(),
                unit = "%",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.Air,
                label = stringResource(R.string.wind_speed),
                value = current.windSpeed,
                unit = "m/s",
                modifier = Modifier.weight(1f)
            )
        }
        Margin(margin = 8)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeatherStatBox(
                icon = Icons.Default.Compress,
                label = stringResource(R.string.air_pressure),
                value = current.airPressure.toDouble(),
                unit = "hPa",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.Cloud,
                label = stringResource(R.string.cloud_cover),
                value = current.cloudinessPercentage.toDouble(),
                unit = "%",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.Visibility,
                label = stringResource(R.string.visibility),
                value = current.visibilityInMeters / 1000.0,
                unit = "km",
                modifier = Modifier.weight(1f)
            )
        }
    }
}*/
