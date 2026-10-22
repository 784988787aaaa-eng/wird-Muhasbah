package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserSettingsEntity::class,
        TemplateItemEntity::class,
        DailyRecordEntity::class,
        DailyRecordItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WirdDatabase : RoomDatabase() {
    abstract fun wirdDao(): WirdDao

    companion object {
        @Volatile
        private var INSTANCE: WirdDatabase? = null

        fun getInstance(context: Context): WirdDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WirdDatabase::class.java,
                    "wird_muhasaba.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
