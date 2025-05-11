package com.example.weatherapp.utils

import com.example.weatherapp.model.HourlyWeather
import kotlin.math.roundToInt

fun getDominantIcon(iconIds: List<Int>): Int {
    return iconIds.groupingBy { it }
        .eachCount()
        .maxBy { it.value }
        .key
}

fun getMinMaxTemperature(dayForecasts: List<HourlyWeather>): Pair<Double, Double> {
    val minTemp = dayForecasts.minOf { it.temperature }
    val maxTemp = dayForecasts.maxOf { it.temperature }
    return Pair(minTemp, maxTemp)
}

fun formatTemp(temp: Double, round: Boolean = true, addSymbol: Boolean = true) =
    "${if (round) temp.roundToInt() else temp}${if (addSymbol) "°C" else ""}"
