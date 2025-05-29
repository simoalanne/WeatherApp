package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.utils.formatLocalDateTime
import com.example.weatherapp.R
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.ui.Alignment
import com.example.weatherapp.model.WeatherInfoOption
import com.example.weatherapp.viewmodel.AppPreferences

/**
 * Renders a list of hourly weather items. the UX here isn't the best because originally there was
 * not meant to be as many items stacked up in the WeatherListItem composable. Swapping from lazy row
 * to lazy column and switching WeatherListItems to rows would work better but run out of time to do that
 */
@Composable
fun WeatherList(
    hourlyWeathers: List<HourlyWeather>,
    isNext24Hours: Boolean = true
) {
    val showLabels = WeatherInfoOption.LABELS in AppPreferences.preferences.selectedWeatherInfoOptions
    val horizontalScrollBlocker = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset, available: Offset, source: NestedScrollSource
                // the box composable below will consume the remaining horizontal scroll if any
            ): Offset = available.copy(y = 0f)
        }
    }

    Row(
        horizontalArrangement = spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            // nested scroll here is used to capture the remaining horizontal scroll that would otherwise
            // go all the way to the pager composable which is not desired here because that makes it
            // too easy to accidentally shift the page when just trying to scroll the lazy row.
            // AI helped explaining the default behaviour of scrolling and suggested a solution
            // similar to what I've ended up using in the end.
            .nestedScroll(horizontalScrollBlocker)
    ) {
        if (showLabels) {
            WeatherItemLabels()
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                // the whole row should block scrolling even if there arent enough items to fill it
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.1f))
                .padding(8.dp)
        ) {
            itemsIndexed(hourlyWeathers) { index, hourlyWeather ->
                val time =
                    if (index == 0 && isNext24Hours) stringResource(R.string.now) else formatLocalDateTime(
                        hourlyWeather.time
                    )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    WeatherListItem(
                        time, hourlyWeather
                    )
                }
            }
        }
    }
}
