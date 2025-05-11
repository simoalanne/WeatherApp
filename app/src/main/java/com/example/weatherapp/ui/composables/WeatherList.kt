package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.utils.formatLocalDateTime

@Composable
fun WeatherList(
    hourlyWeathers: List<HourlyWeather>,
    isNext24Hours: Boolean = true
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.3f))
            .padding(12.dp)
    ) {
        itemsIndexed(hourlyWeathers) { index, hourlyWeather ->
            val time = if (index == 0 && isNext24Hours) "Now" else formatLocalDateTime(hourlyWeather.time)
            WeatherListItem(time, hourlyWeather.iconId, hourlyWeather.temperature)
        }
    }
}

