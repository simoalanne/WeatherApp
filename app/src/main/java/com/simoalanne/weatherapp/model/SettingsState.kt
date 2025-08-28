package com.simoalanne.weatherapp.model

/**
 * Data class representing the app settings.
 *
 * @param tempUnit The unit of temperature to use.
 * @param timeFormat The time format to use.
 * @param selectedWeatherInfoOptions The weather info options to show in each hourly forecast item.
 * @param windSpeedUnit The unit of wind speed to use.
 * @param selectedBackgroundPreset The background preset to use.
 *
 */
data class SettingsState(
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOUR,
    val selectedWeatherInfoOptions: Set<WeatherInfoOption> = setOf(
        WeatherInfoOption.WEATHER_ICON,
        WeatherInfoOption.TEMPERATURE,
        WeatherInfoOption.FEELS_LIKE,
        WeatherInfoOption.WIND_GUSTS,
        WeatherInfoOption.WIND_DIRECTION,
        WeatherInfoOption.PROBABILITY_OF_PRECIPITATION,
        WeatherInfoOption.HUMIDITY,
        WeatherInfoOption.LABELS_AS_ICONS
    ),
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METERS_PER_SECOND,
    val selectedBackgroundPreset: WeatherPreset = WeatherPreset.DYNAMIC,
    val hasSeenOnboarding: Boolean = false
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
    LABELS_AS_ICONS,
    LABELS_AS_TEXT
}

enum class WindSpeedUnit {
    METERS_PER_SECOND,
    KILOMETERS_PER_HOUR,
    MILES_PER_HOUR
}
