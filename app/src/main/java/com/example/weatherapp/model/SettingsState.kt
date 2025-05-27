package com.example.weatherapp.model


data class SettingsState(
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOUR,
    val hourlyWeatherWhatToShow: HourlyWeatherWhatToShow = HourlyWeatherWhatToShow.BOTH
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

enum class HourlyWeatherWhatToShow {
    CONDITION_AND_TEMP,
    POP,
    BOTH
}
