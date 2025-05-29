package com.example.weatherapp.model


data class SettingsState(
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOUR,
    val selectedWeatherInfoOptions: Set<WeatherInfoOption> = setOf(
        WeatherInfoOption.WEATHER_ICON,
        WeatherInfoOption.TEMPERATURE,
        WeatherInfoOption.WIND_GUSTS,
        WeatherInfoOption.HUMIDITY,
        WeatherInfoOption.LABELS
    ),
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METERS_PER_SECOND,
    val selectedBackgroundPreset: WeatherPreset = WeatherPreset.DYNAMIC
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

enum class WeatherInfoOption {
    WEATHER_ICON,
    TEMPERATURE,
    FEELS_LIKE,
    WIND_DIRECTION,
    WIND_GUSTS,
    PROBABILITY_OF_PRECIPITATION,
    HUMIDITY,
    LABELS // whether to show labels for the selected options or no
}

enum class WindSpeedUnit {
    METERS_PER_SECOND,
    KILOMETERS_PER_HOUR,
    MILES_PER_HOUR
}
