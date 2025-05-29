package com.example.weatherapp.ui.composables

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun IconWithBackground(
    icon: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onBackground,
    backgroundColor: Color = Color.DarkGray,
    contentDescription: String? = null,
    size: Int = 24,
    backgroundShape: CornerBasedShape = MaterialTheme.shapes.medium,
    iconRotation: Double = 0.0,
) {
    Box(
        modifier = Modifier
            .clip(backgroundShape)
            .background(color = backgroundColor)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier
                .size(size.dp)
                .rotate(iconRotation.toFloat())
        )
    }
}