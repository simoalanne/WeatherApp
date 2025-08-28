package com.simoalanne.weatherapp.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class DisplayType {
    data class Text(val text: String, val cellHeight: Dp = 16.dp) : DisplayType()
    data class Icon(val icon: ImageVector, val cellHeight: Dp = 16.dp) : DisplayType()
}

/**
 * Composable displaying either text or icon based on the provided [DisplayType].
 */
@Composable
fun WeatherStatTextOrIcon(displayType: DisplayType) {
    val iconSize = with(LocalDensity.current) { 16.sp.toDp() }
    when (displayType) {
        is DisplayType.Text -> {
            WeatherStatText(displayType.text, cellHeight = displayType.cellHeight)
        }
        is DisplayType.Icon -> {
            Box(
                // draw debug border for now
                modifier = Modifier.height(displayType.cellHeight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = displayType.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
fun WeatherStatText(text: String, color: Color = Color.White, cellHeight: Dp = 16.dp) {
    Box(
        modifier = Modifier.height(cellHeight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = Bold,
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
        )
    }
}
