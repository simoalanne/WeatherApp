package com.simoalanne.weatherapp.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simoalanne.weatherapp.model.LocationAndRole
import com.simoalanne.weatherapp.model.LocationRole

/**
 * Composable for displaying a list of favorite locations. Supports displaying a sliding animation
 * when the locations are coming to the screen.
 *
 * @param favoriteLocations The list of favorite locations to display.
 * @param onLocationPress The callback function to be invoked when a location is pressed.
 * @param onLocationDelete The callback function to be invoked when a location is deleted.
 * @param shouldPlayAnimation Whether the sliding animation should be played for the locations or not.
 */
@Composable
fun FavoriteLocationsList(
    favoriteLocations: List<LocationAndRole>,
    onLocationPress: (Int) -> Unit,
    onLocationDelete: (LocationAndRole) -> Unit,
    shouldPlayAnimation: Boolean = true
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
                shouldPlayAnimation = shouldPlayAnimation,
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
        Margin(50)
    }
}
