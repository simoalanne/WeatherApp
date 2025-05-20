package com.example.weatherapp.ui.composables

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationDisplayAccuracy
import com.example.weatherapp.utils.rememberCurrentLanguageCode

@Composable
fun GeocodeResults(
    searchResult: LocationData?,
    onSelect: (LocationData) -> Unit,
    onFavoriteToggle: (LocationData) -> Unit,
    favoriteLocations: List<LocationData> = emptyList(),
    languageCode: String = rememberCurrentLanguageCode()
) {
    if (searchResult == null) return
    val displayText = formatLocationName(
        location = searchResult,
        accuracy = LocationDisplayAccuracy.CITY_AND_STATE_AND_COUNTRY,
        languageCode = languageCode
    )
    val isFavorite = searchResult in favoriteLocations
    val favoriteIcon = if (isFavorite) {
        Icons.Default.Star
    } else {
        Icons.Default.StarOutline
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(searchResult)

            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = favoriteIcon,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color.Yellow else Color.White,
                modifier = Modifier
                    .weight(0.1f)
                    .clickable { onFavoriteToggle(searchResult) }
            )
            MarqueeText(
                text = displayText,
                modifier = Modifier.weight(0.7f)
            )
            AsyncImage(
                model =
                    "https://flagcdn.com/160x120/${searchResult.countryCode.lowercase()}.png",
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Select",
                tint = Color.White,
                modifier = Modifier.weight(0.1f)
            )
        }
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.3f),
            thickness = 0.5f.dp
        )
    }
}
