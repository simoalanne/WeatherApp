package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.utils.formatTemp

@Composable
fun WeatherListItem(formattedTime: String, iconResId: Int, temperature: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(formattedTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        ResImage(iconResId, width = 20, height = 20)
        Text(formatTemp(temperature), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}