package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.composables.WeatherPage
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel
) {
    val uiState = mainViewModel.uiState
    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.loading))
                CircularProgressIndicator()
            }
        }

        uiState.previewLocation != null -> {
            WeatherPage(
                locationWeather = uiState.previewLocation,
                onRefresh = { mainViewModel.refreshWeather() },
                isRefreshing = uiState.isRefreshing,
                navController = navController
            )
        }

        uiState.favoriteLocations.isNotEmpty() -> {
            val pagerState = rememberPagerState(
                initialPage = uiState.pageIndex,
                pageCount = { uiState.favoriteLocations.size }
            )

            LaunchedEffect(pagerState.currentPage) {
                if (uiState.pageIndex != pagerState.currentPage) {
                    mainViewModel.changePageIndex(pagerState.currentPage)
                }
            }

            HorizontalPager(
                state = pagerState
            ) { index ->
                WeatherPage(
                    locationWeather = uiState.favoriteLocations[index],
                    onRefresh = { mainViewModel.refreshWeather(index) },
                    isRefreshing = uiState.isRefreshing,
                    navController = navController
                )
            }
        }

        uiState.errorResId != null -> {
            LaunchedEffect(Unit) {
                navController.navigate("search")
            }
        }

        else -> {
            LaunchedEffect(Unit) {
                navController.navigate("search")
            }
        }
    }
}
