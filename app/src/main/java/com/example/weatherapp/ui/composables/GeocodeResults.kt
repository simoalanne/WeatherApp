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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.weatherapp.model.Accuracy
import com.example.weatherapp.model.Coordinates
import com.example.weatherapp.model.GeocodeEntry
import com.example.weatherapp.model.Location
import com.example.weatherapp.utils.formatLocationName

@Composable
fun GeocodeResults(
    geocodeEntries: List<GeocodeEntry>,
    onSelect: (Coordinates, Location) -> Unit
) {
    val scrollState = rememberScrollState()

    // TODO: Item could be it's own composable
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        geocodeEntries.forEach { entry ->
            val displayText = formatLocationName(
                location = Location(entry.name, entry.countryCode, entry.state),
                accuracy = Accuracy.LOCATION_AND_STATE_AND_COUNTRY
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onSelect(
                            Coordinates(entry.lat, entry.lon),
                            Location(entry.name, entry.countryCode, entry.state)
                        )
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
