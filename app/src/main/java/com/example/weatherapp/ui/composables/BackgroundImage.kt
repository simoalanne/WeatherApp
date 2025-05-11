package com.example.weatherapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.example.weatherapp.R
import androidx.compose.ui.res.painterResource

@Composable
fun BackgroundImage(isDay: Boolean) {
    val id = if (isDay) R.drawable.background else R.drawable.background_night
    Image(
        // TODO: image should be dynamic and based on the weather condition
        painter = painterResource(id),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        colorFilter = ColorFilter.tint(
            color = Color(red = 0f, green = 0f, blue = 0f, alpha = 0.3f),
            blendMode = BlendMode.Darken
        ),
        modifier = Modifier.fillMaxSize()
    )
}
