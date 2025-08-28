package com.simoalanne.weatherapp.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.simoalanne.weatherapp.model.LocationData
import com.simoalanne.weatherapp.network.NominatimAPI
import kotlinx.coroutines.tasks.await
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * This class turns the build in awkward callback and java based Geocoder API into a more
 * coroutine-friendly abstraction for this weather app's needs. It handles returning the results as [LocationData]
 * objects containing the geocoded information in both English and Finnish. It also supports retrieving
 * the users current location and reverse geocoding it. All errors are wrapped in custom exceptions
 * for better error handling.
 *
 * @param context The application context required for the Geocoder and FusedLocationProviderClient.
 *
 */
class LocationService(context: Context) {
    private sealed class GeocodeAction {
        data class Forward(val locationName: String) : GeocodeAction()
        data class Reverse(val lat: Double, val lon: Double) : GeocodeAction()
    }

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    // Since the App will NOT support more than these two languages it's fine to just hardcode
    // the logic around these two. If more languages were supported this would need major changes
    // and likely couldn't just run more than few more geocoders in parallel without hitting
    // hidden rate limits. Additionally adding more languages would make handling edge cases harder
    // and the behaviour couldn't be as fine tuned as it is now. For more languages
    // a high quality geocoding REST API would be a better solution. I also did try the geocoding API
    // Open-Meteo has but it wasn't even close to as good as this build-in one and would not have had
    // reverse geocode support anyway meaning i would have still needed to use this or other REST API
    // for reverse geocoding.
    private val enGeocoder = Geocoder(context, Locale.ENGLISH)
    private val fiGeocoder = Geocoder(context, Locale("fi"))

    /**
     * Gets the user's location and returns it as a [LocationData] object.
     *
     * @return The user's location as a [LocationData] object.
     * @throws [UserLocatingException] if permissions not granted or location not found.
     * @throws [GeocodingException] if geocoding the coordinates fails.
     */
    suspend fun getUserLocation(): LocationData {
        try {
            val location = fusedClient.lastLocation.await() ?: fusedClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()


            if (location == null) {
                throw UserLocatingException(UserLocatingErrorCode.NO_USER_LOCATION_FOUND)
            }
            return reverseGeocode(location.latitude, location.longitude)

        } catch (_: SecurityException) {
            throw UserLocatingException(UserLocatingErrorCode.NO_LOCATION_PERMISSION)
        }
    }

    /**
     * Reverse geocodes a location to a [LocationData] object.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return The resulting location as a [LocationData] object.
     * @throws [GeocodingException] if geocoding fails.
     */
    suspend fun reverseGeocode(lat: Double, lon: Double): LocationData {
        val result = geocodeAction(GeocodeAction.Reverse(lat, lon))
        Log.d("LocationService", "Reverse geocoded $lat, $lon to $result")
        return result
    }

    /**
     * Geocodes a location name to a [LocationData] object.
     *
     * @param locationName The name of the location to geocode.
     * @return The resulting location as a [LocationData] object.
     * @throws [GeocodingException] if geocoding fails.
     */
    suspend fun geocode(locationName: String): LocationData = coroutineScope {
        // To make localization more consistent I tried following but they didn't lead to any improvements:
        // 1. Resolve location name via en geocode. then reverse geo the fi version with lat and lon from en response
        // 2. Resolve location name via en geocode. then use the address as formatted string for fi geocode.
        geocodeAction(GeocodeAction.Forward(locationName))
    }

    /**
     * Performs a geocode action and returns the resulting location as a [LocationData] object.
     * The action can be either a forward or reverse geocode. for reverse geocode it will first
     * use the nominatim reverse geocode api from which it will take the geocoded address and
     * then it will forward geocode that address to the en and fi geocoders. This ensures that both
     * forward and reverse geocode addresses should resolve to the same location with correct
     * localization.
     *
     * @param action The action to perform. either Forward or Reverse geocoding
     * @return The resulting location as a [LocationData] object.
     * @throws [GeocodingException] if geocoding fails.
     */
    private suspend fun geocodeAction(action: GeocodeAction): LocationData = coroutineScope {
        val newAction = if (action is GeocodeAction.Reverse) {
            try {
                // if the action is reverse the query string sent to the geocoder comes from nominatim
                // this is bit stupid and not guaranteed to be consistent but the problem was thar since
                // reverse geocoder in the geocoding class is just bad then it can't be used. For simoalanne
                // it would not differentiate cities in many countries and would just return the state
                // instead of the actual city.
                val geoAddress =
                    NominatimAPI.service.reverseGeocode(action.lat, action.lon).address.geoAddress
                Log.d("LocationService", "Reverse geocoded $action to $geoAddress")
                GeocodeAction.Forward(geoAddress)
            } catch (e: Exception) {
                Log.e("LocationService", "Reverse geocoding failed", e)
                action
            }
        } else {
            action
        }


        val enAddressDeferred = async { enGeocoder.performGeocodeAction(newAction) }
        val fiAddressDeferred = async { fiGeocoder.performGeocodeAction(newAction) }

        val enAddress = enAddressDeferred.await()
        val fiAddress = fiAddressDeferred.await()
        Log.d("LocationService", "Geocoded $enAddress to $fiAddress")
        when {
            // Finnish places should always use fi address for both because they are more accurate
            // eg. the english locale may have wrong admin area name for the place
            fiAddress != null && fiAddress.countryCode == "FI" -> Pair(
                fiAddress,
                fiAddress
            ).toLocationData()
            // when both present use both normally
            fiAddress != null && enAddress != null -> Pair(enAddress, fiAddress).toLocationData()
            // when no fi use en for both
            fiAddress == null && enAddress != null -> Pair(enAddress, enAddress).toLocationData()
            // if english is null and place is not in finland throw error
            else -> throw GeocodingException(GeocodingErrorCode.NO_RESULTS)
        }
    }

    /**
     * Performs either a forward or reverse geocode action using the appropriate method. AI helped
     * here on how to turn the callback based interface into a suspend function that works with
     * coroutines. Possibly could have just used the deprecated functions for newer android versions
     * but wanted to do it the "right way" and learning how the suspendCancellableCoroutine is used
     * was useful to learn.
     *
     * @param action The action to perform. either Forward or Reverse geocoding
     * @return The resulting address or null if no results
     * @throws [GeocodingException] if geocoding fails.
     */
    private suspend fun Geocoder.performGeocodeAction(action: GeocodeAction): Address? =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val listener = object : Geocoder.GeocodeListener {
                    override fun onGeocode(results: MutableList<Address>) {
                        continuation.resumeWith(Result.success(results.firstOrNull()))
                    }

                    override fun onError(errorMessage: String?) {
                        continuation.resumeWithException(
                            GeocodingException(GeocodingErrorCode.GEOCODING_FAILURE)
                        )
                    }
                }
                when (action) {
                    is GeocodeAction.Forward -> {
                        getFromLocationName(action.locationName, 1, listener)
                    }
                    // This is never used in the final version of the app (Replaced by Nominatim)
                    is GeocodeAction.Reverse -> {
                        getFromLocation(action.lat, action.lon, 1, listener)
                    }
                }
            }
        }

    /**
     * Turns a pair of addresses into a [LocationData] object.
     *
     * @return The resulting location as a [LocationData] object.
     * @throws [GeocodingException] if geocoding fails.
     */
    private fun Pair<Address, Address>.toLocationData(): LocationData {
        val (enAddress, fiAddress) = this
        enAddress.isValid()
        fiAddress.isValid()
        return LocationData(
            englishName = enAddress.formatAddress(),
            finnishName = fiAddress.formatAddress(Locale("fi")),
            lat = enAddress.latitude,
            lon = enAddress.longitude,
            // Convert UK nations to countries because that information is more valuable than
            // something being just in the UK. The country code used here is not an official one
            // but rather what FlagCDN what this app uses for async images expects for these "countries"
            countryCode = when (enAddress.adminArea) {
                "England" -> "gb-eng"
                "Scotland" -> "gb-sct"
                "Wales" -> "gb-wls"
                "Northern Ireland" -> "gb-nir"
                else -> enAddress.countryCode
            }
        )
    }

    /**
     * Checks if the address is valid. Its considered valid if it has either a city or region.
     * essentially anything but just a country is considered valid. This however does cause a side-effect
     * where England, Scotland etc alone are considered valid.
     */
    // TODO: England, Scotland, Wales and Northern Ireland should not be considered valid cause they're too broad
    // TODO: Additionally some mini countries like vatican or monaco etc should be considered valid
    private fun Address.isValid() {
        val isValidCity = locality != null && countryName != null
        val isValidRegion = adminArea != null && countryName != null
        if (!isValidCity && !isValidRegion) {
            throw GeocodingException(GeocodingErrorCode.INVALID_ADDRESS)
        }
    }

    /**
     * Formats the address for the given locale. For the Finnish locale it handles translating
     * the admin are name to correct Finnish form.
     *
     * @param locale The locale to format the address for.
     * @return The formatted address.
     */
    private fun Address.formatAddress(locale: Locale = Locale.ENGLISH): String {
        val countryName = if (countryCode == "GB") {
            if (locale.language == "fi") {
                when (adminArea) {
                    "England", "Englanti" -> "Englanti"
                    "Scotland", "Skotlanti" -> "Skotlanti"
                    "Wales" -> "Wales"
                    "Northern Ireland", "Pohjois-Irlanti" -> "Pohjois-Irlanti"
                    else -> countryName
                }
            } else {
                adminArea
            }
        } else {
            countryCode?.let { Locale("", it).getDisplayName(locale) }
        }
        return listOfNotNull(
            locality,
            adminArea,
            if (countryCode != "GB") countryName else null
        ).joinToString(", ")
    }
}

/**
 * Custom exception for user locating.
 *
 * @param errorCode The error code enum for the error.
 */
class UserLocatingException(val errorCode: UserLocatingErrorCode) : Exception()

/**
 * Enum representing the different error statuses that can occur when locating the user.
 */
enum class UserLocatingErrorCode {
    NO_LOCATION_PERMISSION,
    NO_USER_LOCATION_FOUND,
}

/**
 * Custom exception for geocoding.
 *
 * @param errorCode The error code enum for the error.
 */
class GeocodingException(val errorCode: GeocodingErrorCode) : Exception()

/**
 * Enum representing the different error statuses that can occur when geocoding.
 */
enum class GeocodingErrorCode {
    NO_RESULTS,
    INVALID_ADDRESS,
    GEOCODING_FAILURE,
}
