package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WirdDao {
    // User Settings
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun observeSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getSettingsOnce(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: UserSettingsEntity)

    // Template Items
    @Query("SELECT * FROM template_items ORDER BY sortOrder ASC, id ASC")
    fun observeTemplateItems(): Flow<List<TemplateItemEntity>>

    @Query("SELECT * FROM template_items ORDER BY sortOrder ASC, id ASC")
    suspend fun getTemplateItemsOnce(): List<TemplateItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateItems(items: List<TemplateItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplateItem(item: TemplateItemEntity): Long

    @Update
    suspend fun updateTemplateItem(item: TemplateItemEntity)

    @Update
    suspend fun updateTemplateItems(items: List<TemplateItemEntity>)

    @Query("DELETE FROM template_items WHERE id = :id AND isCustom = 1")
    suspend fun deleteCustomTemplateItem(id: Long)

    @Query("DELETE FROM template_items WHERE code = :code")
    suspend fun deleteTemplateItemByCode(code: String)

    // Daily Records
    @Query("SELECT * FROM daily_records WHERE dateKey = :dateKey")
    fun observeDailyRecord(dateKey: String): Flow<DailyRecordEntity?>

    @Query("SELECT * FROM daily_records WHERE dateKey = :dateKey")
    suspend fun getDailyRecordOnce(dateKey: String): DailyRecordEntity?

    @Query("SELECT * FROM daily_records ORDER BY dateKey DESC")
    fun observeAllDailyRecords(): Flow<List<DailyRecordEntity>>

    @Query("SELECT * FROM daily_records ORDER BY dateKey DESC")
    suspend fun getAllDailyRecordsOnce(): List<DailyRecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDailyRecordIgnore(record: DailyRecordEntity): Long

    @Update
    suspend fun updateDailyRecord(record: DailyRecordEntity)

    // Daily Record Items
    @Query("SELECT * FROM daily_record_items WHERE dateKey = :dateKey ORDER BY sortOrder ASC, id ASC")
    fun observeDailyRecordItems(dateKey: String): Flow<List<DailyRecordItemEntity>>

    @Query("SELECT * FROM daily_record_items WHERE dateKey = :dateKey ORDER BY sortOrder ASC, id ASC")
    suspend fun getDailyRecordItemsOnce(dateKey: String): List<DailyRecordItemEntity>

    @Query("SELECT * FROM daily_record_items ORDER BY dateKey DESC, sortOrder ASC")
    fun observeAllDailyRecordItems(): Flow<List<DailyRecordItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDailyRecordItemsIgnore(items: List<DailyRecordItemEntity>)

    @Update
    suspend fun updateDailyRecordItem(item: DailyRecordItemEntity)

    @Query("DELETE FROM daily_record_items WHERE dateKey = :dateKey AND templateCode = :templateCode AND isCompleted = 0")
    suspend fun deleteUncompletedDailyItem(dateKey: String, templateCode: String)
}
