package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.weatherapp.ui.composables.GeocodeResults
import com.example.weatherapp.ui.composables.SearchTextField
import com.example.weatherapp.viewmodel.WeatherViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, weatherViewModel: WeatherViewModel) {
    // TODO: This screen should display users favorite locations as well current location if
    // TODO: location permission is granted. Additionally could have an option to navigate to
    // TODO: new screen where users can search for cities in a world map.
    var query by remember { mutableStateOf("") }
    var isInitialLoad by remember { mutableStateOf(true) }
    val geocodeEntries = weatherViewModel.geocodeEntries
    val error = weatherViewModel.error


    // Navigate back to the WeatherScreen when the weather data is fetched
    // isInitialLoad is needed because otherwise this would instantly navigate
    // back to the WeatherScreen when user comes here.
    LaunchedEffect(weatherViewModel.weather) {
        if (isInitialLoad && weatherViewModel.weather != null) {
            isInitialLoad = false
        } else {
            navController.popBackStack()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)

    ) {
        TopAppBar(
            title = { Text(text = "Search for location", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Back"
                    )
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = spacedBy(16.dp)
        ) {
            SearchTextField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { weatherViewModel.fetchGeocodeEntries(query) })
            if (geocodeEntries.isNotEmpty()) {
                GeocodeResults(
                    geocodeEntries = geocodeEntries,
                    onSelect = { coordinates, displayName -> weatherViewModel.fetchWeatherData(coordinates, displayName) })
            }
            if (error != null) {
                Text(text = error, color = Color.Red)
            }
        }
    }
}
