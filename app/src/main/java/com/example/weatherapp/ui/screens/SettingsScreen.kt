package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.TimeFormat
import com.example.weatherapp.ui.composables.CountryFlag
import com.example.weatherapp.ui.composables.DropdownMenu
import com.example.weatherapp.utils.changeAppLanguage
import com.example.weatherapp.utils.getAppLanguage
import com.example.weatherapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, settingsViewModel: SettingsViewModel) {
    val settingsState = settingsViewModel.settingsState
    val context = LocalContext.current
    val currentLanguage = getAppLanguage(context)
    Column(
        verticalArrangement = spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.settings),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ), navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            })
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = spacedBy(24.dp)
        ) {
            DropdownMenu(
                label = Pair(
                    stringResource(R.string.app_language),
                    {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.Blue)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                ), options = listOf(
                    Triple(
                        stringResource(R.string.english),
                        "en",
                        {
                            CountryFlag("gb")
                        }),
                    Triple(
                        stringResource(R.string.finnish),
                        "fi",
                        {
                            CountryFlag("fi")
                        })
                ),
                selectedOption = currentLanguage,
                onOptionSelected = {
                    changeAppLanguage(context, it)
                }
            )
            DropdownMenu(
                label = Pair(
                    stringResource(R.string.temperature_unit),
                    {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color(255, 140, 0))
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                ),
                options = listOf(
                    Triple(
                        "Celsius (°C)",
                        TempUnit.CELSIUS,
                        {}
                    ),
                    Triple(
                        "Fahrenheit (°F)",
                        TempUnit.FAHRENHEIT
                    ) {},
                    Triple(
                        "Kelvin (K)",
                        TempUnit.KELVIN,
                        {}
                    )
                ),
                selectedOption = settingsState.tempUnit,
                onOptionSelected = {
                    settingsViewModel.setTempUnit(it)
                }
            )
            DropdownMenu(
                label = Pair(
                    stringResource(R.string.time_format),
                    {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.DarkGray)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground

                            )
                        }
                    }
                ),
                options = listOf(
                    Triple(
                        stringResource(R.string.twelve_hour_format),
                        TimeFormat.TWELVE_HOUR,
                        {}
                    ),
                    Triple(
                        stringResource(R.string.twenty_four_hour_format),
                        TimeFormat.TWENTY_FOUR_HOUR,
                        {}
                    )
                ),
                selectedOption = settingsState.timeFormat,
                onOptionSelected = {
                    settingsViewModel.setTimeFormat(it)
                }
            )
        }
    }
}
