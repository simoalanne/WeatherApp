package com.simoalanne.weatherapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simoalanne.weatherapp.R
import com.simoalanne.weatherapp.location.GeocodingErrorCode
import com.simoalanne.weatherapp.location.GeocodingException
import com.simoalanne.weatherapp.location.LocationService
import com.simoalanne.weatherapp.model.SearchUiState
import kotlinx.coroutines.launch

/**
 * View model for the search screen.
 */
class SearchScreenViewModel(private val locationService: LocationService) : ViewModel() {
    var uiState by mutableStateOf(SearchUiState())

    fun geocode(query: String) {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorRecourseId = null)
                val location = locationService.geocode(query)
                uiState = uiState.copy(searchResult = location)
            } catch (e: GeocodingException) {
                uiState = when (e.errorCode) {
                    GeocodingErrorCode.NO_RESULTS -> {
                        uiState.copy(errorRecourseId = R.string.no_results)
                    }

                    GeocodingErrorCode.INVALID_ADDRESS -> {
                        uiState.copy(errorRecourseId = R.string.no_results)
                    }

                    GeocodingErrorCode.GEOCODING_FAILURE -> {
                        uiState.copy(errorRecourseId = R.string.something_went_wrong)
                    }
                }
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun reverseGeocode(lat: Double, lon: Double) {
        if (uiState.searchResult != null) {
            return
        }
        viewModelScope.launch {
            try {
                uiState = uiState.copy(isLoading = true, errorRecourseId = null)
                val location = locationService.reverseGeocode(lat, lon)
                uiState = uiState.copy(searchResult = location)
            } catch (e: GeocodingException) {
            } finally {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun clearLocations() {
        uiState = uiState.copy(searchResult = null, errorRecourseId = null)
    }
}