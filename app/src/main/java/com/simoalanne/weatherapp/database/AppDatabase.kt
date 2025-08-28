package com.simoalanne.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.simoalanne.weatherapp.model.LocationEntity
import com.simoalanne.weatherapp.model.WeatherEntity

/**
 * Database class for the app. Two tables:
 * - LocationEntity: contains the locations the user has added
 * - WeatherEntity: contains the weather data for the locations the user has added or for the physical location
 */
@Database(entities = [LocationEntity::class, WeatherEntity::class],  version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun weatherDao(): WeatherDao

    companion object {
        // the database is a singleton which is thread safe
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // build the database if it doesn't exist and return it
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
