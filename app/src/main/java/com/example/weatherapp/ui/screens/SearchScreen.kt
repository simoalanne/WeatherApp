package com.example.weatherapp.ui.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.weatherapp.ui.composables.SearchTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationAndRole
import com.example.weatherapp.model.LocationData
import com.example.weatherapp.ui.composables.FavoriteLocationsList
import com.example.weatherapp.ui.composables.LocationItem
import com.example.weatherapp.ui.composables.Margin
import com.example.weatherapp.ui.composables.NoLocationsCta
import com.example.weatherapp.ui.composables.SwipeableItem
import com.example.weatherapp.utils.rememberCurrentLanguageCode
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.SearchScreenViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    mainViewModel: MainViewModel,
    searchScreenVm: SearchScreenViewModel,
) {
    var query by remember { mutableStateOf("") }
    var isInitialLoad by remember { mutableStateOf(true) }
    val searchResult = searchScreenVm.uiState.searchResult
    var currentSearchResult by remember { mutableStateOf<LocationData?>(null) }
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current
    val isFocused by interactionSource.collectIsFocusedAsState()
    val error = searchScreenVm.uiState.errorRecourseId
    LaunchedEffect(mainViewModel.uiState.previewLocation) {
        if (isInitialLoad) {
            isInitialLoad = false
            searchScreenVm.clearLocations()
        } else if (mainViewModel.uiState.previewLocation != null) {
            navController.navigate("preview")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.manage_cities),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ), navigationIcon = {
                IconButton(onClick = {
                    if (mainViewModel.uiState.favoriteLocations.isNotEmpty()) {
                        navController.popBackStack()
                    }
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }, actions = {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(
                        Icons.Default.Settings,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = stringResource(R.string.settings)
                    )
                }
            }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(175.dp)
            ) {
                Row(
                    horizontalArrangement = if (isFocused) spacedBy(8.dp) else Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SearchTextField(
                        query = query, onQueryChange = { query = it }, onSearch = {
                            if (query.isNotBlank()) searchScreenVm.geocode(
                                query.trim().lowercase(),
                            )
                        }, modifier = Modifier
                            .focusRequester(focusRequester)
                            .weight(0.5f),
                        interactionSource = interactionSource
                    )
                    if (isFocused) {
                        TextButton(
                            onClick = {
                                searchScreenVm.clearLocations()
                                query = ""
                                focusManager.clearFocus()
                            }
                        ) {
                            Text(stringResource(R.string.cancel_search))
                        }
                    }
                }
                if (!isFocused) {
                    Text(
                        stringResource(R.string.or),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = TextDecoration.Underline
                    )
                    Button(
                        onClick = { navController.navigate("map") },
                    ) {
                        Text(stringResource(R.string.search_from_map))
                    }
                }
                if (error != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(error),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    if (searchResult != null) {
                        SwipeableItem(
                            contentKey = searchResult,
                            onAnimationEnd = { currentSearchResult = searchResult },
                            content = {
                                LocationItem(
                                    location = currentSearchResult,
                                    onLocationTap = {
                                        if (mainViewModel.uiState.previewLocation?.location == currentSearchResult) {
                                            navController.navigate("preview")
                                            return@LocationItem
                                        }
                                        val alreadyFavoriteIndex =
                                            mainViewModel.uiState.favoriteLocations.indexOfFirst {
                                                it.location.englishName == searchResult.englishName
                                            }
                                        if (alreadyFavoriteIndex != -1) {
                                            mainViewModel.changePageIndex(alreadyFavoriteIndex)
                                            navController.popBackStack()
                                        } else {
                                            mainViewModel.previewLocation(searchResult)
                                        }
                                    }
                                )
                            })
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.favorite_locations),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge
                )
                if (mainViewModel.uiState.favoriteLocations.isEmpty()) {
                    NoLocationsCta(
                        onSearchClick = { focusRequester.requestFocus() },
                        onSettingsClick = { navController.navigate("settings") }
                    )
                } else {
                    FavoriteLocationsList(
                        favoriteLocations = mainViewModel.uiState.favoriteLocations.map {
                            LocationAndRole(
                                it.location,
                                it.role
                            )
                        },
                        shouldPlayAnimation = isInitialLoad,
                        onLocationPress = { index ->
                            mainViewModel.changePageIndex(index)
                            navController.popBackStack()
                        },
                        onLocationDelete = { location ->
                            mainViewModel.removeFavoriteLocation(
                                location.location
                            )
                        }
                    )
                    Margin(100)
                }
            }
        }
    }
}
