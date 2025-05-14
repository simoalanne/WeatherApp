package com.example.weatherapp.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.GeoSearchFilterMode
import com.example.weatherapp.model.GeocodeEntry
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.getCurrentLocale
import com.example.weatherapp.R

@Composable
fun GeocodeResults(
    geocodeEntries: List<GeocodeEntry>,
    onSelect: (GeocodeEntry) -> Unit
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
                accuracy = geoSearchFilterMode,
                locale = getCurrentLocale()
            )

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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = displayText, color = Color.White, modifier = Modifier.weight(0.8f))
                    AsyncImage(
                        model = "https://flagcdn.com/160x120/${entry.countryCode.lowercase()}.png",
                        contentDescription = null,
                        modifier = Modifier.weight(0.1f)
                    )

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = Color.White,
                        modifier = Modifier.weight(0.1f)
                    )
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 0.5f.dp)
            }
        }
    }
}
