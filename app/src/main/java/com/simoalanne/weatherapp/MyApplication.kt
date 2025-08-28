package com.simoalanne.weatherapp

import android.app.Application
import com.simoalanne.weatherapp.database.AppDatabase

class MyApplication: Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val locationDao by lazy { database.locationDao() }
    val weatherDao by lazy { database.weatherDao() }
}
