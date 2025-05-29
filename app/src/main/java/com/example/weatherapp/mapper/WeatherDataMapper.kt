package com.example.weatherapp.mapper

import com.example.weatherapp.R
import com.example.weatherapp.model.CurrentWeather
import com.example.weatherapp.model.DailyWeather
import com.example.weatherapp.model.HourlyWeather
import com.example.weatherapp.model.Meta
import com.example.weatherapp.model.OpenMeteoCodes
import com.example.weatherapp.model.OpenMeteoResponse
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.model.WeatherInput
import com.example.weatherapp.model.WeatherVisuals
import com.example.weatherapp.model.WeatherVisualsObject
import com.example.weatherapp.utils.getLocalDateTimeFromUnixTimestamp
import com.example.weatherapp.utils.truncateToHours

fun OpenMeteoResponse.toWeatherData(): WeatherData {

    val meta = Meta(utcOffsetSeconds)
    val isDay = currentWeather.isDay == 1
    val currentTime = currentWeather.time

    val sunrise = (if (isDay) {
        dailyWeather.sunrise.filter { it < currentTime }.minByOrNull { currentTime - it }
    } else {
        dailyWeather.sunrise.filter { it > currentTime }.minByOrNull { it - currentTime }
    })?.let { getLocalDateTimeFromUnixTimestamp(it, utcOffsetSeconds) }
        ?: getLocalDateTimeFromUnixTimestamp(0, 0)


    val sunset = (if (isDay) {
        dailyWeather.sunset.filter { it > currentTime }.minByOrNull { it - currentTime }
    } else {
        dailyWeather.sunset.filter { it < currentTime }.minByOrNull { currentTime - it }
    })?.let { getLocalDateTimeFromUnixTimestamp(it, utcOffsetSeconds) }
        ?: getLocalDateTimeFromUnixTimestamp(0, 0)

    val currentWeather = CurrentWeather(
        time = getLocalDateTimeFromUnixTimestamp(this.currentWeather.time, utcOffsetSeconds),
        temperature = this.currentWeather.temperature,
        weatherIconId = OpenMeteoCodes.getIconFromCode(
            this.currentWeather.weatherCode, isDay
        ),
        conditionId = OpenMeteoCodes.getConditionFromCode(this.currentWeather.weatherCode),
        isDay = isDay,
        sunrise = sunrise,
        sunset = sunset,
        weatherVisuals = WeatherVisualsObject.visualsForPreset(
            OpenMeteoCodes.presetForWeatherCode(
                this.currentWeather.weatherCode, isDay
            )
        )
    )

    val hourlyWeatherGrouped = this.hourlyWeather.time.mapIndexed { index, time ->
        HourlyWeather(
            time = getLocalDateTimeFromUnixTimestamp(time, utcOffsetSeconds),
            temperature = this.hourlyWeather.temperature[index],
            weatherIconId = OpenMeteoCodes.getIconFromCode(
                this.hourlyWeather.weatherCode[index], this.hourlyWeather.isDay[index] == 1
            ),
            pop = this.hourlyWeather.pop[index],
            windGusts = this.hourlyWeather.windGusts[index],
            windDirection = this.hourlyWeather.flippedWindDirection[index],
            humidity = this.hourlyWeather.humidity[index],
            feelsLike = this.hourlyWeather.feelsLike[index]
        )
    }.groupBy { it.time.toLocalDate() }

    val dailyWeathers = this.dailyWeather.time.mapIndexed { index, time ->
        val date = getLocalDateTimeFromUnixTimestamp(time, utcOffsetSeconds).toLocalDate()
        // if is the current day copy the current weather data as hourly data to the hourly list
        // as pop doesn't exist for current use the closest entry's pop for that instead
        val newHourlyWeathers = if (index == 1) {
            val closestEntry = hourlyWeatherGrouped[date]?.find {
                it.time == truncateToHours(currentWeather.time)
            } ?: hourlyWeatherGrouped[date]?.first()
            val currentWeatherAsHourly = HourlyWeather(
                time = currentWeather.time,
                temperature = currentWeather.temperature,
                weatherIconId = currentWeather.weatherIconId,
                pop = closestEntry?.pop ?: 0,
                windGusts = closestEntry?.windGusts ?: 0.0,
                windDirection = closestEntry?.windDirection ?: 0,
                humidity = closestEntry?.humidity ?: 0,
                feelsLike = closestEntry?.feelsLike ?: 0.0,
            )
            // If current time is even hour, then remove the duplicated hourly weather entry
            // as the current weather should be more accurate than the closest entry
            (listOf(currentWeatherAsHourly) + (hourlyWeatherGrouped[date]
                ?: emptyList())).distinctBy { it.time }
        } else {
            hourlyWeatherGrouped[date] ?: emptyList()
        }

        val dailyWeather = DailyWeather(
            date = date,
            weatherIconId = OpenMeteoCodes.getIconFromCode(
                dailyWeather.weatherCode[index], isDay = true
            ),
            maxTemperature = this.dailyWeather.maxTemperature[index],
            minTemperature = this.dailyWeather.minTemperature[index],
            meanTemperature = this.dailyWeather.meanTemperature[index],
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
                val entryToUse = dailyWeather.hourlyWeathers.find {
                    it.time == truncateToHours(time)
                } ?: dailyWeather.hourlyWeathers.first()

                HourlyWeather(
                    time = time,
                    temperature = entryToUse.temperature,
                    weatherIconId = if (sunIndex == 0) R.drawable.sunrise else R.drawable.sunset,
                    pop = entryToUse.pop,
                    windGusts = entryToUse.windGusts,
                    windDirection = entryToUse.windDirection,
                    humidity = entryToUse.humidity,
                    feelsLike = entryToUse.feelsLike
                )
            }

        val newDailyWeather = (sunriseAndSunsetAsHourly
            .plus(newHourlyWeathers)).sortedBy { it.time }
            // exclude any weather or sunrise/sunset from the past
            .filterNot { it.time.isBefore(currentWeather.time) }


        dailyWeather.copy(hourlyWeathers = newDailyWeather)
        // exclude empty days so essentially the yesterdays that above filter makes empty
    }.filterNot { it.hourlyWeathers.isEmpty() }

    return WeatherData(meta, currentWeather, dailyWeathers)
}
