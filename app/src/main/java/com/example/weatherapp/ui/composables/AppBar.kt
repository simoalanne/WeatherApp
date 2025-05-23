package com.example.weatherapp.ui.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R

@Composable
fun AppBar(
    title: String,
    onSearchIconPress: () -> Unit,
    onSettingsIconPress: () -> Unit,
    totalPages: Int = 0,
    currentPage: Int = 0,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(0.75f)
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            if (totalPages > 1) {
                PageIndicator(currentPage, totalPages)
            }
        }

        Row(
            modifier = Modifier.weight(0.25f),
        ) {
            IconButton(onClick = onSearchIconPress) {
                Icon(
                    Icons.Default.Search,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.search)
                )
            }
            IconButton(onClick = onSettingsIconPress) {
                Icon(
                    Icons.Default.Settings,
                    tint = Color.White,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
    }
    HorizontalDivider(
        color = Color.White.copy(alpha = 0.5f),
        thickness = 0.5f.dp
    )
}
