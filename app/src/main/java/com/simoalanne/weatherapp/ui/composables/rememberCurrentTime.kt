package com.simoalanne.weatherapp.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Composable that returns the current time based on the timezone offset.
 *
 * @param timezoneOffset The timezone offset in seconds.
 */
@Composable
fun rememberCurrentTime(timezoneOffset: Int): LocalDateTime {
    var currentTime by remember {
        mutableStateOf(
            LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timezoneOffset.toLong())
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timezoneOffset.toLong())
            delay(1000)
        }
    }
    return currentTime
}

