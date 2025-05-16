package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.LocationWeather
import com.example.weatherapp.ui.composables.UserLocation
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.SearchScreenViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    searchScreenVm: SearchScreenViewModel,
    settingsVm: SettingsViewModel
) {
    var query by remember { mutableStateOf("") }
    var isInitialLoad by remember { mutableStateOf(true) }
    val locations = searchScreenVm.uiState.locations
    val error = searchScreenVm.uiState.errorRecourseId

    LaunchedEffect(mainViewModel.uiState.currentLocation) {
        if (isInitialLoad && mainViewModel.uiState.currentLocation != null) {
            searchScreenVm.clearLocations()
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
                    text = stringResource(R.string.manage_cities), color = Color.White
                )
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ), navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            })
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
                    query = query, onQueryChange = { query = it }, onSearch = {
                        if (query.isNotBlank()) searchScreenVm.geocode(query.trim().lowercase())
                    }, modifier = Modifier.weight(0.5f)
                )
                TextButton(
                    content = { Text(text = stringResource(R.string.clear_results)) },
                    onClick = {
                        searchScreenVm.clearLocations()
                        query = ""
                    },
                    // Button should be invisible but still take up space when it's not interactable
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White, disabledContentColor = Color.Transparent
                    ),
                    enabled = locations.isNotEmpty()
                )
            }
            if (error != null) {
                Text(text = stringResource(error), color = Color.Red)
            }
            GeocodeResults(
                geocodeEntries = locations, onSelect = {
                    mainViewModel.previewLocation(it)
                })
            UserLocation(
                userLocation = mainViewModel.uiState.locations.find { it.role == LocationRole.USER },
                onLocateUser = {
                    mainViewModel.locateUser()
                },
                onLocationPress = {
                    mainViewModel.changeCurrentLocation(
                        mainViewModel.uiState.locations.indexOfFirst
                        { it.role == LocationRole.USER })
                }
            )
        }
    }
}
