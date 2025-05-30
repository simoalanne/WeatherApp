package com.example.weatherapp.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/**
 * Reusable composable for displaying non async images
 *
 * @param resId The resource id of the image.
 * @param description The description of the image.
 * @param width The width of the image.
 * @param height The height of the image.
 */
@Composable
fun ResImage(resId: Int, description: String = "", width: Int = 24, height: Int = 24) {
    Image(
        painterResource(id = resId),
        contentDescription = description,
        modifier = Modifier.size(width.dp, height.dp)
    )
}
