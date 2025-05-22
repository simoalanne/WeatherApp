package com.example.weatherapp.utils

import android.util.Log
import com.example.weatherapp.model.TimeFormat
import com.example.weatherapp.viewmodel.AppPreferences
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Returns the LocalDateTime object for a given unix timestamp and timezone offset. For consistency
 * with time and timezone handling each unix timestamp from API responses should be converted to
 * a LocalDateTime object.
 *
 * @param timestamp the unix timestamp
 * @param timezoneOffset the timezone offset in seconds
 * @return the LocalDateTime object for the given timestamp and timezone offset
 */
fun getLocalDateTimeFromUnixTimestamp(timestamp: Long, timezoneOffset: Int = 0): LocalDateTime {
    val offset = ZoneOffset.ofTotalSeconds(timezoneOffset)
    val instant = Instant.ofEpochSecond(timestamp).atOffset(offset)
    return LocalDateTime.of(instant.toLocalDate(), instant.toLocalTime())
}

/*
fun isDay(time: LocalDateTime, sunriseSunsetMap: Map<String, SunriseSunset>): Boolean {
    val dateKey = time.toLocalDate().toString()
    val entry = sunriseSunsetMap[dateKey]
    if (entry == null) {
        val allDates = sunriseSunsetMap.keys.joinToString(", ")
        Log.e("isBeforeSunset", "No entry found for date: ${dateKey}. Available dates: $allDates")
        return true
    }
    return time.isAfter(entry.sunrise) && time.isBefore(entry.sunset)
} */

fun truncateToHours(time: LocalDateTime): LocalDateTime = time.truncatedTo(ChronoUnit.HOURS)

fun getHoursBetweenTwoLocalDates(a: LocalDateTime, b: LocalDateTime): Int {

    if (a.isAfter(b)) {
        Log.e("getHourDifference", "First DateTime should be before second DateTime")
        return 0
    }
    val roundedA = truncateToHours(a)
    val roundedB = truncateToHours(b)

    return Duration.between(roundedA, roundedB).toHours().toInt()
}

fun formatLocalDateTime(
    dateTime: LocalDateTime,
    accuracy: String = "minutes"
): String {
    val use24HourFormat = AppPreferences.preferences.timeFormat == TimeFormat.TWENTY_FOUR_HOUR
    val minute = dateTime.minute.toString().padStart(2, '0')
    val second = dateTime.second.toString().padStart(2, '0')

    return if (use24HourFormat) {
        val base = "${dateTime.hour}:$minute"
        if (accuracy == "seconds") "$base:$second" else base
    } else {
        val hour = dateTime.hour % 12
        val hourFormatted = if (hour == 0) 12 else hour
        val amPm = if (dateTime.hour < 12) "AM" else "PM"
        val base = "$hourFormatted:$minute $amPm"
        if (accuracy == "seconds") "$hourFormatted:$minute:$second $amPm" else base
    }
}

// This could just be a composable rather than pure util function because it depends on data from composable
// but for because all formatting is in util functions this stays here
fun formatDate(
    date: LocalDate,
    timezoneOffset: Int,
    locale: Locale,
    todayLabel: String,
    tomorrowLabel: String
): String {
    val dayOfWeek = date.dayOfWeek
    val monthFormatter = DateTimeFormatter.ofPattern("dd.MMM")

    val now = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timezoneOffset.toLong()).toLocalDate()

    return when (date) {
        now -> "${date.format(monthFormatter)} $todayLabel"
        now.plusDays(1) -> "${date.format(monthFormatter)} $tomorrowLabel"
        else -> "${date.format(monthFormatter)} ${
            dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                locale
            )?.lowercase()?.replaceFirstChar { it.uppercase() } ?: dayOfWeek.name
        }"
    }
}

/**
 * Returns the current time at one second accuracy with the given timezone offset.
 *
 * @param timezoneOffset the timezone offset in seconds
 * @return formatted time string with the given timezone offset
 */
fun getTimeAtOffset(timezoneOffset: Int = 0): String {
    val now = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timezoneOffset.toLong())
    return formatLocalDateTime(
        dateTime = now,
        accuracy = "seconds"
    )
}
