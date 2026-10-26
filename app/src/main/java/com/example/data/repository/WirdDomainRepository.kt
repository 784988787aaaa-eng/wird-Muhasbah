package com.example.data.repository

import com.example.core.common.ArabicDateFormatter
import com.example.core.common.ClockProvider
import com.example.core.common.SystemClockProvider
import com.example.data.local.catalog.SeedCatalogs
import com.example.data.local.datastore.PreferencesDataSource
import com.example.data.local.db.ActivityDefEntity
import com.example.data.local.db.ActivityLogEntity
import com.example.data.local.db.PrayerLogEntity
import com.example.data.local.db.TasbihCounterEntity
import com.example.data.local.db.WirdNewDao
import com.example.domain.model.ActivityCategory
import com.example.domain.model.ActivityDefinition
import com.example.domain.model.AuthenticDhikrItem
import com.example.domain.model.DailyActivityRecord
import com.example.domain.model.DailyPrayersSummary
import com.example.domain.model.DayHistorySummary
import com.example.domain.model.HeartMessage
import com.example.domain.model.JourneyWeeklyGlimpse
import com.example.domain.model.PrayerDayLog
import com.example.domain.model.PrayerPerformanceMode
import com.example.domain.model.PrayerStatus
import com.example.domain.model.PrayerType
import com.example.domain.model.TasbihCounterState
import com.example.domain.model.UserPreferences
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class WirdDomainRepository(
    private val dao: WirdNewDao,
    private val preferencesDataSource: PreferencesDataSource,
    private val clock: ClockProvider = SystemClockProvider
) {
    val userPreferencesFlow: Flow<UserPreferences> = preferencesDataSource.preferencesFlow

    val tasbihStateFlow: Flow<TasbihCounterState> = dao.observeTasbihState().map { entity ->
        if (entity == null) {
            TasbihCounterState()
        } else {
            TasbihCounterState(
                activeDhikrId = entity.activeDhikrId,
                activeDhikrTitle = entity.activeDhikrTitle,
                currentCount = entity.currentCount,
                targetCycle = entity.targetCycle,
                totalTodayCount = entity.totalTodayCount
            )
        }
    }

    val activityDefinitionsFlow: Flow<List<ActivityDefinition>> =
        dao.observeActivityDefinitions().map { list ->
            list.map { it.toDomain() }
        }

    fun currentDateKey(): String = ArabicDateFormatter.toDateKey(clock.currentLocalDate())

    /**
     * Initializes default seed activities and tasbih state without auto-recording any prayers or deeds.
     * Detects if the user is returning after a pause (> 2 days) and returns the previous active date key.
     */
    suspend fun ensureInitializedAndGetPreviousDateKey(): Pair<String, String> {
        val todayKey = currentDateKey()
        val prefs = preferencesDataSource.preferencesFlow.first()
        val previousDateKey = prefs.lastActiveDateKey

        val existingDefs = dao.getActivityDefinitionsOnce()
        if (existingDefs.isEmpty()) {
            val seedEntities = SeedCatalogs.defaultActivities.map { def ->
                ActivityDefEntity(
                    code = def.code,
                    title = def.title,
                    subtitle = def.subtitle,
                    categoryKey = def.category.key,
                    iconKey = def.iconKey,
                    isCustom = false,
                    isEnabled = true,
                    sortOrder = def.sortOrder,
                    targetCount = def.targetCount
                )
            }
            dao.insertActivityDefinitionsIgnore(seedEntities)
        }

        val existingTasbih = dao.getTasbihStateOnce()
        if (existingTasbih == null) {
            dao.upsertTasbihState(
                TasbihCounterEntity(
                    id = 1,
                    activeDhikrId = "subhan_allah",
                    activeDhikrTitle = "سبحان الله",
                    currentCount = 0,
                    targetCycle = 33,
                    totalTodayCount = 0,
                    lastUpdatedDateKey = todayKey
                )
            )
        }

        if (previousDateKey.isEmpty()) {
            preferencesDataSource.updateLastActiveDateKey(todayKey)
        }
        return todayKey to previousDateKey
    }

    suspend fun acknowledgeReturnToday() {
        preferencesDataSource.updateLastActiveDateKey(currentDateKey())
    }

    fun observePrayersForDate(dateKey: String): Flow<DailyPrayersSummary> {
        return dao.observePrayerLogsForDate(dateKey).map { entities ->
            val byType = entities.associate { entity ->
                val type = PrayerType.fromCode(entity.prayerCode)
                type to entity.toDomain()
            }
            DailyPrayersSummary(dateKey = dateKey, logsByPrayer = byType)
        }
    }

    fun observeActivitiesForDate(dateKey: String): Flow<List<DailyActivityRecord>> {
        return combine(
            dao.observeActivityDefinitions(),
            dao.observeActivityLogsForDate(dateKey)
        ) { defs, logs ->
            val logsByCode = logs.associateBy { it.activityCode }
            defs.filter { it.isEnabled }
                .sortedBy { it.sortOrder }
                .map { defEntity ->
                    val def = defEntity.toDomain()
                    val log = logsByCode[def.code]
                    DailyActivityRecord(
                        dateKey = dateKey,
                        definition = def,
                        isCompleted = log?.isCompleted ?: false,
                        currentCount = log?.currentCount ?: 0,
                        note = log?.note ?: "",
                        completedAtMillis = log?.completedAtMillis
                    )
                }
        }
    }

    fun observeDayHistorySummary(dateKey: String): Flow<DayHistorySummary> {
        return combine(
            observePrayersForDate(dateKey),
            observeActivitiesForDate(dateKey)
        ) { prayersSummary, activityRecords ->
            val adhkarRecords = activityRecords.filter { it.definition.category == ActivityCategory.ADHKAR }
            val goodDeedRecords = activityRecords.filter { it.definition.category != ActivityCategory.ADHKAR }

            DayHistorySummary(
                dateKey = dateKey,
                prayersCompleted = prayersSummary.completedCount,
                prayersTotal = prayersSummary.totalCount,
                goodDeedsCompleted = goodDeedRecords.count { it.isCompleted },
                goodDeedsTotal = goodDeedRecords.size.coerceAtLeast(1),
                adhkarCompleted = adhkarRecords.count { it.isCompleted },
                adhkarTotal = adhkarRecords.size.coerceAtLeast(1),
                prayerLogs = prayersSummary.orderedLogs,
                activityRecords = activityRecords
            )
        }
    }

    fun observeAllHistorySummaries(): Flow<Map<String, DayHistorySummary>> {
        return combine(
            dao.observeAllPrayerLogs(),
            dao.observeAllActivityLogs(),
            dao.observeActivityDefinitions()
        ) { allPrayers, allActivities, allDefs ->
            val enabledDefs = allDefs.filter { it.isEnabled }.sortedBy { it.sortOrder }.map { it.toDomain() }
            val allDateKeys = (allPrayers.map { it.dateKey } + allActivities.map { it.dateKey } + currentDateKey()).toSet()
            val prayersByDate = allPrayers.groupBy { it.dateKey }
            val activitiesByDate = allActivities.groupBy { it.dateKey }

            allDateKeys.associateWith { dateKey ->
                val pEntities = prayersByDate[dateKey].orEmpty()
                val pMap = pEntities.associate { PrayerType.fromCode(it.prayerCode) to it.toDomain() }
                val prayersSummary = DailyPrayersSummary(dateKey, pMap)

                val aLogsMap = activitiesByDate[dateKey].orEmpty().associateBy { it.activityCode }
                val records = enabledDefs.map { def ->
                    val log = aLogsMap[def.code]
                    DailyActivityRecord(
                        dateKey = dateKey,
                        definition = def,
                        isCompleted = log?.isCompleted ?: false,
                        currentCount = log?.currentCount ?: 0,
                        note = log?.note ?: "",
                        completedAtMillis = log?.completedAtMillis
                    )
                }

                val adhkarRecords = records.filter { it.definition.category == ActivityCategory.ADHKAR }
                val deedRecords = records.filter { it.definition.category != ActivityCategory.ADHKAR }

                DayHistorySummary(
                    dateKey = dateKey,
                    prayersCompleted = prayersSummary.completedCount,
                    prayersTotal = 5,
                    goodDeedsCompleted = deedRecords.count { it.isCompleted },
                    goodDeedsTotal = deedRecords.size.coerceAtLeast(1),
                    adhkarCompleted = adhkarRecords.count { it.isCompleted },
                    adhkarTotal = adhkarRecords.size.coerceAtLeast(1),
                    prayerLogs = prayersSummary.orderedLogs,
                    activityRecords = records
                )
            }
        }
    }

    fun observeWeeklyJourneyGlimpse(): Flow<JourneyWeeklyGlimpse> {
        return observeAllHistorySummaries().map { summariesByDate ->
            val today = clock.currentLocalDate()
            val weekDates = ArabicDateFormatter.recentDateWindow(today, 7)
                .map { ArabicDateFormatter.toDateKey(it) }
            val weekSummaries = weekDates.mapNotNull { summariesByDate[it] }
            val activeDays = weekSummaries.count { it.hasAnyRecordedAction }
            val totalPrayers = weekSummaries.sumOf { it.prayersCompleted }
            val totalDeeds = weekSummaries.sumOf { it.goodDeedsCompleted + it.adhkarCompleted }

            val text = when {
                activeDays == 0 -> "رحلتك هذا الأسبوع تبدأ بخطوة واحدة هادئة متى شئت."
                else -> "سجّلت هذا الأسبوع $totalPrayers صلاة و$totalDeeds من الأذكار وأعمال الخير خلال $activeDays أيام."
            }

            JourneyWeeklyGlimpse(
                activeDaysInWeek = activeDays,
                totalPrayersRecordedInWeek = totalPrayers,
                totalDeedsRecordedInWeek = totalDeeds,
                descriptiveSummaryText = text
            )
        }
    }

    // Prayer Actions
    suspend fun recordOrUpdatePrayer(
        dateKey: String,
        prayerType: PrayerType,
        isCompleted: Boolean,
        performanceMode: PrayerPerformanceMode = PrayerPerformanceMode.UNSPECIFIED,
        postPrayerAdhkarDone: Boolean = false,
        personalNote: String = ""
    ) {
        dao.upsertPrayerLog(
            PrayerLogEntity(
                dateKey = dateKey,
                prayerCode = prayerType.code,
                isCompleted = isCompleted,
                performanceMode = performanceMode.name,
                postPrayerAdhkarDone = postPrayerAdhkarDone,
                personalNote = personalNote.trim(),
                updatedAtMillis = clock.currentTimeMillis()
            )
        )
        preferencesDataSource.updateLastActiveDateKey(currentDateKey())
    }

    suspend fun togglePrayerQuick(dateKey: String, prayerType: PrayerType) {
        val currentLogs = dao.getPrayerLogsForDateOnce(dateKey)
        val existing = currentLogs.firstOrNull { it.prayerCode == prayerType.code }
        val nextCompleted = !(existing?.isCompleted ?: false)
        dao.upsertPrayerLog(
            PrayerLogEntity(
                dateKey = dateKey,
                prayerCode = prayerType.code,
                isCompleted = nextCompleted,
                performanceMode = existing?.performanceMode ?: PrayerPerformanceMode.UNSPECIFIED.name,
                postPrayerAdhkarDone = existing?.postPrayerAdhkarDone ?: false,
                personalNote = existing?.personalNote ?: "",
                updatedAtMillis = clock.currentTimeMillis()
            )
        )
        preferencesDataSource.updateLastActiveDateKey(currentDateKey())
    }

    // Activity & Good Deeds Actions
    suspend fun toggleActivityCompletion(dateKey: String, activityCode: String) {
        val currentLogs = dao.getActivityLogsForDateOnce(dateKey)
        val existing = currentLogs.firstOrNull { it.activityCode == activityCode }
        val nextCompleted = !(existing?.isCompleted ?: false)
        dao.upsertActivityLog(
            ActivityLogEntity(
                dateKey = dateKey,
                activityCode = activityCode,
                isCompleted = nextCompleted,
                currentCount = if (nextCompleted) (existing?.currentCount?.coerceAtLeast(1) ?: 1) else 0,
                note = existing?.note ?: "",
                completedAtMillis = if (nextCompleted) clock.currentTimeMillis() else null
            )
        )
        preferencesDataSource.updateLastActiveDateKey(currentDateKey())
    }

    suspend fun incrementActivityCount(dateKey: String, activityCode: String, targetCount: Int = 1) {
        val currentLogs = dao.getActivityLogsForDateOnce(dateKey)
        val existing = currentLogs.firstOrNull { it.activityCode == activityCode }
        val nextCount = (existing?.currentCount ?: 0) + 1
        val completed = nextCount >= targetCount
        dao.upsertActivityLog(
            ActivityLogEntity(
                dateKey = dateKey,
                activityCode = activityCode,
                isCompleted = completed,
                currentCount = nextCount,
                note = existing?.note ?: "",
                completedAtMillis = if (completed) clock.currentTimeMillis() else existing?.completedAtMillis
            )
        )
        preferencesDataSource.updateLastActiveDateKey(currentDateKey())
    }

    suspend fun addCustomActivity(
        title: String,
        subtitle: String,
        category: ActivityCategory
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val cleanSubtitle = subtitle.trim().ifEmpty { "عمل شخصي مضاف" }
        val existing = dao.getActivityDefinitionsOnce()
        val nextOrder = (existing.maxOfOrNull { it.sortOrder } ?: 0) + 1
        val code = "custom_${UUID.randomUUID().toString().take(8)}"
        dao.upsertActivityDefinition(
            ActivityDefEntity(
                code = code,
                title = cleanTitle,
                subtitle = cleanSubtitle,
                categoryKey = category.key,
                iconKey = "custom_star",
                isCustom = true,
                isEnabled = true,
                sortOrder = nextOrder,
                targetCount = 1
            )
        )
    }

    suspend fun updateCustomActivity(
        definition: ActivityDefinition,
        newTitle: String,
        newSubtitle: String,
        newCategory: ActivityCategory
    ) {
        val cleanTitle = newTitle.trim()
        if (cleanTitle.isEmpty()) return
        dao.upsertActivityDefinition(
            ActivityDefEntity(
                id = definition.id,
                code = definition.code,
                title = cleanTitle,
                subtitle = newSubtitle.trim().ifEmpty { definition.subtitle },
                categoryKey = newCategory.key,
                iconKey = definition.iconKey,
                isCustom = definition.isCustom,
                isEnabled = definition.isEnabled,
                sortOrder = definition.sortOrder,
                targetCount = definition.targetCount
            )
        )
    }

    suspend fun toggleActivityDefinitionEnabled(definition: ActivityDefinition) {
        dao.upsertActivityDefinition(
            ActivityDefEntity(
                id = definition.id,
                code = definition.code,
                title = definition.title,
                subtitle = definition.subtitle,
                categoryKey = definition.category.key,
                iconKey = definition.iconKey,
                isCustom = definition.isCustom,
                isEnabled = !definition.isEnabled,
                sortOrder = definition.sortOrder,
                targetCount = definition.targetCount
            )
        )
    }

    suspend fun deleteCustomActivity(definition: ActivityDefinition) {
        if (!definition.isCustom) return
        dao.deleteCustomActivityByCode(definition.code)
        dao.deleteUncompletedActivityLog(currentDateKey(), definition.code)
    }

    suspend fun reorderActivityDefinitions(fromIndex: Int, toIndex: Int) {
        val defs = dao.getActivityDefinitionsOnce().sortedBy { it.sortOrder }.toMutableList()
        if (fromIndex !in defs.indices || toIndex !in defs.indices || fromIndex == toIndex) return
        val item = defs.removeAt(fromIndex)
        defs.add(toIndex, item)
        val reindexed = defs.mapIndexed { idx, entity -> entity.copy(sortOrder = idx + 1) }
        dao.updateActivityDefinitions(reindexed)
    }

    // Tasbih Counter Actions
    suspend fun incrementTasbih(step: Int = 1) {
        val todayKey = currentDateKey()
        val current = dao.getTasbihStateOnce() ?: TasbihCounterEntity(lastUpdatedDateKey = todayKey)
        val nextCount = (current.currentCount + step).coerceAtLeast(0)
        val nextTodayTotal = if (current.lastUpdatedDateKey == todayKey) {
            current.totalTodayCount + step
        } else {
            step
        }
        dao.upsertTasbihState(
            current.copy(
                currentCount = nextCount,
                totalTodayCount = nextTodayTotal.coerceAtLeast(0),
                lastUpdatedDateKey = todayKey
            )
        )
    }

    suspend fun resetTasbihCounter() {
        val current = dao.getTasbihStateOnce() ?: return
        dao.upsertTasbihState(current.copy(currentCount = 0))
    }

    suspend fun selectDhikrForTasbih(dhikr: AuthenticDhikrItem) {
        val current = dao.getTasbihStateOnce() ?: TasbihCounterEntity(lastUpdatedDateKey = currentDateKey())
        dao.upsertTasbihState(
            current.copy(
                activeDhikrId = dhikr.id,
                activeDhikrTitle = dhikr.categoryTitle,
                targetCycle = dhikr.recommendedCount.coerceAtLeast(1)
            )
        )
    }

    suspend fun cycleTasbihDhikr() {
        val all = SeedCatalogs.authenticAdhkar
        val current = dao.getTasbihStateOnce() ?: TasbihCounterEntity()
        val currentIdx = all.indexOfFirst { it.id == current.activeDhikrId }
        val next = all[(currentIdx + 1).mod(all.size)]
        selectDhikrForTasbih(next)
    }

    fun getAuthenticAdhkar(): List<AuthenticDhikrItem> = SeedCatalogs.authenticAdhkar

    fun getHeartMessages(): List<HeartMessage> = SeedCatalogs.heartMessages

    suspend fun clearAllLocalLogs() {
        dao.clearAllPrayerLogs()
        dao.clearAllActivityLogs()
        resetTasbihCounter()
    }
}

private fun PrayerLogEntity.toDomain(): PrayerDayLog {
    val type = PrayerType.fromCode(prayerCode)
    val mode = runCatching { PrayerPerformanceMode.valueOf(performanceMode) }
        .getOrDefault(PrayerPerformanceMode.UNSPECIFIED)
    return PrayerDayLog(
        dateKey = dateKey,
        prayerType = type,
        status = if (isCompleted) PrayerStatus.COMPLETED else PrayerStatus.NOT_RECORDED,
        performanceMode = mode,
        postPrayerAdhkarCompleted = postPrayerAdhkarDone,
        personalNote = personalNote,
        updatedAtMillis = updatedAtMillis
    )
}

private fun ActivityDefEntity.toDomain(): ActivityDefinition {
    return ActivityDefinition(
        id = id,
        code = code,
        title = title,
        subtitle = subtitle,
        category = ActivityCategory.fromKey(categoryKey),
        iconKey = iconKey,
        isCustom = isCustom,
        isEnabled = isEnabled,
        sortOrder = sortOrder,
        targetCount = targetCount
    )
}
