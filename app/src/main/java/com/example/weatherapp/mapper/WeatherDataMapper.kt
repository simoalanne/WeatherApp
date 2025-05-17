package com.example.weatherapp.mapper

import com.example.weatherapp.R
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.Meta
import com.example.weatherapp.model.OpenMeteoCodes
import com.example.weatherapp.model.OpenMeteoResponse
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.utils.getLocalDateTimeFromUnixTimestamp
import java.time.Duration
import kotlin.math.abs

fun OpenMeteoResponse.toWeatherData(): WeatherData {

    val meta = Meta(utcOffsetSeconds)
    val isDay = this.currentWeather.isDay == 1
    val currentWeather = CurrentWeather(
        time = getLocalDateTimeFromUnixTimestamp(this.currentWeather.time, utcOffsetSeconds),
        temperature = this.currentWeather.temperature,
        weatherIconId = OpenMeteoCodes.getIconFromCode(
            this.currentWeather.weatherCode, isDay
        ),
        conditionId = OpenMeteoCodes.getConditionFromCode(this.currentWeather.weatherCode),
        isDay = isDay
    )

    val hourlyWeatherGrouped = this.hourlyWeather.time.mapIndexed { index, time ->
        HourlyWeather(
            time = getLocalDateTimeFromUnixTimestamp(time, utcOffsetSeconds),
            temperature = this.hourlyWeather.temperature[index],
            weatherIconId = OpenMeteoCodes.getIconFromCode(
                this.hourlyWeather.weatherCode[index], this.hourlyWeather.isDay[index] == 1
            )
        )
    }.groupBy { it.time.toLocalDate() }

    val dailyWeathers = this.dailyWeather.time.mapIndexed { index, time ->
        val date = getLocalDateTimeFromUnixTimestamp(time, utcOffsetSeconds).toLocalDate()
        val dailyWeather = DailyWeather(
            date = date,
            weatherIconId = OpenMeteoCodes.getIconFromCode(
                dailyWeather.weatherCode[index], isDay = true
            ),
            maxTemperature = this.dailyWeather.maxTemperature[index],
            minTemperature = this.dailyWeather.minTemperature[index],
            sunrise = getLocalDateTimeFromUnixTimestamp(
                this.dailyWeather.sunrise[index], utcOffsetSeconds
            ),
            sunset = getLocalDateTimeFromUnixTimestamp(
                this.dailyWeather.sunset[index], utcOffsetSeconds
            ),
            hourlyWeathers = hourlyWeatherGrouped[date] ?: emptyList()
        )

        val sunriseAndSunsetAsHourly =
            listOf(dailyWeather.sunrise, dailyWeather.sunset).mapIndexed { sunIndex, time ->
                val tempToUse = dailyWeather.hourlyWeathers.minBy {
                    abs(
                        Duration.between(time, it.time).toMinutes()
                    )
                }.temperature

                HourlyWeather(
                    time = time,
                    temperature = tempToUse,
                    weatherIconId = if (sunIndex == 0) R.drawable.sunrise else R.drawable.sunset
                )
            }

        val currentWeatherAsHourly = if (index == 0) {
            HourlyWeather(
                time = currentWeather.time,
                temperature = currentWeather.temperature,
                weatherIconId = currentWeather.weatherIconId
            )
        } else null

        val newDailyWeather = (listOfNotNull(currentWeatherAsHourly).plus(sunriseAndSunsetAsHourly)
            .plus(dailyWeather.hourlyWeathers)).sortedBy { it.time }
            .filterNot { entry -> entry.time.isBefore(currentWeather.time) }

        dailyWeather.copy(hourlyWeathers = newDailyWeather)
    }

    return WeatherData(meta, currentWeather, dailyWeathers)
}
