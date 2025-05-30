package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationDisplayAccuracy
import com.example.weatherapp.utils.rememberCurrentLanguageCode

/**
 * Reusable composable for displaying a location item in search screen either for the result or for a favorite location
 *
 * @param location The location data to display.
 * @param leadingIcon The leading icon to display.
 * @param onLocationTap The callback function to be invoked when the location is tapped.
 * @param onLocationDoubleTap The callback function to be invoked when the location is double tapped.
 * @param languageCode The language code to use for formatting the location name.
 *
 */
@Composable
fun LocationItem(
    location: LocationData?,
    leadingIcon: @Composable () -> Unit = {},
    onLocationTap: () -> Unit = {},
    onLocationDoubleTap: () -> Unit = {},
    languageCode: String = rememberCurrentLanguageCode()
) {
    if (location == null) return
    val displayText = formatLocationName(
        location = location,
        accuracy = LocationDisplayAccuracy.CITY_AND_COUNTRY,
        languageCode = languageCode
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                onClick = onLocationTap,
                onDoubleClick = onLocationDoubleTap,
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()
        MarqueeText(
            text = displayText, modifier = Modifier.weight(0.7f)
        )
        AsyncImage(
            model = "https://flagcdn.com/160x120/${location.countryCode.lowercase()}.png",
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Select",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}
