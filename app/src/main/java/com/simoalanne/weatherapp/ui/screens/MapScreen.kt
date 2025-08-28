package com.simoalanne.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.simoalanne.weatherapp.R
import com.simoalanne.weatherapp.ui.composables.CountryFlag
import com.simoalanne.weatherapp.utils.formatLocationName
import com.simoalanne.weatherapp.utils.rememberCurrentLanguageCode
import com.simoalanne.weatherapp.viewmodel.MainViewModel
import com.simoalanne.weatherapp.viewmodel.SearchScreenViewModel
import dev.sargunv.maplibrecompose.compose.ClickResult
import dev.sargunv.maplibrecompose.compose.MaplibreMap
import dev.sargunv.maplibrecompose.core.GestureSettings
import dev.sargunv.maplibrecompose.core.OrnamentSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    searchScreenViewModel: SearchScreenViewModel
) {
    val searchResult = searchScreenViewModel.uiState.searchResult
    val languageCode = rememberCurrentLanguageCode()
    var isInitialLoad by remember { mutableStateOf(true) }
    LaunchedEffect(mainViewModel.uiState.previewLocation) {
        if (isInitialLoad) {
            isInitialLoad = false
        } else if (mainViewModel.uiState.previewLocation != null) {
            navController.navigate("preview")
        }
    }
    val gestureSettings = if (searchResult != null) {
        GestureSettings(
            isTiltGesturesEnabled = false,
            isZoomGesturesEnabled = false,
            isRotateGesturesEnabled = false,
            isScrollGesturesEnabled = false
        )
    } else {
        GestureSettings(
            isTiltGesturesEnabled = false,
            isZoomGesturesEnabled = true,
            isRotateGesturesEnabled = false,
            isScrollGesturesEnabled = true
        )
    }
    MaplibreMap(
        styleUri = "https://tiles.openfreemap.org/styles/liberty",
        gestureSettings = gestureSettings,
        ornamentSettings = OrnamentSettings(
            isScaleBarEnabled = false
        ),
        onMapClick = { pos, offset ->
            searchScreenViewModel.reverseGeocode(pos.latitude, pos.longitude)
            ClickResult.Consume
        })
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.map_search),
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black.copy(0.7f)
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
    }
    if (searchResult != null) {
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp, start = 8.dp, end = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.weight(0.75f),
                    horizontalArrangement = spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatLocationName(searchResult, languageCode = languageCode),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                    )
                    CountryFlag(
                        countryCode = searchResult.countryCode,
                    )
                }
                Row(
                    modifier = Modifier.weight(0.25f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val alreadyFavoriteIndex =
                                mainViewModel.uiState.favoriteLocations.indexOfFirst {
                                    it.location.englishName == searchResult.englishName
                                }
                            if (alreadyFavoriteIndex != -1) {
                                mainViewModel.changePageIndex(alreadyFavoriteIndex)
                                navController.navigate("weather")
                            } else if (mainViewModel.uiState.previewLocation?.location?.englishName == searchResult.englishName) {
                                navController.navigate("preview")
                            } else {
                                mainViewModel.previewLocation(searchResult)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(144,213,255),
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Cloud,
                            tint = Color.White,
                            contentDescription = stringResource(R.string.show_weather),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { searchScreenViewModel.clearLocations() },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint = Color.White,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
