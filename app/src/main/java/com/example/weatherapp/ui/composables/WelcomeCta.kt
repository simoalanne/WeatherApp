package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import kotlinx.coroutines.delay

/**
 * Composable for the welcome CTA. Displays a main title and automatically changing secondary
 * header and a simple button asking user to get started.
 */
@Composable
fun WelcomeCta(onGetStartedClick: () -> Unit) {
    val secondaryHeaders = listOf(
        stringResource(R.string.secondary_header_1),
        stringResource(R.string.secondary_header_2),
        stringResource(R.string.secondary_header_3),
        stringResource(R.string.secondary_header_4),
        stringResource(R.string.secondary_header_5),
        stringResource(R.string.secondary_header_6),
        stringResource(R.string.secondary_header_7),
    )

    var currentSecondaryHeaderIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(7500)
            currentSecondaryHeaderIndex = (currentSecondaryHeaderIndex + 1) % secondaryHeaders.size
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(48.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_message),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = Bold,
            )
            Text(
                text = secondaryHeaders[currentSecondaryHeaderIndex],
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f),
                minLines = 3
            )
            Button(onClick = onGetStartedClick) {
                Text(text = stringResource(R.string.get_started))
            }
        }
    }
}
