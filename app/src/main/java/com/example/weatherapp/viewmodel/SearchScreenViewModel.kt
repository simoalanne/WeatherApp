package com.example.weatherapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.location.GeocodingErrorCode
import com.example.weatherapp.location.GeocodingException
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.SearchUiState
import kotlinx.coroutines.launch

/**
 * View model for the search screen.
 */
class SearchScreenViewModel : ViewModel() {
    var uiState by mutableStateOf(SearchUiState())
    private lateinit var locationService: LocationService

    fun setLocationService(locationService: LocationService) {
        this.locationService = locationService
    }

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