package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun WeatherStatBox(icon: ImageVector, label: String, value: Double, unit: String, modifier: Modifier = Modifier) {
    val roundedValue = value.roundToInt()

    val annotatedString = buildAnnotatedString {
        append("$roundedValue")
        withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)) {
            append(" $unit")
        }
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.3f))
            .padding(8.dp)
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp), tint = Color.White)
        Text(text = label, fontSize = 12.sp, color = Color.White)
        Text(text = annotatedString, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}


