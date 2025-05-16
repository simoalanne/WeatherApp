package com.example.weatherapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel

/**
 * The view model responsible for app settings.
 */
class SettingsViewModel: ViewModel() {
    private lateinit var dataStore: DataStore<Preferences>

    /* TODO: Implement settings
    var settings by mutableStateOf(Settings())
        private set */

    fun setDataStore(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }

    fun loadSettings() {
        // TODO: Load settings from data store
    }

    fun saveSettings() {
        // TODO: Save settings to data store
    }
}
