package com.example.weatherapp.model

/**
 * Represents the accuracy of the location.
 */
// the idea was that user could choose this but the state name localization was quite poor and really
// the state is relevant only in few countries not in all the countries the geocoding API(s) still
// return state information for.
enum class LocationDisplayAccuracy {
    CITY,
    CITY_AND_COUNTRY,
    CITY_AND_STATE_AND_COUNTRY,
}
