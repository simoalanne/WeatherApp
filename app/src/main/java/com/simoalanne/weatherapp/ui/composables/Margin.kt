package com.simoalanne.weatherapp.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Very simple abstraction for vertical margin.
 */
// TODO: Pretty pointless especially with limited options like this
@Composable
fun Margin(margin: Int) {
    val padding = margin
    Spacer(modifier = Modifier.height(padding.dp))
}
