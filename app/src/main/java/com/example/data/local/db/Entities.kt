package com.example.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayer_logs",
    primaryKeys = ["dateKey", "prayerCode"],
    indices = [Index(value = ["dateKey"])]
)
data class PrayerLogEntity(
    val dateKey: String,
    val prayerCode: String,
    val isCompleted: Boolean = false,
    val performanceMode: String = "UNSPECIFIED",
    val postPrayerAdhkarDone: Boolean = false,
    val personalNote: String = "",
    val updatedAtMillis: Long = 0L
)

@Entity(
    tableName = "activity_definitions",
    indices = [Index(value = ["code"], unique = true)]
)
data class ActivityDefEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val subtitle: String,
    val categoryKey: String,
    val iconKey: String,
    val isCustom: Boolean = false,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0,
    val targetCount: Int = 1
)

@Entity(
    tableName = "daily_activity_logs",
    primaryKeys = ["dateKey", "activityCode"],
    indices = [Index(value = ["dateKey"])]
)
data class ActivityLogEntity(
    val dateKey: String,
    val activityCode: String,
    val isCompleted: Boolean = false,
    val currentCount: Int = 0,
    val note: String = "",
    val completedAtMillis: Long? = null
)

@Entity(tableName = "tasbih_counter_state")
data class TasbihCounterEntity(
    @PrimaryKey val id: Int = 1,
    val activeDhikrId: String = "subhan_allah",
    val activeDhikrTitle: String = "سبحان الله",
    val currentCount: Int = 0,
    val targetCycle: Int = 33,
    val totalTodayCount: Int = 0,
    val lastUpdatedDateKey: String = ""
)
