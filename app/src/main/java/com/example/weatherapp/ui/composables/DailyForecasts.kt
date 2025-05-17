package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.HourlyWeather
import java.time.LocalDate
import com.example.weatherapp.R
import com.example.weatherapp.model.DailyWeather

@Composable
fun DailyForecasts(dailyForecasts: List<DailyWeather>, timezoneOffset: Int) {

    var expandedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = stringResource(R.string.five_day_forecast), color = Color.White, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = Color.White.copy(alpha = 0.5f), thickness = 0.5f.dp)
        }
        dailyForecasts.forEachIndexed { index, dailyForecast ->
            DailyForecastItem(
                dailyWeather = dailyForecast,
                isExpanded = dailyForecast.date == expandedDate,
                timezoneOffset = timezoneOffset,
                onExpand = {
                    // Only one item can be expanded at a time
                    expandedDate = if (expandedDate == dailyForecast.date) null else dailyForecast.date
                }
            )
        }
    }
}
