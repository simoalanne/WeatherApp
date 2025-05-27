package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun WeatherList(
    hourlyWeathers: List<HourlyWeather>,
    isNext24Hours: Boolean = true,
) {

    val horizontalScrollBlocker = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset, available: Offset, source: NestedScrollSource
                // the box composable below will consume the remaining horizontal scroll if any
            ): Offset = available.copy(y = 0f)
        }
    }

    Box(
        modifier = Modifier
            // nested scroll here is used to capture the remaining horizontal scroll that would otherwise
            // go all the way to the pager composable which is not desired here because that makes it
            // too easy to accidentally shift the page when just trying to scroll the lazy row.
            // AI helped explaining the default behaviour of scrolling and suggested a solution
            // similar to what I've ended up using in the end.
            .nestedScroll(horizontalScrollBlocker)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.3f))
                .padding(12.dp)
        ) {
            itemsIndexed(hourlyWeathers) { index, hourlyWeather ->
                val time =
                    if (index == 0 && isNext24Hours) stringResource(R.string.now) else formatLocalDateTime(
                        hourlyWeather.time
                    )
                WeatherListItem(
                    time, hourlyWeather.weatherIconId, hourlyWeather.temperature, hourlyWeather.pop
                )
            }
        }
    }
}
