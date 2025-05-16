package com.example.weatherapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.weatherapp.location.UserLocationProvider
import com.example.weatherapp.settingsDataStore
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
                val userLocationProvider =
                    UserLocationProvider(LocationServices.getFusedLocationProviderClient(this))
                val dataStore = applicationContext.settingsDataStore

                // Create the view models
                val mainVm: MainViewModel = viewModel()
                val searchScreenVm: SearchScreenViewModel = viewModel()
                val settingsVm: SettingsViewModel = viewModel()

                val navController: NavHostController = rememberNavController()

                LaunchedEffect(Unit) {
                    // Inject the dependencies manually, no Hilt or other fancy tools required for now
                    mainVm.setLocationDao(locationDao)
                    mainVm.setUserLocationProvider(userLocationProvider)
                    settingsVm.setDataStore(dataStore)

                    mainVm.loadInitialData()
                    settingsVm.loadSettings()
                    Log.d("MainActivity", "onCreate: ${mainVm.uiState.locations}")

                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "weather",
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(500)
                            )
                        },
                        exitTransition = {
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
                            WeatherScreen(navController, mainVm, settingsVm)
                        }
                        composable("search") {
                            SearchScreen(navController, mainVm, searchScreenVm, settingsVm)
                        }
                    }
                }
            }
        }
    }
}
