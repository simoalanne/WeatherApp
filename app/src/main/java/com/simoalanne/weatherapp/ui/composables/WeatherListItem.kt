package com.simoalanne.weatherapp.ui.composables

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simoalanne.weatherapp.model.HourlyWeather
import com.simoalanne.weatherapp.model.WeatherInfoOption
import com.simoalanne.weatherapp.utils.formatTemp
import com.simoalanne.weatherapp.utils.formatWindSpeed
import com.simoalanne.weatherapp.viewmodel.AppPreferences
import kotlin.math.abs

/**
 * Composable for displaying a single item in the hourly weather list. Would work better as a row
 * than a column when there are lot of selected options.
 *
 * @param formattedTime The formatted time of the weather forecast.
 * @param hourlyWeather The hourly weather forecast.
 */
@Composable
fun WeatherListItem(formattedTime: String, hourlyWeather: HourlyWeather) {
    val selected = AppPreferences.preferences.selectedWeatherInfoOptions
    Log.d("WeatherListItem", "selected: $selected")
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
                iconColor = Color(0, 255, 0),
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

