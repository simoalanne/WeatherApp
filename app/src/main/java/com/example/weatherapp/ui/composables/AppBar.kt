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
    lastUpdated: String,
    timezoneOffset: Int,
    collapseHeader: Boolean,
    onSearchIconPress: () -> Unit,
    onSettingsIconPress: () -> Unit
) {
    var currentTime = rememberCurrentTime(timezoneOffset)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = if (collapseHeader) Alignment.CenterVertically else Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(0.75f)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = if (collapseHeader) 14.sp else 20.sp
            )
            if (!collapseHeader) {
                Text(text = "${stringResource(R.string.last_update)}: $lastUpdated", color = Color.White, fontSize = 12.sp)
                Text(text = "${stringResource(R.string.local_time)}: $currentTime", color = Color.White, fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier.weight(0.25f),
        ) {
            IconButton(onClick = onSearchIconPress) {
                Icon(Icons.Default.Search, tint = Color.White, contentDescription = stringResource(R.string.search))
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
    if (collapseHeader) HorizontalDivider(
        color = Color.White.copy(alpha = 0.5f),
        thickness = 0.5f.dp
    )
}
