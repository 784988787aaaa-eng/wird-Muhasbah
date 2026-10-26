package com.example.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.common.ArabicDateFormatter
import com.example.core.common.ClockProvider
import com.example.core.common.SystemClockProvider
import com.example.data.local.catalog.SeedCatalogs
import com.example.data.local.datastore.PreferencesDataSource
import com.example.data.repository.WirdDomainRepository
import com.example.domain.model.ActivityCategory
import com.example.domain.model.ActivityDefinition
import com.example.domain.model.AuthenticDhikrItem
import com.example.domain.model.DailyActivityRecord
import com.example.domain.model.DailyPrayersSummary
import com.example.domain.model.DayHistorySummary
import com.example.domain.model.HeartMessage
import com.example.domain.model.JourneyWeeklyGlimpse
import com.example.domain.model.MessageToneStyle
import com.example.domain.model.PrayerPerformanceMode
import com.example.domain.model.PrayerType
import com.example.domain.model.ReminderCategory
import com.example.domain.model.TasbihCounterState
import com.example.domain.model.ThemeModePreference
import com.example.domain.model.UserPreferences
import java.time.LocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WirdAppUiState(
    val isReady: Boolean = false,
    val todayDate: LocalDate = LocalDate.now(),
    val todayDateKey: String = "",
    val selectedHistoryDateKey: String = "",
    val gregorianDateText: String = "",
    val hijriDateText: String? = null,
    val greetingText: String = "",
    val preferences: UserPreferences = UserPreferences(),
    val todayPrayersSummary: DailyPrayersSummary = DailyPrayersSummary("", emptyMap()),
    val todayActivities: List<DailyActivityRecord> = emptyList(),
    val tasbihState: TasbihCounterState = TasbihCounterState(),
    val weeklyGlimpse: JourneyWeeklyGlimpse = JourneyWeeklyGlimpse(
        activeDaysInWeek = 0,
        totalPrayersRecordedInWeek = 0,
        totalDeedsRecordedInWeek = 0,
        descriptiveSummaryText = ""
    ),
    val allHistoryByDate: Map<String, DayHistorySummary> = emptyMap(),
    val activeHeartMessage: HeartMessage = SeedCatalogs.heartMessages.first(),
    val allHeartMessages: List<HeartMessage> = SeedCatalogs.heartMessages,
    val authenticAdhkar: List<AuthenticDhikrItem> = SeedCatalogs.authenticAdhkar,
    val showReturnAfterPauseBanner: Boolean = false,
    val calmFeedbackBanner: String? = null
)

private data class TransientSessionState(
    val selectedHistoryDateKey: String = "",
    val shownMessageIdsInSession: List<String> = emptyList(),
    val currentMessageId: String = SeedCatalogs.heartMessages.first().id,
    val showReturnBanner: Boolean = false,
    val calmFeedbackBanner: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class WirdMainViewModel(
    private val repository: WirdDomainRepository,
    private val preferencesDataSource: PreferencesDataSource,
    private val clock: ClockProvider = SystemClockProvider,
    private val customScope: CoroutineScope? = null,
    initialPreferences: UserPreferences = UserPreferences()
) : ViewModel() {

    private val activeScope: CoroutineScope
        get() = customScope ?: viewModelScope

    private val todayDateKeyFlow = MutableStateFlow(repository.currentDateKey())
    private val sessionStateFlow = MutableStateFlow(
        TransientSessionState(selectedHistoryDateKey = repository.currentDateKey())
    )

    private val todayPrayersFlow = todayDateKeyFlow.flatMapLatest { dateKey ->
        repository.observePrayersForDate(dateKey)
    }

    private val todayActivitiesFlow = todayDateKeyFlow.flatMapLatest { dateKey ->
        repository.observeActivitiesForDate(dateKey)
    }

    val uiState: StateFlow<WirdAppUiState> = combine(
        todayDateKeyFlow,
        repository.userPreferencesFlow,
        todayPrayersFlow,
        todayActivitiesFlow,
        combine(
            repository.tasbihStateFlow,
            repository.observeWeeklyJourneyGlimpse(),
            repository.observeAllHistorySummaries(),
            sessionStateFlow
        ) { tasbih, glimpse, historyMap, session ->
            Quadruple(tasbih, glimpse, historyMap, session)
        }
    ) { todayKey, prefs, prayers, activities, (tasbih, glimpse, historyMap, session) ->
        val localDate = ArabicDateFormatter.parseDateKey(todayKey, clock.currentLocalDate())
        val useEastern = prefs.useEasternArabicDigits
        val toneMessages = SeedCatalogs.heartMessages.filter { it.toneStyle == prefs.messageTone }
            .ifEmpty { SeedCatalogs.heartMessages }
        val activeMsg = SeedCatalogs.heartMessages.firstOrNull { it.id == session.currentMessageId }
            ?: toneMessages.first()

        WirdAppUiState(
            isReady = true,
            todayDate = localDate,
            todayDateKey = todayKey,
            selectedHistoryDateKey = session.selectedHistoryDateKey.ifEmpty { todayKey },
            gregorianDateText = ArabicDateFormatter.formatGregorianArabic(localDate, useEastern),
            hijriDateText = ArabicDateFormatter.formatHijriArabicWithSource(localDate, useEastern),
            greetingText = ArabicDateFormatter.greetingForHour(clock.currentHourOfDay()),
            preferences = prefs,
            todayPrayersSummary = prayers,
            todayActivities = activities,
            tasbihState = tasbih,
            weeklyGlimpse = glimpse,
            allHistoryByDate = historyMap,
            activeHeartMessage = activeMsg,
            allHeartMessages = SeedCatalogs.heartMessages,
            authenticAdhkar = SeedCatalogs.authenticAdhkar,
            showReturnAfterPauseBanner = session.showReturnBanner,
            calmFeedbackBanner = session.calmFeedbackBanner
        )
    }.stateIn(
        scope = activeScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WirdAppUiState(
            todayDate = clock.currentLocalDate(),
            todayDateKey = repository.currentDateKey(),
            selectedHistoryDateKey = repository.currentDateKey(),
            gregorianDateText = ArabicDateFormatter.formatGregorianArabic(
                clock.currentLocalDate(),
                initialPreferences.useEasternArabicDigits
            ),
            hijriDateText = ArabicDateFormatter.formatHijriArabicWithSource(
                clock.currentLocalDate(),
                initialPreferences.useEasternArabicDigits
            ),
            greetingText = ArabicDateFormatter.greetingForHour(clock.currentHourOfDay()),
            preferences = initialPreferences
        )
    )

    init {
        refreshTodaySession()
    }

    fun refreshTodaySession() {
        activeScope.launch {
            val (todayKey, previousKey) = repository.ensureInitializedAndGetPreviousDateKey()
            todayDateKeyFlow.value = todayKey
            val gapDays = ArabicDateFormatter.daysBetween(previousKey, todayKey)
            val isReturningAfterPause = previousKey.isNotEmpty() && gapDays >= 2L
            sessionStateFlow.update { current ->
                current.copy(
                    selectedHistoryDateKey = if (current.selectedHistoryDateKey.isEmpty()) todayKey else current.selectedHistoryDateKey,
                    showReturnBanner = isReturningAfterPause
                )
            }
        }
    }

    fun completeOnboarding(displayName: String) {
        activeScope.launch {
            if (displayName.isNotBlank()) {
                preferencesDataSource.updateDisplayName(displayName)
            }
            preferencesDataSource.setOnboardingCompleted(true)
            preferencesDataSource.updateLastActiveDateKey(repository.currentDateKey())
        }
    }

    fun acknowledgeReturnBanner() {
        activeScope.launch {
            repository.acknowledgeReturnToday()
            sessionStateFlow.update { it.copy(showReturnBanner = false) }
        }
    }

    fun cycleNextHeartMessage() {
        val allPool = SeedCatalogs.heartMessages
        val currentId = sessionStateFlow.value.currentMessageId
        val shownIds = sessionStateFlow.value.shownMessageIdsInSession + currentId
        val unseen = allPool.filter { it.id !in shownIds }
        val nextMessage = if (unseen.isNotEmpty()) {
            unseen.first()
        } else {
            // All messages shown in this session; reset session history and pick a different one than currentId
            allPool.firstOrNull { it.id != currentId } ?: allPool.first()
        }
        val updatedShown = if (unseen.isNotEmpty()) shownIds else listOf(currentId)
        sessionStateFlow.update {
            it.copy(
                currentMessageId = nextMessage.id,
                shownMessageIdsInSession = updatedShown
            )
        }
    }

    fun currentHeartMessageId(): String = sessionStateFlow.value.currentMessageId

    fun savePrayerDetails(
        dateKey: String,
        prayerType: PrayerType,
        isCompleted: Boolean,
        mode: PrayerPerformanceMode,
        postPrayerAdhkarDone: Boolean,
        note: String
    ) {
        activeScope.launch {
            repository.recordOrUpdatePrayer(
                dateKey = dateKey,
                prayerType = prayerType,
                isCompleted = isCompleted,
                performanceMode = mode,
                postPrayerAdhkarDone = postPrayerAdhkarDone,
                personalNote = note
            )
            val feedback = if (isCompleted) {
                "تقبّل الله طاعتك • تم حفظ صلاة ${prayerType.arabicName}"
            } else {
                "تم تحديث سجل صلاة ${prayerType.arabicName}"
            }
            sessionStateFlow.update { it.copy(calmFeedbackBanner = feedback) }
        }
    }

    fun togglePrayerQuick(dateKey: String, prayerType: PrayerType) {
        activeScope.launch {
            repository.togglePrayerQuick(dateKey, prayerType)
        }
    }

    fun toggleActivity(dateKey: String, activityCode: String) {
        activeScope.launch {
            repository.toggleActivityCompletion(dateKey, activityCode)
        }
    }

    fun incrementTasbih() {
        activeScope.launch {
            repository.incrementTasbih(1)
        }
    }

    fun resetTasbih() {
        activeScope.launch {
            repository.resetTasbihCounter()
        }
    }

    fun cycleTasbihDhikr() {
        activeScope.launch {
            repository.cycleTasbihDhikr()
        }
    }

    fun selectDhikrForTasbih(dhikr: AuthenticDhikrItem) {
        activeScope.launch {
            repository.selectDhikrForTasbih(dhikr)
        }
    }

    fun addCustomActivity(title: String, subtitle: String, category: ActivityCategory) {
        activeScope.launch {
            repository.addCustomActivity(title, subtitle, category)
        }
    }

    fun updateCustomActivity(
        definition: ActivityDefinition,
        title: String,
        subtitle: String,
        category: ActivityCategory
    ) {
        activeScope.launch {
            repository.updateCustomActivity(definition, title, subtitle, category)
        }
    }

    fun deleteCustomActivity(definition: ActivityDefinition) {
        activeScope.launch {
            repository.deleteCustomActivity(definition)
        }
    }

    fun reorderActivity(fromIndex: Int, toIndex: Int) {
        activeScope.launch {
            repository.reorderActivityDefinitions(fromIndex, toIndex)
        }
    }

    fun selectHistoryDateKey(dateKey: String) {
        sessionStateFlow.update { it.copy(selectedHistoryDateKey = dateKey) }
    }

    fun updateDisplayName(name: String) {
        activeScope.launch {
            preferencesDataSource.updateDisplayName(name)
        }
    }

    fun updateThemeMode(mode: ThemeModePreference) {
        activeScope.launch {
            preferencesDataSource.updateThemeMode(mode)
        }
    }

    fun updateMessageTone(tone: MessageToneStyle) {
        activeScope.launch {
            preferencesDataSource.updateMessageTone(tone)
            val matching = SeedCatalogs.heartMessages.firstOrNull { it.toneStyle == tone }
            if (matching != null) {
                sessionStateFlow.update { it.copy(currentMessageId = matching.id) }
            }
        }
    }

    fun updateCardVisibility(
        showHeart: Boolean?,
        showGoodness: Boolean?,
        showJourney: Boolean?
    ) {
        activeScope.launch {
            preferencesDataSource.updateHomeCardVisibility(showHeart, showGoodness, showJourney)
        }
    }

    fun updateHijriAndDigits(showHijri: Boolean?, easternDigits: Boolean?) {
        activeScope.launch {
            preferencesDataSource.updateHijriAndDigits(showHijri, easternDigits)
        }
    }

    fun updateQuietMode(quietAllOff: Boolean) {
        activeScope.launch {
            preferencesDataSource.updateQuietMode(quietAllOff)
        }
    }

    fun updateReminder(category: ReminderCategory, isEnabled: Boolean, timeHHmm: String) {
        activeScope.launch {
            preferencesDataSource.updateReminder(category, isEnabled, timeHHmm)
        }
    }

    fun clearAllLocalData() {
        activeScope.launch {
            repository.clearAllLocalLogs()
            sessionStateFlow.update {
                it.copy(calmFeedbackBanner = "تم مسح السجلات اليومية المحفوظة محليًا")
            }
        }
    }

    class Factory(
        private val repository: WirdDomainRepository,
        private val preferencesDataSource: PreferencesDataSource,
        private val clock: ClockProvider = SystemClockProvider
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WirdMainViewModel(repository, preferencesDataSource, clock) as T
        }
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
