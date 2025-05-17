package com.example.weatherapp.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.weatherapp.model.GeoSearchFilterMode
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.getCurrentLocale
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationDisplayAccuracy

@Composable
fun GeocodeResults(
    geocodeEntries: List<LocationData>,
    onSelect: (LocationData) -> Unit,
    onFavoriteToggle: (LocationData) -> Unit,
    favoriteLocations: List<LocationData> = emptyList()
) {
    var geoSearchFilterMode by remember { mutableStateOf(GeoSearchFilterMode.BEST_MATCH) }

    Column {
        FilterModeSelector(
            selectedFilterMode = geoSearchFilterMode,
            options = listOf(
                Pair(R.string.best_match, GeoSearchFilterMode.BEST_MATCH),
                Pair(R.string.most_relevant, GeoSearchFilterMode.MOST_RELEVANT),
                Pair(R.string.all_results, GeoSearchFilterMode.ALL_RESULTS)
            ),
            onFilterModeSelected = { geoSearchFilterMode = it as GeoSearchFilterMode }
        )
        if (geocodeEntries.isEmpty()) return@Column
        val filteredGeocodeEntries = when (geoSearchFilterMode) {
            // whatever the API thought is most relevant is always first
            GeoSearchFilterMode.BEST_MATCH -> listOf(geocodeEntries.first())
            // only one city/town per country. If for example many cities in USA with same name
            // in response only the most relevant one is left
            GeoSearchFilterMode.MOST_RELEVANT -> geocodeEntries.distinctBy { it.countryCode }
            // return all entries with only clear duplicate entries removed eg. "London, UK" or
            // "Lontoo, UK" would be considered the same entry.
            GeoSearchFilterMode.ALL_RESULTS -> geocodeEntries
        }

        filteredGeocodeEntries.forEach { entry ->
            val displayText = formatLocationName(
                location = entry,
                accuracy = LocationDisplayAccuracy.CITY_AND_STATE_AND_COUNTRY,
                locale = getCurrentLocale()
            )

            val isFavorite = entry in favoriteLocations
            val favoriteIcon = if (isFavorite) {
                Icons.Default.Star
            } else {
                Icons.Default.StarOutline
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelect(entry)

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
                        modifier = Modifier.weight(0.1f).clickable { onFavoriteToggle(entry) }
                    )
                    MarqueeText(
                        text = displayText,
                        modifier = Modifier.weight(0.7f)
                    )
                    AsyncImage(
                        model =
                            "https://flagcdn.com/160x120/${entry.countryCode.lowercase()}.png",
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
    }
}
