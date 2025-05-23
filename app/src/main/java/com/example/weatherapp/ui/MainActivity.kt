package com.example.weatherapp.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.screens.SearchScreen
import com.example.weatherapp.ui.screens.WeatherScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.android.gms.location.LocationServices
import androidx.compose.runtime.LaunchedEffect
import com.example.weatherapp.MyApplication
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.settingsDataStore
import com.example.weatherapp.ui.screens.PreviewWeatherScreen
import com.example.weatherapp.ui.screens.SettingsScreen
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.SearchScreenViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                // Create the dependencies
                val locationDao = (application as MyApplication).locationDao
                val locationService = LocationService(applicationContext)
                val dataStore = applicationContext.settingsDataStore

                // Create the view models
                val mainVm: MainViewModel = viewModel()
                val searchScreenVm: SearchScreenViewModel = viewModel()
                val settingsVm: SettingsViewModel = viewModel()

                val navController: NavHostController = rememberNavController()

                LaunchedEffect(Unit) {
                    // Inject the dependencies manually, no Hilt or other fancy tools required for now
                    mainVm.setLocationDao(locationDao)
                    mainVm.setLocationService(locationService)
                    settingsVm.setDataStore(dataStore)
                    searchScreenVm.setLocationService(locationService)

                    mainVm.loadInitialData()
                    settingsVm.loadSettings()
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "weather",
                        enterTransition = {
                            if (initialState.destination.route == "preview" && targetState.destination.route == "weather") {
                                null
                            }
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
                            if (initialState.destination.route == "preview" && targetState.destination.route == "weather") {
                                null
                            }
                            slideOutHorizontally(
                                targetOffsetX = { -it / 2 },
                                animationSpec = tween(500)
                            )
                        },
                        popEnterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { -it / 2 },
                                animationSpec = tween(500)
                            )
                        },
                        popExitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it },
                                animationSpec = tween(500)
                            )
                        }
                    ) {
                        composable("weather") {
                            WeatherScreen(navController, mainVm)
                        }
                        composable("preview") {
                            PreviewWeatherScreen(navController, mainVm)
                        }
                        composable("search") {
                            SearchScreen(navController, mainVm, searchScreenVm)
                        }
                        composable("settings") {
                            SettingsScreen(navController, settingsVm, mainVm)
                        }
                    }
                }
            }
        }
    }
}
