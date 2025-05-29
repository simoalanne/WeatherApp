package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.WeatherInfoOption
import com.example.weatherapp.utils.formatTemp
import com.example.weatherapp.utils.formatWindSpeed
import com.example.weatherapp.viewmodel.AppPreferences
import kotlin.math.abs

@Composable
fun WeatherListItem(formattedTime: String, hourlyWeather: HourlyWeather) {
    val selected = AppPreferences.preferences.selectedWeatherInfoOptions
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        WeatherStatText(formattedTime)

        if (WeatherInfoOption.WEATHER_ICON in selected) {
            ResImage(hourlyWeather.weatherIconId, width = 20, height = 20)
        }
        if (WeatherInfoOption.TEMPERATURE in selected) {
            WeatherStatText(formatTemp(hourlyWeather.temperature))
        }
        if (WeatherInfoOption.FEELS_LIKE in selected) {
            val color = if (abs(hourlyWeather.feelsLike - hourlyWeather.temperature) >= 2)
                Color.Red else Color.White
            WeatherStatText(formatTemp(hourlyWeather.feelsLike), color)
        }
        if (WeatherInfoOption.WIND_DIRECTION in selected) {
            IconWithBackground(
                icon = Icons.Filled.ArrowUpward,
                iconColor = Color(30, 144, 255),
                backgroundColor = Color(255, 255, 255, 128),
                iconRotation = hourlyWeather.windDirection.toDouble(),
                size = 16,
                backgroundShape = CircleShape
            )
        }
        if (WeatherInfoOption.WIND_GUSTS in selected) {
            WeatherStatText("${formatWindSpeed(hourlyWeather.windGusts)}")
        }
        if (WeatherInfoOption.PROBABILITY_OF_PRECIPITATION in selected) {
            val color = if (hourlyWeather.pop >= 75) Color.Red else Color.White
            WeatherStatText("${hourlyWeather.pop}%", color)
        }
        if (WeatherInfoOption.HUMIDITY in selected) {
            WeatherStatText("${hourlyWeather.humidity}%")
        }
    }
}

