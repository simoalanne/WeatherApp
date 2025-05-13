package com.example.weatherapp.model

data class SunriseSunsetResponse(
    val results: List<Result>,
)

data class Result(
    val sunrise: String?,
    val sunset: String?,
)
