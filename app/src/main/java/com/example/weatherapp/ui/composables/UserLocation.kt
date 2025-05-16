package com.example.weatherapp.ui.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import coil3.compose.AsyncImage
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.getCurrentLocale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationWeather

// TODO: Terrible modularization should be refactored.
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UserLocation(
    userLocation: LocationWeather?,
    onLocateUser: () -> Unit,
    onLocationPress: () -> Unit
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(stringResource(R.string.current_location_label), color = Color.White)
        when {
            userLocation != null -> {
                val locationText = formatLocationName(
                    userLocation.location,
                    locale = getCurrentLocale()
                )
                AssistChip(
                    onClick = onLocationPress,
                    label = {
                        Text(
                            text = locationText,
                            maxLines = 1,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        AsyncImage(
                            model = "https://flagcdn.com/h40/${userLocation.location.countryCode.lowercase()}.png",
                            contentDescription = null
                        )
                    }
                )
            }

            locationPermissionState.status.isGranted.not() -> {
                TextButton(onClick = {
                    locationPermissionState.launchPermissionRequest()
                }) {
                    Text(stringResource(R.string.locate_me))
                }
            }

            else -> {
                TextButton(onClick = onLocateUser) {
                    Text(stringResource(R.string.locate_me))
                }
            }
        }
    }
}
