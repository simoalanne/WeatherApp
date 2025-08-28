package com.simoalanne.weatherapp.ui.composables

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
import com.simoalanne.weatherapp.utils.formatTemp
import com.simoalanne.weatherapp.viewmodel.AppPreferences

/**
 * Composable for displaying current temperature, min and max temperatures and weather condition.
 * Current is displayed in large font and min and max + condition in small font below the current.
 *
 * @param current The current temperature.
 * @param min The minimum temperature.
 * @param max The maximum temperature.
 * @param condition The weather condition.
 */
// TODO: Confusing name. Not obvious what it does.
@Composable
fun WeatherInfo(
    current: Double,
    min: Double,
    max: Double,
    condition: String,
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
                formatTemp(current, addUnit = false),
                fontSize = 90.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                if (AppPreferences.preferences.tempUnit == com.simoalanne.weatherapp.model.TempUnit.KELVIN) {
                    "K"
                } else "°",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.offset(y = (-15).dp)
            )
        }
        Text(
            "$condition ${formatTemp(min)} / ${formatTemp(max)}",
            fontSize = 18.sp,
            color = Color.White
        )
    }
}
