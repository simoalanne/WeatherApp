package com.example.weatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.model.LocationRole
import com.example.weatherapp.model.TempUnit
import com.example.weatherapp.model.TimeFormat
import com.example.weatherapp.model.WeatherInfoOption
import com.example.weatherapp.model.WeatherPreset
import com.example.weatherapp.model.WindSpeedUnit
import com.example.weatherapp.model.localizedLabel
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
    val expandLocationDropDownInitially = navController.currentBackStackEntry
        ?.arguments?.getBoolean("expandLocationColumn") == true

    fun handleOptionSelected(option: WeatherInfoOption) {
        val labelOptions =
            setOf(WeatherInfoOption.LABELS_AS_ICONS, WeatherInfoOption.LABELS_AS_TEXT)

        if (option in settingsState.selectedWeatherInfoOptions) {
            val newSelection = settingsState.selectedWeatherInfoOptions - option
            if (newSelection.isEmpty() || (newSelection.size == 1 && newSelection.first() in labelOptions)) return
            settingsViewModel.setSelectedOptions(newSelection)
        } else {
            // Both of the label options can't be selected at the same time. if one already is the other
            // should be deselected
            if (option in labelOptions) {
                val newSelection =
                    (settingsState.selectedWeatherInfoOptions.filterNot { it in labelOptions } + option).toSet()
                settingsViewModel.setSelectedOptions(newSelection)
            } else {
                settingsViewModel.setSelectedOptions(settingsState.selectedWeatherInfoOptions + option)
            }
        }
    }

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
                label = stringResource(R.string.app_language), leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.Language, backgroundColor = Color.Blue
                    )
                }, options = listOf(
                    DropdownOption(
                        label = stringResource(R.string.english),
                        value = "en",
                        trailingIcon = { CountryFlag("gb") }), DropdownOption(
                        label = stringResource(R.string.finnish),
                        value = "fi",
                        trailingIcon = { CountryFlag("fi") })
                ), selectedOptions = setOf(currentLanguage), onOptionSelected = {
                    changeAppLanguage(context, it)
                })
            DropdownMenu(
                label = stringResource(R.string.weather_screen_background_image),
                leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.Image,
                        backgroundColor = Color.DarkGray
                    )
                },
                options = List(WeatherPreset.entries.size) {
                    DropdownOption(
                        label = WeatherPreset.entries[it].localizedLabel(),
                        value = WeatherPreset.entries[it]
                    )
                },
                selectedOptions = setOf(settingsState.selectedBackgroundPreset),
                onOptionSelected = {
                    settingsViewModel.setSelectedBackgroundPreset(it)
                }
            )
            DropdownMenu(
                label = stringResource(R.string.temperature_unit), leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.Thermostat, backgroundColor = Color(255, 140, 0)
                    )
                }, options = listOf(
                    DropdownOption("Celsius (°C)", TempUnit.CELSIUS),
                    DropdownOption("Fahrenheit (°F)", TempUnit.FAHRENHEIT),
                    DropdownOption("Kelvin (K)", TempUnit.KELVIN)
                ), selectedOptions = setOf(settingsState.tempUnit), onOptionSelected = {
                    settingsViewModel.setTempUnit(it)
                })
            DropdownMenu(
                label = stringResource(R.string.wind_speed_unit), leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.Air, backgroundColor = Color.DarkGray
                    )
                },
                options = listOf(
                    DropdownOption(
                        stringResource(R.string.meters_per_second),
                        WindSpeedUnit.METERS_PER_SECOND
                    ),
                    DropdownOption(
                        stringResource(R.string.kilometers_per_hour),
                        WindSpeedUnit.KILOMETERS_PER_HOUR
                    ),
                    DropdownOption(
                        stringResource(R.string.miles_per_hour),
                        WindSpeedUnit.MILES_PER_HOUR
                    )
                ),
                selectedOptions = setOf(settingsState.windSpeedUnit),
                onOptionSelected = {
                    settingsViewModel.setWindSpeedUnit(it.name)
                }
            )
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
                        stringResource(R.string.weather_icon),
                        WeatherInfoOption.WEATHER_ICON
                    ),
                    DropdownOption(
                        stringResource(R.string.temperature),
                        WeatherInfoOption.TEMPERATURE
                    ),
                    DropdownOption(
                        stringResource(R.string.feels_like),
                        WeatherInfoOption.FEELS_LIKE
                    ),
                    DropdownOption(
                        stringResource(R.string.wind_gusts),
                        WeatherInfoOption.WIND_GUSTS
                    ),
                    DropdownOption(
                        stringResource(R.string.wind_direction),
                        WeatherInfoOption.WIND_DIRECTION
                    ),
                    DropdownOption(
                        stringResource(R.string.probability_of_precipitation),
                        WeatherInfoOption.PROBABILITY_OF_PRECIPITATION
                    ),
                    DropdownOption(
                        stringResource(R.string.humidity),
                        WeatherInfoOption.HUMIDITY
                    ),
                    DropdownOption(
                        stringResource(R.string.show_labels_as_text),
                        WeatherInfoOption.LABELS_AS_TEXT
                    ),
                    DropdownOption(
                        stringResource(R.string.show_labels_as_icons),
                        WeatherInfoOption.LABELS_AS_ICONS
                    )
                ),
                selectedOptions = settingsState.selectedWeatherInfoOptions,
                onOptionSelected = { handleOptionSelected(it) }
            )
            DropdownMenu(
                label = stringResource(R.string.time_format), leadingIcon = {
                    IconWithBackground(
                        icon = Icons.Default.AccessTime, backgroundColor = Color.DarkGray
                    )
                }, options = listOf(
                    DropdownOption(
                        stringResource(R.string.twelve_hour_format), TimeFormat.TWELVE_HOUR
                    ), DropdownOption(
                        stringResource(R.string.twenty_four_hour_format),
                        TimeFormat.TWENTY_FOUR_HOUR
                    )
                ), selectedOptions = setOf(settingsState.timeFormat), onOptionSelected = {
                    settingsViewModel.setTimeFormat(it)
                })
            CurrentLocation(
                shouldBeExpanded = expandLocationDropDownInitially,
                userLocation =
                    mainViewModel.uiState.favoriteLocations.find { it.role == LocationRole.USER }?.location,
                handleUserLocate = {
                    mainViewModel.locateUser()
                })
            Margin(100)
        }
    }
}
