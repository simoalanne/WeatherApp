package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.model.HourlyWeatherWhatToShow
import com.example.weatherapp.utils.formatTemp
import com.example.weatherapp.viewmodel.AppPreferences

@Composable
fun WeatherListItem(formattedTime: String, iconResId: Int, temperature: Double, pop: Int) {
    val whatToShow = AppPreferences.preferences.hourlyWeatherWhatToShow
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(formattedTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        if (whatToShow == HourlyWeatherWhatToShow.CONDITION_AND_TEMP || whatToShow == HourlyWeatherWhatToShow.BOTH) {
            ResImage(iconResId, width = 20, height = 20)
            Text(
                formatTemp(temperature),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        if (whatToShow == HourlyWeatherWhatToShow.POP || whatToShow == HourlyWeatherWhatToShow.BOTH) {
            Icon(
                imageVector = Icons.Filled.WaterDrop,
                contentDescription = null,
                tint = Color(30, 144, 255)
            )
            Text(
                "$pop%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
