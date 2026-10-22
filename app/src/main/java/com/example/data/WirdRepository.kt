package com.example.data

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class WirdRepository(
    private val dao: WirdDao,
    private val clock: ClockProvider = SystemClockProvider
) {
    val settingsFlow: Flow<UserSettingsEntity?> = dao.observeSettings()
    val templateItemsFlow: Flow<List<TemplateItemEntity>> = dao.observeTemplateItems()

    fun observeDaySummary(dateKey: String): Flow<DailyDaySummary?> {
        return combine(
            dao.observeDailyRecord(dateKey),
            dao.observeDailyRecordItems(dateKey)
        ) { record, items ->
            if (record == null) null else DailyDaySummary(record, items)
        }
    }

    fun observeAllHistorySummaries(): Flow<List<DailyDaySummary>> {
        return combine(
            dao.observeAllDailyRecords(),
            dao.observeAllDailyRecordItems()
        ) { records, allItems ->
            val itemsByDate = allItems.groupBy { it.dateKey }
            records.map { record ->
                DailyDaySummary(
                    record = record,
                    items = itemsByDate[record.dateKey].orEmpty().sortedBy { it.sortOrder }
                )
            }
        }
    }

    fun currentDateKey(): String = ArabicDateUtils.toDateKey(clock.currentLocalDate())

    /**
     * Ensures settings and default template exist, and creates today's independent daily record
     * from the active template without modifying or backfilling any previous days.
     */
    suspend fun ensureInitializedAndSyncToday(): String {
        val todayKey = currentDateKey()
        var settings = dao.getSettingsOnce()
        if (settings == null) {
            settings = UserSettingsEntity(
                id = 1,
                isOnboardingCompleted = false,
                ageGroup = AgeGroup.YOUNG_ADULTS.name,
                genderGroup = GenderGroup.UNSPECIFIED.name,
                intensityLevel = WirdIntensity.BALANCED.name,
                darkModePref = DarkModePreference.SYSTEM.name,
                counterValue = 0,
                activeDhikrTitle = "ذكر مطلق",
                lastActiveDateKey = todayKey
            )
            dao.upsertSettings(settings)
        } else if (settings.lastActiveDateKey != todayKey) {
            dao.upsertSettings(settings.copy(lastActiveDateKey = todayKey))
        }

        var templates = dao.getTemplateItemsOnce()
        if (templates.isEmpty()) {
            val age = runCatching { AgeGroup.valueOf(settings.ageGroup) }.getOrDefault(AgeGroup.YOUNG_ADULTS)
            val gender = runCatching { GenderGroup.valueOf(settings.genderGroup) }.getOrDefault(GenderGroup.UNSPECIFIED)
            val intensity = runCatching { WirdIntensity.valueOf(settings.intensityLevel) }.getOrDefault(WirdIntensity.BALANCED)
            val defaults = DefaultTemplateCatalog.buildDefaultItems(age, gender, intensity)
            dao.insertTemplateItems(defaults)
            templates = dao.getTemplateItemsOnce()
        }

        ensureDailyRecordForDate(todayKey, settings, templates)
        return todayKey
    }

    private suspend fun ensureDailyRecordForDate(
        dateKey: String,
        settings: UserSettingsEntity,
        templates: List<TemplateItemEntity>
    ) {
        val existingRecord = dao.getDailyRecordOnce(dateKey)
        val ageLabel = runCatching { AgeGroup.valueOf(settings.ageGroup).arabicLabel }.getOrDefault("الشباب")
        val intensityLabel = runCatching { WirdIntensity.valueOf(settings.intensityLevel).arabicLabel }.getOrDefault("ورد متوازن")
        val summaryTitle = "$intensityLabel • $ageLabel"

        if (existingRecord == null) {
            val newRecord = DailyRecordEntity(
                dateKey = dateKey,
                createdAtMillis = clock.currentTimeMillis(),
                timeZoneId = clock.currentZoneId().id,
                templateSummary = summaryTitle
            )
            dao.insertDailyRecordIgnore(newRecord)
            val enabledItems = templates.filter { it.isEnabled }.sortedBy { it.sortOrder }
            val dailyItems = enabledItems.map { tmpl ->
                DailyRecordItemEntity(
                    dateKey = dateKey,
                    templateCode = tmpl.code,
                    title = tmpl.title,
                    subtitle = tmpl.subtitle,
                    iconKey = tmpl.iconKey,
                    sortOrder = tmpl.sortOrder,
                    isCompleted = false,
                    completedAtMillis = null
                )
            }
            dao.insertDailyRecordItemsIgnore(dailyItems)
        }
    }

    /**
     * Synchronizes today's record when the user explicitly customizes the template on the same day,
     * while strictly preserving any already-completed items on today's record and never touching past dates.
     */
    private suspend fun syncTodayWithUpdatedTemplate(todayKey: String) {
        val settings = dao.getSettingsOnce() ?: return
        val templates = dao.getTemplateItemsOnce()
        val existingRecord = dao.getDailyRecordOnce(todayKey)
        if (existingRecord == null) {
            ensureDailyRecordForDate(todayKey, settings, templates)
            return
        }

        val ageLabel = runCatching { AgeGroup.valueOf(settings.ageGroup).arabicLabel }.getOrDefault("الشباب")
        val intensityLabel = runCatching { WirdIntensity.valueOf(settings.intensityLevel).arabicLabel }.getOrDefault("ورد متوازن")
        dao.updateDailyRecord(existingRecord.copy(templateSummary = "$intensityLabel • $ageLabel"))

        val currentTodayItems = dao.getDailyRecordItemsOnce(todayKey).associateBy { it.templateCode }
        val enabledTemplates = templates.filter { it.isEnabled }.sortedBy { it.sortOrder }
        val enabledCodes = enabledTemplates.map { it.code }.toSet()

        // Remove uncompleted items from today that were disabled in the template
        for ((code, item) in currentTodayItems) {
            if (code !in enabledCodes && !item.isCompleted) {
                dao.deleteUncompletedDailyItem(todayKey, code)
            }
        }

        // Insert newly enabled items or update sortOrder/title of existing today items
        for (tmpl in enabledTemplates) {
            val existingItem = currentTodayItems[tmpl.code]
            if (existingItem == null) {
                dao.insertDailyRecordItemsIgnore(
                    listOf(
                        DailyRecordItemEntity(
                            dateKey = todayKey,
                            templateCode = tmpl.code,
                            title = tmpl.title,
                            subtitle = tmpl.subtitle,
                            iconKey = tmpl.iconKey,
                            sortOrder = tmpl.sortOrder,
                            isCompleted = false,
                            completedAtMillis = null
                        )
                    )
                )
            } else {
                dao.updateDailyRecordItem(
                    existingItem.copy(
                        title = tmpl.title,
                        subtitle = tmpl.subtitle,
                        sortOrder = tmpl.sortOrder
                    )
                )
            }
        }
    }

    suspend fun toggleDailyItemCompletion(item: DailyRecordItemEntity) {
        val nowCompleted = !item.isCompleted
        val updatedMask = if (item.isFivePrayers) {
            if (nowCompleted) FivePrayer.entries.joinToString(",") { it.code } else ""
        } else {
            item.subItemsMask
        }
        dao.updateDailyRecordItem(
            item.copy(
                isCompleted = nowCompleted,
                completedAtMillis = if (nowCompleted) clock.currentTimeMillis() else null,
                subItemsMask = updatedMask
            )
        )
    }

    suspend fun toggleSingleFivePrayer(item: DailyRecordItemEntity, prayer: FivePrayer) {
        if (!item.isFivePrayers) return
        val currentSet = item.completedPrayers.toMutableSet()
        if (prayer in currentSet) {
            currentSet.remove(prayer)
        } else {
            currentSet.add(prayer)
        }
        val orderedCodes = FivePrayer.entries.filter { it in currentSet }.joinToString(",") { it.code }
        val allCompleted = currentSet.size == FivePrayer.entries.size
        dao.updateDailyRecordItem(
            item.copy(
                isCompleted = allCompleted,
                completedAtMillis = if (allCompleted) clock.currentTimeMillis() else null,
                subItemsMask = orderedCodes
            )
        )
    }

    suspend fun setAllFivePrayers(item: DailyRecordItemEntity, completed: Boolean) {
        if (!item.isFivePrayers) return
        val mask = if (completed) FivePrayer.entries.joinToString(",") { it.code } else ""
        dao.updateDailyRecordItem(
            item.copy(
                isCompleted = completed,
                completedAtMillis = if (completed) clock.currentTimeMillis() else null,
                subItemsMask = mask
            )
        )
    }

    suspend fun incrementCounter(step: Int = 1) {
        val current = dao.getSettingsOnce() ?: run {
            ensureInitializedAndSyncToday()
            dao.getSettingsOnce()!!
        }
        val nextValue = (current.counterValue + step).coerceAtLeast(0)
        dao.upsertSettings(current.copy(counterValue = nextValue))
    }

    suspend fun resetCounter() {
        val current = dao.getSettingsOnce() ?: return
        dao.upsertSettings(current.copy(counterValue = 0))
    }

    suspend fun selectDhikrForCounter(dhikrTitle: String) {
        val current = dao.getSettingsOnce() ?: return
        dao.upsertSettings(current.copy(activeDhikrTitle = dhikrTitle))
    }

    suspend fun applyProfilePreset(
        ageGroup: AgeGroup,
        genderGroup: GenderGroup,
        intensity: WirdIntensity,
        markOnboardingComplete: Boolean = true
    ) {
        val current = dao.getSettingsOnce() ?: UserSettingsEntity()
        val todayKey = currentDateKey()
        val updatedSettings = current.copy(
            isOnboardingCompleted = if (markOnboardingComplete) true else current.isOnboardingCompleted,
            ageGroup = ageGroup.name,
            genderGroup = genderGroup.name,
            intensityLevel = intensity.name,
            lastActiveDateKey = todayKey
        )
        dao.upsertSettings(updatedSettings)

        val existingTemplates = dao.getTemplateItemsOnce()
        val presetItemsByCode = DefaultTemplateCatalog.buildDefaultItems(ageGroup, genderGroup, intensity)
            .associateBy { it.code }

        if (existingTemplates.isEmpty()) {
            dao.insertTemplateItems(presetItemsByCode.values.toList())
        } else {
            val updatedList = existingTemplates.map { existing ->
                val preset = presetItemsByCode[existing.code]
                if (preset != null && !existing.isCustom) {
                    existing.copy(
                        isEnabled = preset.isEnabled,
                        subtitle = preset.subtitle
                    )
                } else {
                    existing
                }
            }
            dao.updateTemplateItems(updatedList)
        }

        syncTodayWithUpdatedTemplate(todayKey)
    }

    suspend fun completeOnboardingWithCurrentSettings() {
        val current = dao.getSettingsOnce() ?: return
        dao.upsertSettings(current.copy(isOnboardingCompleted = true))
    }

    suspend fun toggleTemplateItemEnabled(item: TemplateItemEntity) {
        dao.updateTemplateItem(item.copy(isEnabled = !item.isEnabled))
        syncTodayWithUpdatedTemplate(currentDateKey())
    }

    suspend fun addCustomTemplateItem(title: String, subtitle: String) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val cleanSubtitle = subtitle.trim().ifEmpty { "عمل شخصي مضاف" }
        val existing = dao.getTemplateItemsOnce()
        val nextSortOrder = (existing.maxOfOrNull { it.sortOrder } ?: 0) + 1
        val newItem = TemplateItemEntity(
            code = "custom_${UUID.randomUUID().toString().take(8)}",
            title = cleanTitle,
            subtitle = cleanSubtitle,
            iconKey = "star",
            isEnabled = true,
            sortOrder = nextSortOrder,
            isCustom = true,
            minIntensity = WirdIntensity.LIGHT.name
        )
        dao.insertTemplateItem(newItem)
        syncTodayWithUpdatedTemplate(currentDateKey())
    }

    suspend fun deleteCustomTemplateItem(item: TemplateItemEntity) {
        if (!item.isCustom) return
        dao.deleteCustomTemplateItem(item.id)
        dao.deleteUncompletedDailyItem(currentDateKey(), item.code)
    }

    suspend fun moveTemplateItem(item: TemplateItemEntity, moveUp: Boolean) {
        val items = dao.getTemplateItemsOnce().sortedBy { it.sortOrder }.toMutableList()
        val index = items.indexOfFirst { it.id == item.id }
        if (index == -1) return
        val targetIndex = if (moveUp) index - 1 else index + 1
        if (targetIndex !in items.indices) return

        val neighbor = items[targetIndex]
        val itemOrder = item.sortOrder
        val neighborOrder = neighbor.sortOrder
        val updatedItem = item.copy(sortOrder = if (itemOrder == neighborOrder) targetIndex + 1 else neighborOrder)
        val updatedNeighbor = neighbor.copy(sortOrder = if (itemOrder == neighborOrder) index + 1 else itemOrder)

        dao.updateTemplateItems(listOf(updatedItem, updatedNeighbor))
        syncTodayWithUpdatedTemplate(currentDateKey())
    }

    suspend fun updateDarkModePreference(pref: DarkModePreference) {
        val current = dao.getSettingsOnce() ?: return
        dao.upsertSettings(current.copy(darkModePref = pref.name))
    }

    suspend fun updateReminderPreference(enabled: Boolean, time: String) {
        val current = dao.getSettingsOnce() ?: return
        dao.upsertSettings(current.copy(reminderEnabled = enabled, reminderTime = time))
    }
}
