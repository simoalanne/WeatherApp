package com.example.weatherapp.model


data class SettingsState(
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOUR
)

enum class TempUnit {
    CELSIUS,
    FAHRENHEIT,
    KELVIN
}

enum class TimeFormat {
    TWELVE_HOUR,
    TWENTY_FOUR_HOUR
}