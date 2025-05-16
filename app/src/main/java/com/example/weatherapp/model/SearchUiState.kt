package com.example.weatherapp.model

data class SearchUiState(
    val locations: List<LocationData> = emptyList(),
    val isLoading: Boolean = false,
    val errorRecourseId: Int? = null
)
