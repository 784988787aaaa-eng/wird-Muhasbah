package com.example.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WirdNewDao {
    // 1. Prayer Logs
    @Query("SELECT * FROM prayer_logs WHERE dateKey = :dateKey")
    fun observePrayerLogsForDate(dateKey: String): Flow<List<PrayerLogEntity>>

    @Query("SELECT * FROM prayer_logs WHERE dateKey = :dateKey")
    suspend fun getPrayerLogsForDateOnce(dateKey: String): List<PrayerLogEntity>

    @Query("SELECT * FROM prayer_logs WHERE dateKey BETWEEN :startDateKey AND :endDateKey ORDER BY dateKey DESC")
    fun observePrayerLogsBetween(startDateKey: String, endDateKey: String): Flow<List<PrayerLogEntity>>

    @Query("SELECT * FROM prayer_logs ORDER BY dateKey DESC")
    fun observeAllPrayerLogs(): Flow<List<PrayerLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrayerLog(log: PrayerLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPrayerLogs(logs: List<PrayerLogEntity>)

    // 2. Activity Definitions
    @Query("SELECT * FROM activity_definitions ORDER BY sortOrder ASC, id ASC")
    fun observeActivityDefinitions(): Flow<List<ActivityDefEntity>>

    @Query("SELECT * FROM activity_definitions ORDER BY sortOrder ASC, id ASC")
    suspend fun getActivityDefinitionsOnce(): List<ActivityDefEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertActivityDefinitionsIgnore(items: List<ActivityDefEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertActivityDefinition(item: ActivityDefEntity): Long

    @Update
    suspend fun updateActivityDefinitions(items: List<ActivityDefEntity>)

    @Query("DELETE FROM activity_definitions WHERE code = :code AND isCustom = 1")
    suspend fun deleteCustomActivityByCode(code: String)

    // 3. Daily Activity Logs
    @Query("SELECT * FROM daily_activity_logs WHERE dateKey = :dateKey")
    fun observeActivityLogsForDate(dateKey: String): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM daily_activity_logs WHERE dateKey = :dateKey")
    suspend fun getActivityLogsForDateOnce(dateKey: String): List<ActivityLogEntity>

    @Query("SELECT * FROM daily_activity_logs ORDER BY dateKey DESC")
    fun observeAllActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertActivityLog(log: ActivityLogEntity)

    @Query("DELETE FROM daily_activity_logs WHERE dateKey = :dateKey AND activityCode = :code AND isCompleted = 0")
    suspend fun deleteUncompletedActivityLog(dateKey: String, code: String)

    // 4. Tasbih Counter
    @Query("SELECT * FROM tasbih_counter_state WHERE id = 1")
    fun observeTasbihState(): Flow<TasbihCounterEntity?>

    @Query("SELECT * FROM tasbih_counter_state WHERE id = 1")
    suspend fun getTasbihStateOnce(): TasbihCounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasbihState(state: TasbihCounterEntity)

    // 5. Clear all user logs (Explicit user-requested privacy wipe only)
    @Query("DELETE FROM prayer_logs")
    suspend fun clearAllPrayerLogs()

    @Query("DELETE FROM daily_activity_logs")
    suspend fun clearAllActivityLogs()
}
