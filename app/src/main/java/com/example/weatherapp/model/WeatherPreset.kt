package com.example.weatherapp.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R

enum class WeatherPreset {
    DYNAMIC,
    CLEAR_DAY,
    CLEAR_NIGHT,
    CLOUDY_DAY,
    CLOUDY_NIGHT,
    RAINY_DAY,
    RAINY_NIGHT,
    SNOWY_DAY,
    SNOWY_NIGHT,
    STORMY
}

@Composable
fun WeatherPreset.localizedLabel(): String {
    return when (this) {
        WeatherPreset.DYNAMIC -> stringResource(R.string.preset_dynamic)
        WeatherPreset.CLEAR_DAY -> stringResource(R.string.preset_clear_day)
        WeatherPreset.CLEAR_NIGHT -> stringResource(R.string.preset_clear_night)
        WeatherPreset.CLOUDY_DAY -> stringResource(R.string.preset_cloudy_day)
        WeatherPreset.CLOUDY_NIGHT -> stringResource(R.string.preset_cloudy_night)
        WeatherPreset.RAINY_DAY -> stringResource(R.string.preset_rainy_day)
        WeatherPreset.RAINY_NIGHT -> stringResource(R.string.preset_rainy_night)
        WeatherPreset.SNOWY_DAY -> stringResource(R.string.preset_snowy_day)
        WeatherPreset.SNOWY_NIGHT -> stringResource(R.string.preset_snowy_night)
        WeatherPreset.STORMY -> stringResource(R.string.preset_stormy)
    }
}
