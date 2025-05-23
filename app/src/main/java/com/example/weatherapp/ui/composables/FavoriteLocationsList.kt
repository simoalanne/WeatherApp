package com.example.weatherapp.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationAndRole
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.LocationWeather

@Composable
fun FavoriteLocationsList(
    favoriteLocations: List<LocationAndRole>,
    onLocationPress: (Int) -> Unit,
    onLocationDelete: (LocationAndRole) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        favoriteLocations.forEachIndexed { index, location ->
            SwipeableItem(
                start = if (index % 2 == 0) Offset.LEFT else Offset.RIGHT,
                initialDelayMs = 450,
                contentKey = location,
                content = {
                    LocationItem(
                        location = location.location,
                        onLocationTap = {
                            onLocationPress(index)
                        },
                        leadingIcon = {
                            if (location.role == LocationRole.FAVORITE) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.Yellow,
                                    modifier = Modifier.clickable {
                                        onLocationDelete(location)
                                    }
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Green
                                )
                            }
                        }
                    )
                }
            )
        }
    }
}
