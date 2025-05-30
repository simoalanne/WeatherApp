package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R

/**
 * Composable for displaying a CTA when there are no locations. Used in search screen.
 *
 * @param onSearchClick The callback function to be invoked when the search button is clicked.
 * @param onSettingsClick The callback function to be invoked when the settings button is clicked.
 */
@Composable
fun NoLocationsCta(onSearchClick: () -> Unit, onSettingsClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            stringResource(R.string.no_locations_message),
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Button(onClick = onSearchClick) {
                Text(stringResource(R.string.search_for_cities))
            }
            Button(onClick = onSettingsClick) {
                Text(stringResource(R.string.open_settings))
            }
        }
    }
}
