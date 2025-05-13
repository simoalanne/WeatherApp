package com.example.weatherapp.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.composables.Margin
import com.example.weatherapp.R

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
    val isLoading = weatherViewModel.isLoading


    // Navigate back to the WeatherScreen when the weather data is fetched
    // isInitialLoad is needed because otherwise this would instantly navigate
    // back to the WeatherScreen when user comes here.
    LaunchedEffect(weatherViewModel.weather) {
        if (isInitialLoad && weatherViewModel.weather != null) {
            weatherViewModel.resetGeocodeEntries()
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
            title = {
                Text(
                    text = stringResource(R.string.manage_cities),
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = stringResource(R.string.back)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = spacedBy(8.dp),
            ) {
                SearchTextField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { if (query.isNotBlank()) weatherViewModel.fetchGeocodeEntries(query) },
                    modifier = Modifier.weight(0.5f)
                )
                TextButton(
                    content = { Text(text = stringResource(R.string.clear_results)) },
                    onClick = {
                        weatherViewModel.resetGeocodeEntries()
                        query = ""
                    },
                    // Button should be invisible but still take up space when it's not interactable
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White,
                        disabledContentColor = Color.Transparent
                    ),
                    enabled = geocodeEntries.isNotEmpty(),
                )
            }
            if (geocodeEntries.isNotEmpty()) {
                GeocodeResults(
                    geocodeEntries = geocodeEntries,
                    onSelect = { coordinates, displayName ->
                        weatherViewModel.fetchWeatherData(
                            coordinates,
                            displayName
                        )
                    }
                )
            }
            if (isLoading) {
                Margin(100)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CircularProgressIndicator()
                        Text(text = stringResource(R.string.loading), color = Color.White)
                    }
                }
            }
            if (error != null) {
                Text(text = stringResource(error), color = Color(red = 255, green = 155, blue = 155))
            }
        }
    }
}
