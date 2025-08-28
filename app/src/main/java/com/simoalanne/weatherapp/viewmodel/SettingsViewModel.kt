package com.simoalanne.weatherapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simoalanne.weatherapp.model.SettingsState
import com.simoalanne.weatherapp.model.TempUnit
import com.simoalanne.weatherapp.model.TimeFormat
import com.simoalanne.weatherapp.model.WeatherInfoOption
import com.simoalanne.weatherapp.model.WeatherPreset
import com.simoalanne.weatherapp.model.WindSpeedUnit
import kotlinx.coroutines.launch

/**
 * The view model responsible for app settings.
 */
class SettingsViewModel(
    private val dataStore: DataStore<Preferences>,
    private val onSettingsLoaded: (hasUserSeenOnboarding: Boolean) -> Unit
) : ViewModel() {

    var settingsState by mutableStateOf(SettingsState())
        private set

    init {
        loadSettings()
    }

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

        val windSpeedUnit =
            WindSpeedUnit.valueOf(this[PreferencesKeys.WIND_SPEED_UNIT] ?: "METERS_PER_SECOND")

        val selectedOptions = this[PreferencesKeys.SELECTED_OPTIONS]?.split(",")?.map {
            WeatherInfoOption.valueOf(it)
        }?.toSet()

        val selectedBackgroundPreset = WeatherPreset.valueOf(
            this[PreferencesKeys.BACKGROUND_PRESET] ?: "DYNAMIC"
        )

        return SettingsState(
            tempUnit = unit,
            timeFormat = timeFormat,
            selectedWeatherInfoOptions = selectedOptions
                ?: SettingsState().selectedWeatherInfoOptions,
            windSpeedUnit = windSpeedUnit,
            selectedBackgroundPreset = selectedBackgroundPreset,
            hasSeenOnboarding = this[PreferencesKeys.HAS_SEEN_ONBOARDING] == "true"
        )
    }

    fun loadSettings() {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val state = preferences.toSettingsState()
                settingsState = state
                AppPreferences.preferences = state
                onSettingsLoaded(state.hasSeenOnboarding)
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

    fun setSelectedOptions(selectedOptions: Set<WeatherInfoOption>) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.SELECTED_OPTIONS] =
                    selectedOptions.joinToString(",") { it.name }
            }
        }
    }

    fun setWindSpeedUnit(windSpeedUnit: String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.WIND_SPEED_UNIT] = windSpeedUnit
            }
        }
    }

    fun setSelectedBackgroundPreset(selectedBackgroundPreset: WeatherPreset) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.BACKGROUND_PRESET] = selectedBackgroundPreset.name
            }
        }
    }

    fun onOnboardingComplete() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.HAS_SEEN_ONBOARDING] = "true"
            }
        }
    }
}

object PreferencesKeys {
    val TEMP_UNIT = stringPreferencesKey("temp_unit")
    val TIME_FORMAT = stringPreferencesKey("time_format")
    val SELECTED_OPTIONS = stringPreferencesKey("selected_weather_info_options")
    val WIND_SPEED_UNIT = stringPreferencesKey("wind_speed_unit")
    val BACKGROUND_PRESET = stringPreferencesKey("background_preset")
    val HAS_SEEN_ONBOARDING = stringPreferencesKey("has_seen_onboarding")
}

/**
 * Singleton object that holds the current app settings and can be accessed from anywhere.
 * Avoids needing to reference this view model elsewhere on other screens as well as having to
 * prop drill single preferences that a single composable needs.
 */
object AppPreferences {
    var preferences: SettingsState = SettingsState()
}
