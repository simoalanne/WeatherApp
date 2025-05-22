package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.ui.composables.Margin
import com.example.weatherapp.ui.composables.WeatherPage
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
) {
    val uiState = mainViewModel.uiState
    Log.d("WeatherScreen", "WeatherScreen recomposed $uiState")
    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.loading),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    CircularProgressIndicator()
                }
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
            Log.e("WeatherScreen", stringResource(uiState.errorResId))
            LaunchedEffect(Unit) {
                navController.navigate("search")
            }
        }
    }
}
