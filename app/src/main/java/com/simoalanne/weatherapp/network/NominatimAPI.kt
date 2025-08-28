package com.simoalanne.weatherapp.network

import com.simoalanne.weatherapp.BuildConfig
import com.simoalanne.weatherapp.model.NominatimResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for the Nominatim API. Nominatim is needed because the build in Geocoder class sucks
 * at reverse geocoding.
 */
interface NominatimAPI {
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
        // affects the accuracy of the results. Lower values return just country level results. for city
        // level results this works the best
        @Query("zoom") zoom: Int = 14,
        @Query("accept-language") language: String = "en"
    ): NominatimResponse

    // Nominatim API requires a User-Agent header to be sent with every request. Ideally that should
    // contain the app name + contact information. By default the config value is "weather app android"
    // but you can change it in local.properties to include also your email for simoalanne.
    companion object {
        // AI helped to write this one because had no idea how to add headers to retrofit, only
        // knew that I need the user-agent header for the api to work.
        private val client = OkHttpClient.Builder()
            // this makes retrofit add the user agent header for every request.
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", BuildConfig.NOMINATIM_USER_AGENT)
                    .build()
                chain.proceed(request)
            }
            .build()

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: NominatimAPI = retrofit.create(NominatimAPI::class.java)
    }
}
