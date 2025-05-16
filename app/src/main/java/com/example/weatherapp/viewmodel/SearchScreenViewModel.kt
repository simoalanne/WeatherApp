package com.example.weatherapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.model.SearchUiState
import com.example.weatherapp.model.toLocationData
import com.example.weatherapp.network.GeocodeAPI
import kotlinx.coroutines.launch

/**
 * View model for the search screen.
 */
class SearchScreenViewModel: ViewModel() {
    var uiState by mutableStateOf(SearchUiState())

    fun geocode(query: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorRecourseId = null)
                val locations = GeocodeAPI.service.geocode(query).distinct().map { it.toLocationData() }
                uiState = uiState.copy(locations = locations)
            } catch (e: Exception) {
                uiState = uiState.copy(errorRecourseId = R.string.something_went_wrong)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun reverseGeocode(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorRecourseId = null)
                val locations = GeocodeAPI.service.reverseGeocode(lat, lon).distinct().map { it.toLocationData() }
                uiState = uiState.copy(locations = locations)
            } catch (e: Exception) {
                uiState = uiState.copy(errorRecourseId = R.string.something_went_wrong)
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun clearLocations() {
        uiState = uiState.copy(locations = emptyList())
    }

}