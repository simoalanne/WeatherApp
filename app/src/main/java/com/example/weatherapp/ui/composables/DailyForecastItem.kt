package com.example.weatherapp.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.WeatherIcons
import com.example.weatherapp.utils.formatDate
import com.example.weatherapp.utils.getDominantIcon
import com.example.weatherapp.utils.getMinMaxTemperature
import java.time.LocalDate
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@Composable
fun DailyForecastItem(
    date: LocalDate, dayForecasts: List<HourlyWeather>, timezoneOffset: Int,
    isExpanded: Boolean, onExpand: () -> Unit
) {
    val formattedDate = formatDate(date, timezoneOffset)
    val dominantIcon = getDominantIcon(dayForecasts.map { it.iconId })
    val dayVariant = WeatherIcons.getDailyVariantFromIcon(dominantIcon)
    val (minTemp, maxTemp) = getMinMaxTemperature(dayForecasts)
    val expandIcon =
        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
    val content = if (isExpanded) "Close" else "Expand"
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResImage(dayVariant)

                val minTempStr = "${minTemp.roundToInt()}°"
                val maxTempStr = "${maxTemp.roundToInt()}°"
                Text(
                    text = "$minTempStr / $maxTempStr",
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onExpand) {
                    Icon(imageVector = expandIcon, contentDescription = content, tint = Color.White)
                }
            }
        }
        AnimatedVisibility(visible = isExpanded) {
            WeatherList(dayForecasts, isNext24Hours = false)
        }
    }
}
