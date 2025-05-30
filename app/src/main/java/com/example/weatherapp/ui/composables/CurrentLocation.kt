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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Composable for the current location. Handles asking for location permissions and showing the status
 * of the location.
 *
 * @param userLocation The user location.
 * @param handleUserLocate Callback to call when user has given permissions but the location is null
 * @param shouldBeExpanded Whether the column should be expanded or not. If navigating here through
 * a cta button to enable location access should be set to true for better UX.
 *
 */
// TODO: Function name is misleading should be changed. Also userLocation should just be a boolean cause value is not important
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CurrentLocation(
    userLocation: LocationData?, handleUserLocate: () -> Unit, shouldBeExpanded: Boolean = false
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val context = LocalContext.current

    // this could be stored to datastore if wanted to show the permissions permanently denied message
    // initially and not only after user clicks the locate me button.
    var hasRequestedPermission by remember { mutableStateOf(false) }

    LaunchedEffect(locationPermissionState.status.isGranted, userLocation) {
        if (locationPermissionState.status.isGranted && userLocation == null) {
            handleUserLocate()
        }
    }

    ExpandableColumn(
        isExpandedInitially = shouldBeExpanded,
        label = stringResource(R.string.current_location),
        leadingIcon = {
            IconWithBackground(
                Icons.Default.LocationOn, backgroundColor = Color(0, 100, 0)
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

            !locationPermissionState.status.isGranted && locationPermissionState.status.shouldShowRationale || !hasRequestedPermission -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.location_permissions_required_message),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = {
                        hasRequestedPermission = true
                        locationPermissionState.launchPermissionRequest()
                    }) {
                        Text(stringResource(R.string.locate_me))
                    }
                }
            }

            !locationPermissionState.status.isGranted && !locationPermissionState.status.shouldShowRationale -> {
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

