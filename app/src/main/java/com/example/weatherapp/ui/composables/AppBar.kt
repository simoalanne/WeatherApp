package com.example.weatherapp.ui.composables

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.utils.formatLocalDateTime
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun AppBar(
    title: String,
    lastUpdated: String,
    timezoneOffset: Int,
    collapseHeader: Boolean,
    onSearchIconPress: () -> Unit
) {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timezoneOffset.toLong())
            currentTime = formatLocalDateTime(now, accuracy = "seconds")
            delay(1000)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = if (collapseHeader) Alignment.CenterVertically else Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(0.75f)
        ) {
            Text(text = title, color = Color.White, fontSize = if (collapseHeader) 14.sp else 20.sp)
            if (!collapseHeader) {
                Text(text = "Last updated: $lastUpdated", color = Color.White, fontSize = 12.sp)
                Text(text = "Local time: $currentTime", color = Color.White, fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier.weight(0.25f),
        ) {
            IconButton(onClick = onSearchIconPress) {
                Icon(Icons.Default.Search, tint = Color.White, contentDescription = "Search")
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Settings, tint = Color.White, contentDescription = "Settings")
            }
        }
    }
    if (collapseHeader) HorizontalDivider(
        color = Color.White.copy(alpha = 0.5f),
        thickness = 0.5f.dp
    )
}
