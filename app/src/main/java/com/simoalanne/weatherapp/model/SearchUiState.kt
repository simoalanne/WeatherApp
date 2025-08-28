package com.simoalanne.weatherapp.model

/**
 * Data class representing the state of the search screen.
 *
 * @param searchResult The location data of the search result.
 * @param isLoading Whether the search is currently loading. (not used because loading is so fast)
 * @param errorRecourseId The resource ID of the error message to display.
 */
data class SearchUiState(
    val searchResult: LocationData? = null,
    val isLoading: Boolean = false,
    val errorRecourseId: Int? = null
)
