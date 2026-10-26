package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * WirdAppDatabase — Safe, non-destructive Room Database for «وِرد».
 * Explicitly defines Migrations and NEVER uses fallbackToDestructiveMigration().
 */
@Database(
    entities = [
        PrayerLogEntity::class,
        ActivityDefEntity::class,
        ActivityLogEntity::class,
        TasbihCounterEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WirdAppDatabase : RoomDatabase() {
    abstract fun dao(): WirdNewDao

    companion object {
        @Volatile
        private var INSTANCE: WirdAppDatabase? = null

        /**
         * Non-destructive migration from version 1 to version 2 that creates any missing tables
         * and preserves existing records if upgrading from an earlier schema.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `prayer_logs` (
                        `dateKey` TEXT NOT NULL,
                        `prayerCode` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL DEFAULT 0,
                        `performanceMode` TEXT NOT NULL DEFAULT 'UNSPECIFIED',
                        `postPrayerAdhkarDone` INTEGER NOT NULL DEFAULT 0,
                        `personalNote` TEXT NOT NULL DEFAULT '',
                        `updatedAtMillis` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`dateKey`, `prayerCode`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_prayer_logs_dateKey` ON `prayer_logs` (`dateKey`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `activity_definitions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `code` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `subtitle` TEXT NOT NULL,
                        `categoryKey` TEXT NOT NULL,
                        `iconKey` TEXT NOT NULL,
                        `isCustom` INTEGER NOT NULL DEFAULT 0,
                        `isEnabled` INTEGER NOT NULL DEFAULT 1,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        `targetCount` INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_activity_definitions_code` ON `activity_definitions` (`code`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_activity_logs` (
                        `dateKey` TEXT NOT NULL,
                        `activityCode` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL DEFAULT 0,
                        `currentCount` INTEGER NOT NULL DEFAULT 0,
                        `note` TEXT NOT NULL DEFAULT '',
                        `completedAtMillis` INTEGER,
                        PRIMARY KEY(`dateKey`, `activityCode`)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_activity_logs_dateKey` ON `daily_activity_logs` (`dateKey`)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tasbih_counter_state` (
                        `id` INTEGER NOT NULL,
                        `activeDhikrId` TEXT NOT NULL,
                        `activeDhikrTitle` TEXT NOT NULL,
                        `currentCount` INTEGER NOT NULL,
                        `targetCycle` INTEGER NOT NULL,
                        `totalTodayCount` INTEGER NOT NULL,
                        `lastUpdatedDateKey` TEXT NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): WirdAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WirdAppDatabase::class.java,
                    "wird_clean_v1.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
