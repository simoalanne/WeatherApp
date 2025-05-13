package com.example.weatherapp.mapper

import android.util.Log
import com.example.weatherapp.R
import com.example.weatherapp.model.*
import com.example.weatherapp.model.WeatherIcons.Companion.getConditionString
import com.example.weatherapp.utils.formatLocationName
import com.example.weatherapp.utils.getHoursBetweenTwoLocalDates
import com.example.weatherapp.utils.getInterpolationWeights
import com.example.weatherapp.utils.getLocalDateTimeFromUnixTimestamp
import com.example.weatherapp.utils.isDay
import com.example.weatherapp.utils.truncateToHours
import kotlin.math.abs
import java.time.Duration

/**
 * Maps the API responses to a WeatherData object. API responses are
 * either from OpenWeatherMap or from sunrisesunset.io
 *
 * @param weatherResponse the response from the OpenWeatherMap weather endpoint
 * @param forecastResponse the response from the OpenWeatherMap forecast endpoint
 * @param sunriseSunsetResponse the response from sunrisesunset API
 * @param geoCodeEntry the geocode entry that was used to fetch the coordinates
 * @return a WeatherData object containing the mapped data
 */
fun mapApiResponsesToWeatherData(
    weatherResponse: WeatherResponse,
    forecastResponse: ForecastResponse,
    sunriseSunsetResponse: SunriseSunsetResponse,
    geoCodeEntry: GeocodeEntry,
): WeatherData {
    val timezoneOffset = forecastResponse.cityInfo.timezone
    val sunriseSunsetList = sunriseSunsetResponse.results
    val sunriseSunsetMap = sunriseSunsetList.associate {
        val sunrise = getLocalDateTimeFromUnixTimestamp(it.sunrise?.toLongOrNull() ?: 0, timezoneOffset)
        val sunset = getLocalDateTimeFromUnixTimestamp(it.sunset?.toLongOrNull() ?: 0, timezoneOffset)
        val key = sunrise.toLocalDate().toString()
        Pair(key, SunriseSunset(sunrise, sunset))
    }

    if (sunriseSunsetList.any { it.sunrise == null || it.sunset == null }) {
        Log.e("mapApiResponsesToWeatherData", "sunriseSunset response contains null values: $sunriseSunsetResponse")
        Log.e("mapApiResponsesToWeatherData", "resolved values are: $sunriseSunsetMap")
    }

    val population = forecastResponse.cityInfo.population

    val meta = Meta(
        geocodeEntry = geoCodeEntry,
        population = if (population == 0) null else population,
        timezoneOffsetInSeconds = forecastResponse.cityInfo.timezone,
        sunriseSunsetTimes = sunriseSunsetMap
    )

    val currentTime = getLocalDateTimeFromUnixTimestamp(weatherResponse.timestamp, timezoneOffset)

    val currentWeather = CurrentWeather(
        time = currentTime,
        temperature = weatherResponse.weatherInfo.temp,
        feelsLike = weatherResponse.weatherInfo.feelsLike,
        conditionId = getConditionString(weatherResponse.weatherCondition[0].code),
        iconId = WeatherIcons.fromCode(
            weatherResponse.weatherCondition[0].code, isDay(currentTime, sunriseSunsetMap)
        ),
        humidityPercentage = weatherResponse.weatherInfo.humidity,
        windSpeed = weatherResponse.wind.speed,
        airPressure = weatherResponse.weatherInfo.airPressure,
        cloudinessPercentage = weatherResponse.clouds.cloudinessPercentage,
        visibilityInMeters = weatherResponse.visibility,
        sunrise = sunriseSunsetMap[currentTime.toLocalDate().toString()]!!.sunrise,
        sunset = sunriseSunsetMap[currentTime.toLocalDate().toString()]!!.sunset
    )

    val firstHourlyForecast = HourlyWeather(
        time = currentWeather.time,
        temperature = currentWeather.temperature,
        iconId = currentWeather.iconId,
        rainProbability = (forecastResponse.forecastList.first().rainProbability * 100).toInt()
    )

    val forecastHourlyWeathers = forecastResponse.forecastList.map {
        val time = getLocalDateTimeFromUnixTimestamp(it.timestamp, timezoneOffset)
        val isDay = isDay(time, sunriseSunsetMap)
        HourlyWeather(
            time = time,
            temperature = it.weatherInfo.temp,
            iconId = WeatherIcons.fromCode(it.weatherCondition[0].code, isDay),
            rainProbability = (it.rainProbability * 100).toInt()
        )
    }

    Log.d("WeatherViewModel", "forecastHourlyWeathers: $forecastHourlyWeathers")

    val interpolated = (listOf(firstHourlyForecast) + forecastHourlyWeathers).zipWithNext { a, b ->
        interpolateHourlyWeatherData(a, b, sunriseSunsetMap)
    }.flatten()

    val hourlyForecasts = interpolated + forecastHourlyWeathers.last()

    val sunriseAndSunsetsAsHourlyWeather = meta.sunriseSunsetTimes.values.flatMap { times ->
        listOf(times.sunrise, times.sunset).map { eventTime ->
            val nearestForecast =
                hourlyForecasts.minBy { abs(Duration.between(it.time, eventTime).toMinutes()) }
            HourlyWeather(
                time = eventTime,
                temperature = nearestForecast.temperature,
                iconId = if (eventTime == times.sunrise) R.drawable.sunrise else R.drawable.sunset,
                rainProbability = nearestForecast.rainProbability,
            )
        }
    }

    // Sorts entries by time ascending then filters out entries that are before or after the forecast entries.
    // So basically filters out sunrise and sunset entries from start and end that won't fall inside the forecast hours
    val hourlyForecastWithSunriseAndSunsets =
        (hourlyForecasts + sunriseAndSunsetsAsHourlyWeather).sortedBy { it.time }.filter { entry ->
            (entry.time.isAfter(currentTime) || entry.time == currentTime) && (entry.time.isBefore(
                forecastHourlyWeathers.last().time
            ) || entry.time == forecastHourlyWeathers.last().time)
        }

    return WeatherData(meta, currentWeather, hourlyForecastWithSunriseAndSunsets)
}

private fun interpolateHourlyWeatherData(
    startWeather: HourlyWeather,
    endWeather: HourlyWeather,
    sunriseSunsetMap: Map<String, SunriseSunset>
): List<HourlyWeather> {

    val hoursBetween = getHoursBetweenTwoLocalDates(startWeather.time, endWeather.time)

    return List(hoursBetween) { index ->
        if (index == 0) {
            return@List startWeather
        }

        val weights = getInterpolationWeights(index, hoursBetween)
        val isCloserToStart = weights.first > 0.5 // for values that can't be interpolated linearly
        val time = startWeather.time.plusHours(index.toLong())
        val isDay = isDay(time, sunriseSunsetMap)
        HourlyWeather(
            time = truncateToHours(startWeather.time).plusHours(index.toLong()),
            temperature = startWeather.temperature * weights.first + endWeather.temperature * weights.second,
            iconId = WeatherIcons.fromCode(startWeather.iconId, isDay),
            rainProbability = if (isCloserToStart) startWeather.rainProbability else endWeather.rainProbability
        )
    }
}