package com.example.weatherapp.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.weatherapp.utils.getTimeAtOffset
import kotlinx.coroutines.delay

@Composable
fun rememberCurrentTime(timezoneOffset: Int): String {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getTimeAtOffset(timezoneOffset)
            delay(1000)
        }
    }
    return currentTime
}
