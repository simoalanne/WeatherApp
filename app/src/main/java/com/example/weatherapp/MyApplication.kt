package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.database.AppDatabase

class MyApplication: Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val locationDao by lazy { database.locationDao() }
}
