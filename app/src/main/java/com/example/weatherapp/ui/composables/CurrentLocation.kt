package com.example.weatherapp.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationData
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CurrentLocation(userLocation: LocationData?, handleUserLocate: () -> Unit) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current

    LaunchedEffect(locationPermissionState.status.isGranted, userLocation) {
        if (locationPermissionState.status.isGranted && userLocation == null) {
            handleUserLocate()
        }
    }

    ExpandableColumn(
        label = stringResource(R.string.current_location),
        leadingIcon = {
            IconWithBackground(
                Icons.Default.LocationOn,
                backgroundColor = Color(0, 100, 0)
            )
        },
    ) {
        when {
            userLocation != null -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.location_available_message),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            !locationPermissionState.status.isGranted &&
                    locationPermissionState.status.shouldShowRationale -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.location_permissions_required_message),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        locationPermissionState.launchPermissionRequest()
                    }) {
                        Text(stringResource(R.string.locate_me))
                    }
                }
            }

            !locationPermissionState.status.isGranted &&
                    !locationPermissionState.status.shouldShowRationale -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.location_permissions_permanently_denied_message),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }) {
                        Text(stringResource(R.string.open_system_settings))
                    }
                }
            }

            else -> {
                Text(
                    stringResource(R.string.location_not_available),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

