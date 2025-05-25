package com.example.weatherapp.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.utils.formatLocalDateTime
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs

@Composable
fun SunriseSunsetInfo(
    sunrise: LocalDateTime,
    sunset: LocalDateTime,
    currentTime: LocalDateTime,
    lastUpdated: LocalDateTime,
) {
    val isDay = lastUpdated.isAfter(sunrise) && lastUpdated.isBefore(sunset)
    val eventDuration = abs(Duration.between(sunrise, sunset).toMinutes())

    val progress =
        if (isDay) {
            (Duration.between(sunrise, lastUpdated).toMinutes()) / eventDuration.toFloat()
        } else {
            (Duration.between(sunset, lastUpdated).toMinutes()) / eventDuration.toFloat()
        }

    val density = LocalDensity.current
    var containerWidthPx by remember { mutableIntStateOf(0) }
    val iconSize = 32.dp
    val iconSizePx = with(density) { iconSize.toPx() }.toInt()

    val progressBarLeftWidthPx = containerWidthPx * progress
    val progressBarRightWidthPx = containerWidthPx * (1 - progress)

    val progressBarLeftWidthDp = with(density) { progressBarLeftWidthPx.toDp() }
    val progressBarRightWidthDp = with(density) { progressBarRightWidthPx.toDp() }

    Column(
        verticalArrangement = spacedBy(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(12.dp)
            .onGloballyPositioned {
                containerWidthPx = it.size.width - iconSizePx
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconWithBackground(
                    Icons.Default.AccessTime,
                    iconColor = Color.White,
                    size = 16
                )
                Text(
                    formatLocalDateTime(currentTime, accuracy = "seconds"),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = Bold
                )
            }
            Row(
                horizontalArrangement = spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconWithBackground(
                    Icons.Default.Cloud,
                    iconColor = Color.White,
                    backgroundColor = Color(144, 213, 255),
                    size = 16
                )
                Text(
                    formatLocalDateTime(lastUpdated, accuracy = "minutes"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = Bold
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = SpaceBetween,
            ) {
                ResImage(resId = if (isDay) R.drawable.sunrise else R.drawable.sunset)
                ResImage(resId = if (isDay) R.drawable.sunset else R.drawable.sunrise)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = SpaceBetween,
            ) {
                if (isDay) {
                    Text(stringResource(R.string.sunrise), color = Color.White)
                    Text(stringResource(R.string.sunset), color = Color.White)
                } else {
                    Text(stringResource(R.string.sunset), color = Color.White)
                    Text(stringResource(R.string.sunrise), color = Color.White)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProgressBox(
                    width = progressBarLeftWidthDp,
                    color = Color.White
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(iconSize)
                ) {
                    ResImage(
                        resId = if (isDay) R.drawable.sun else R.drawable.moon,
                    )
                }
                ProgressBox(
                    width = progressBarRightWidthDp,
                    color = Color.White.copy(alpha = 0.3f)
                )

            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = SpaceBetween,
            ) {
                Text(
                    if (isDay) formatLocalDateTime(sunrise) else formatLocalDateTime(sunset),
                    color = Color.White
                )
                Text(
                    if (isDay) formatLocalDateTime(sunset) else formatLocalDateTime(sunrise),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ProgressBox(width: Dp, color: Color) {
    Box(
        modifier = Modifier
            .width(width)
            .height(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(color)
    )
}
