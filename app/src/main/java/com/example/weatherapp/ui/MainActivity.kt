package com.example.weatherapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.screens.SearchScreen
import com.example.weatherapp.ui.screens.WeatherScreen
import com.example.weatherapp.ui.theme.WeatherAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.weatherapp.MyApplication
import com.example.weatherapp.location.LocationService
import com.example.weatherapp.model.WeatherPreset
import com.example.weatherapp.model.WeatherVisualsObject
import com.example.weatherapp.settingsDataStore
import com.example.weatherapp.ui.composables.BackgroundImage
import com.example.weatherapp.ui.screens.MapScreen
import com.example.weatherapp.ui.screens.OnboardingScreen
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
                val weatherDao = (application as MyApplication).weatherDao
                val locationService = LocationService(applicationContext)
                val dataStore = applicationContext.settingsDataStore

                var doesUserHaveFavoriteLocations: Boolean? by remember { mutableStateOf(null) }
                var hasUserSeenOnboarding: Boolean? by remember { mutableStateOf(null) }

                val mainVm: MainViewModel = viewModel {
                    MainViewModel(locationService, locationDao, weatherDao, onInitialDataLoaded = {
                        doesUserHaveFavoriteLocations = it
                    })
                }
                val searchScreenVm: SearchScreenViewModel =
                    viewModel { SearchScreenViewModel(locationService) }
                val settingsVm: SettingsViewModel = viewModel {
                    SettingsViewModel(dataStore, onSettingsLoaded = {
                        hasUserSeenOnboarding = it
                    })
                }
                val startDestination: String? =
                    remember(doesUserHaveFavoriteLocations, hasUserSeenOnboarding) {
                        if (doesUserHaveFavoriteLocations == null || hasUserSeenOnboarding == null) {
                            return@remember null
                        }
                        if (doesUserHaveFavoriteLocations == true) {
                            return@remember "weather"
                        }
                        if (hasUserSeenOnboarding == false) {
                            return@remember "onboarding"
                        }
                        return@remember "search"
                    }
                if (startDestination == null) {
                    BackgroundImage(
                        WeatherVisualsObject.visualsForPreset(WeatherPreset.entries.random())
                    )
                } else {
                    val navController: NavHostController = rememberNavController()
                    val tweenTime = 500
                    val forwardEnter = slideInHorizontally(
                        initialOffsetX = { it }, // from right
                        animationSpec = tween(tweenTime)
                    )
                    val forwardExit = slideOutHorizontally(
                        targetOffsetX = { -it / 2 }, // to left
                        animationSpec = tween(tweenTime)
                    )

                    val backwardEnter = slideInHorizontally(
                        initialOffsetX = { -it / 2 }, // from left
                        animationSpec = tween(tweenTime)
                    )
                    val backwardExit = slideOutHorizontally(
                        targetOffsetX = { it }, // to right
                        animationSpec = tween(tweenTime)
                    )
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        enterTransition = {
                            if (initialState.destination.route == "preview" && targetState.destination.route == "weather") {
                                null
                            }
                            forwardEnter
                        },
                        exitTransition = {
                            if (initialState.destination.route == "preview" && targetState.destination.route == "weather") {
                                null
                            }
                            forwardExit
                        },
                        popEnterTransition = {
                            backwardEnter
                        },
                        popExitTransition = {
                            backwardExit
                        }) {
                        composable(
                            route = "weather?pageIndex={pageIndex}",
                            arguments = listOf(navArgument("pageIndex") {
                                type = NavType.IntType
                                defaultValue = mainVm.uiState.pageIndex
                            })
                        ) {
                            WeatherScreen(
                                navController, mainVm
                            )
                        }
                        composable("preview") {
                            PreviewWeatherScreen(navController, mainVm)
                        }
                        composable("search") {
                            SearchScreen(navController, mainVm, searchScreenVm)
                        }
                        composable(
                            route = "settings?expandLocationColumn={expandLocationColumn}",
                            arguments = listOf(
                                navArgument("expandLocationColumn") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                })
                        ) {
                            SettingsScreen(navController, settingsVm, mainVm)
                        }
                        composable("map") {
                            MapScreen(navController, mainVm, searchScreenVm)
                        }
                        composable("onboarding") {
                            OnboardingScreen {
                                settingsVm.onOnboardingComplete()
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}
