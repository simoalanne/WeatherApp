package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun CountryFlag(
    countryCode: String,
    useCurvedFlag: Boolean = true,
    size: Int = 24
) {
    AsyncImage(
        model = "https://flagcdn.com/${if (useCurvedFlag) "160x120" else "h40"}/${countryCode.lowercase()}.png",
        contentDescription = null,
        modifier = Modifier.size(size.dp)
    )
}