package com.example.weatherapp.ui.composables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp

@Composable
fun WeatherStatText(text: String, color: Color = Color.White) {
    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        fontWeight = Bold
    )
}
