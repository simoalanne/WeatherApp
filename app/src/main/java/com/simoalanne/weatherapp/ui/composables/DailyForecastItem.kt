package com.simoalanne.weatherapp.ui.composables

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
import androidx.compose.ui.unit.sp
import com.simoalanne.weatherapp.utils.formatDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.stringResource
import com.simoalanne.weatherapp.R
import com.simoalanne.weatherapp.model.DailyWeather
import com.simoalanne.weatherapp.utils.formatTemp
import com.simoalanne.weatherapp.utils.rememberCurrentLocale

/**
 * Composable for the daily forecast item showing summary of the days weather and temperature
 * and containing an expandable list of hourly weather.
 *
 * @param dailyWeather The daily weather.
 * @param isExpanded Whether the hourly weathers from daily weather are expanded or not.
 * @param onExpand Callback for when the expand button is pressed.
 * @param timezoneOffset The timezone offset required to format the date.
 *
 */
@Composable
fun DailyForecastItem(
    dailyWeather: DailyWeather,
    isExpanded: Boolean, onExpand: () -> Unit,
    timezoneOffset: Int,
) {
    val locale = rememberCurrentLocale()
    val formattedDate = formatDate(
        dailyWeather.date,
        timezoneOffset,
        locale = locale,
        stringResource(R.string.today),
        stringResource(R.string.tomorrow)
    )
    val dominantIcon = dailyWeather.weatherIconId
    val minTemp = dailyWeather.minTemperature
    val maxTemp = dailyWeather.maxTemperature
    val expandIcon =
        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
    val content =
        if (isExpanded) stringResource(R.string.collapse) else stringResource(R.string.expand)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.55f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.weight(0.45f)
            ) {
                ResImage(dominantIcon)
                val minTempStr = formatTemp(minTemp)
                val maxTempStr = formatTemp(maxTemp)
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
            WeatherList(dailyWeather.hourlyWeathers, isNext24Hours = false)
        }
    }
}
