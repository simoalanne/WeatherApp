package com.simoalanne.weatherapp.ui.composables

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Composable for the country flag.
 *
 * @param countryCode The country code of the flag.
 * @param useCurvedFlag Whether to return curved flag or not.
 * @param size The size of the flag (dp)
 */
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