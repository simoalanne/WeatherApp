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
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.CurrentWeather

@Composable
fun WeatherStatsGrid(current: CurrentWeather) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeatherStatBox(
                icon = Icons.Default.Thermostat,
                label = "Feels Like",
                value = current.feelsLike,
                unit = "°",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.WaterDrop,
                label = "Humidity",
                value = current.humidityPercentage.toDouble(), // Convert to double for consistency
                unit = "%",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.Air,
                label = "Wind speed",
                value = current.windSpeed,
                unit = "m/s",
                modifier = Modifier.weight(1f)
            )
        }
        Margin(margin = 8)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeatherStatBox(
                icon = Icons.Default.Compress,
                label = "Air Pressure",
                value = current.airPressure.toDouble(), // Convert to double
                unit = "hPa",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.Cloud,
                label = "Cloud Cover",
                value = current.cloudinessPercentage.toDouble(), // Convert to double
                unit = "%",
                modifier = Modifier.weight(1f)
            )
            WeatherStatBox(
                icon = Icons.Default.Visibility,
                label = "Visibility",
                value = current.visibilityInMeters / 1000.0, // Convert to km
                unit = "km",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

