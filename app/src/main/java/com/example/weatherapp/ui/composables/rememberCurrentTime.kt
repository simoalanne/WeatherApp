package com.example.weatherapp.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.weatherapp.utils.getTimeAtOffset
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun rememberCurrentTime(timezoneOffset: Int): LocalDateTime {
    var currentTime by remember {
        mutableStateOf(
            LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timezoneOffset.toLong())
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = currentTime.plusSeconds(1)
            delay(1000)
        }
    }
    return currentTime
}

