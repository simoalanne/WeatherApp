package com.example.weatherapp.location

import android.util.Log
import com.example.weatherapp.model.Coordinates
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await

class UserLocationProvider(val fusedLocationProviderClient: FusedLocationProviderClient) {

    suspend fun getCurrentLocation(): Coordinates? {
        try {
            val location = fusedLocationProviderClient.lastLocation.await()
            if (location != null) {
                return Coordinates(location.latitude, location.longitude)
            }
            return null
        } catch (e: SecurityException) {
            Log.d("LocationProvider", "getUserCoordinates: ${e.message}")
            return null
        }
    }
}
