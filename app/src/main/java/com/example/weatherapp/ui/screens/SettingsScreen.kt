package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.List
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.model.HourlyWeatherWhatToShow
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.TimeFormat
import com.example.weatherapp.ui.composables.CountryFlag
import com.example.weatherapp.ui.composables.CurrentLocation
import com.example.weatherapp.ui.composables.DropdownMenu
import com.example.weatherapp.ui.composables.DropdownOption
import com.example.weatherapp.ui.composables.IconWithBackground
import com.example.weatherapp.ui.composables.Margin
import com.example.weatherapp.utils.changeAppLanguage
import com.example.weatherapp.utils.getAppLanguage
import com.example.weatherapp.viewmodel.MainViewModel
import com.example.weatherapp.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController, settingsViewModel: SettingsViewModel, mainViewModel: MainViewModel
) {
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
            }, colors = TopAppBarDefaults.topAppBarColors(
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = spacedBy(24.dp),
        ) {
            DropdownMenu(
                label = stringResource(R.string.app_language),
                leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.Language, backgroundColor = Color.Blue
                    )
                },
                options = listOf(
                    DropdownOption(
                        label = stringResource(R.string.english),
                        value = "en",
                        trailingIcon = { CountryFlag("gb") }), DropdownOption(
                        label = stringResource(R.string.finnish),
                        value = "fi",
                        trailingIcon = { CountryFlag("fi") })
                ),
                selectedOption = currentLanguage,
                onOptionSelected = {
                    changeAppLanguage(context, it)
                })

            DropdownMenu(
                label = stringResource(R.string.temperature_unit), leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.Thermostat, backgroundColor = Color(255, 140, 0)
                    )
                }, options = listOf(
                    DropdownOption("Celsius (°C)", TempUnit.CELSIUS),
                    DropdownOption("Fahrenheit (°F)", TempUnit.FAHRENHEIT),
                    DropdownOption("Kelvin (K)", TempUnit.KELVIN)
                ), selectedOption = settingsState.tempUnit, onOptionSelected = {
                    settingsViewModel.setTempUnit(it)
                })
            DropdownMenu(
                label = stringResource(R.string.hourly_forecast_details),
                leadingIcon = {
                    IconWithBackground(
                        icon = Icons.AutoMirrored.Filled.List,
                        backgroundColor = Color.DarkGray
                    )
                },
                options = listOf(
                    DropdownOption(
                        stringResource(R.string.condition_and_temp),
                        HourlyWeatherWhatToShow.CONDITION_AND_TEMP
                    ),
                    DropdownOption(
                        stringResource(R.string.probability_of_precipitation),
                        HourlyWeatherWhatToShow.POP
                    ),
                    DropdownOption(
                        stringResource(R.string.show_both),
                        HourlyWeatherWhatToShow.BOTH
                    )
                ),
                selectedOption = settingsState.hourlyWeatherWhatToShow,
                onOptionSelected = {
                    settingsViewModel.setHourlyWeatherWhatToShow(it)
                }
            )
            DropdownMenu(
                label = stringResource(R.string.time_format), leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.Timer, backgroundColor = Color.DarkGray
                    )
                }, options = listOf(
                    DropdownOption(
                        stringResource(R.string.twelve_hour_format), TimeFormat.TWELVE_HOUR
                    ), DropdownOption(
                        stringResource(R.string.twenty_four_hour_format),
                        TimeFormat.TWENTY_FOUR_HOUR
                    )
                ), selectedOption = settingsState.timeFormat, onOptionSelected = {
                    settingsViewModel.setTimeFormat(it)
                })
            CurrentLocation(
                userLocation = mainViewModel.uiState.favoriteLocations.find { it.role == LocationRole.USER }?.location,
                handleUserLocate = {
                    mainViewModel.locateUser()
                })
            Margin(100)
        }
    }
}
