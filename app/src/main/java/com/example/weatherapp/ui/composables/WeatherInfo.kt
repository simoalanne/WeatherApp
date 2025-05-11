package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.utils.formatTemp

@Composable
fun WeatherInfo(
    current: Double,
    min: Double,
    max: Double,
    condition: String,
    round: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // For larger font this mess is needed because the temp + degree symbol in single string
        // looks bad. this slightly shifts the degree symbol up and entire row right so it looks
        // like the temp is in the exact center instead of being misaligned.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.offset(x = 15.dp)
        ) {
            Text(
                formatTemp(current, round, false),
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "°",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.offset(y = (-15).dp)
            )
        }
        Text(
            "$condition ${formatTemp(min, round)} / ${formatTemp(max, round)}",
            fontSize = 18.sp,
            color = Color.White
        )
    }
}
