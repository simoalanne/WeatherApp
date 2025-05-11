package com.example.weatherapp.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Margin(margin: Int) {
    val padding = margin
    Spacer(modifier = Modifier.height(padding.dp))
}
