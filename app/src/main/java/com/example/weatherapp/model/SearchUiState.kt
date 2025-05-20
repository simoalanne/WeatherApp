package com.example.weatherapp.model

data class SearchUiState(
    val searchResult: LocationData? = null,
    val isLoading: Boolean = false,
    val errorRecourseId: Int? = null
)
