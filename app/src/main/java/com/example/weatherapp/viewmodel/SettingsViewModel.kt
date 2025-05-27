package com.example.weatherapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.HourlyWeatherWhatToShow
import com.example.weatherapp.model.SettingsState
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.TimeFormat
import kotlinx.coroutines.launch

/**
 * The view model responsible for app settings.
 */
class SettingsViewModel : ViewModel() {
    private lateinit var dataStore: DataStore<Preferences>

    fun setDataStore(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }

    var settingsState by mutableStateOf(SettingsState())
        private set

    private fun Preferences.toSettingsState(): SettingsState {
        val unit = when (this[PreferencesKeys.TEMP_UNIT]) {
            "celsius" -> TempUnit.CELSIUS
            "fahrenheit" -> TempUnit.FAHRENHEIT
            "kelvin" -> TempUnit.KELVIN
            else -> TempUnit.CELSIUS
        }

        val timeFormat = when (this[PreferencesKeys.TIME_FORMAT]) {
            "twelve_hour" -> TimeFormat.TWELVE_HOUR
            "twenty_four_hour" -> TimeFormat.TWENTY_FOUR_HOUR
            else -> TimeFormat.TWENTY_FOUR_HOUR
        }

        val hourlyWeatherWhatToShow = when (this[PreferencesKeys.HOURLY_WEATHER_WHAT_TO_SHOW]) {
            "condition_and_temp" -> HourlyWeatherWhatToShow.CONDITION_AND_TEMP
            "pop" -> HourlyWeatherWhatToShow.POP
            else -> HourlyWeatherWhatToShow.BOTH
        }

        return SettingsState(unit, timeFormat, hourlyWeatherWhatToShow)
    }

    fun loadSettings() {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val state = preferences.toSettingsState()
                settingsState = state
                AppPreferences.preferences = state
            }
        }
    }

    fun setTempUnit(tempUnit: TempUnit) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.TEMP_UNIT] = tempUnit.name.lowercase()
            }
        }
    }

    fun setTimeFormat(timeFormat: TimeFormat) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.TIME_FORMAT] = timeFormat.name.lowercase()
            }
        }
    }

    fun setHourlyWeatherWhatToShow(whatToShow: HourlyWeatherWhatToShow) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.HOURLY_WEATHER_WHAT_TO_SHOW] =
                    whatToShow.name.lowercase()
            }
        }
    }
}

object PreferencesKeys {
    val TEMP_UNIT = stringPreferencesKey("temp_unit")
    val TIME_FORMAT = stringPreferencesKey("time_format")
    val HOURLY_WEATHER_WHAT_TO_SHOW = stringPreferencesKey("hourly_weather_what_to_show")
}

/**
 * Singleton object that holds the current app settings and can be accessed from anywhere.
 * Avoids needing to reference this view model elsewhere on other screens as well as having to
 * prop drill single preferences that a single composable needs.
 */
object AppPreferences {
    var preferences: SettingsState = SettingsState()
}
