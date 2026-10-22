package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AgeGroup
import com.example.data.ArabicDateUtils
import com.example.data.AuthenticAdhkarCatalog
import com.example.data.ClockProvider
import com.example.data.DailyDaySummary
import com.example.data.DailyRecordItemEntity
import com.example.data.DarkModePreference
import com.example.data.FivePrayer
import com.example.data.GenderGroup
import com.example.data.MotivationKind
import com.example.data.MotivationalLibrary
import com.example.data.MotivationalMessage
import com.example.data.SuggestedDhikr
import com.example.data.SystemClockProvider
import com.example.data.TemplateItemEntity
import com.example.data.UserSettingsEntity
import com.example.data.WirdIntensity
import com.example.data.WirdRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OverlayUiState(
    val isCounterOpen: Boolean = false,
    val isResetConfirmVisible: Boolean = false,
    val isCustomizationOpen: Boolean = false,
    val isHistoryOpen: Boolean = false,
    val isFivePrayersExpanded: Boolean = false,
    val selectedDhikr: SuggestedDhikr? = null,
    val adhkarSetIndex: Int = 0,
    val motivationIndex: Int = 0,
    val celebrationBurstId: Int = 0,
    val celebrationText: String? = null,
    val recentStatusBanner: String? = null
)

data class WirdUiState(
    val isLoading: Boolean = true,
    val todayDateKey: String = "",
    val gregorianDateText: String = "",
    val hijriDateText: String? = null,
    val greetingText: String = "",
    val settings: UserSettingsEntity = UserSettingsEntity(),
    val todaySummary: DailyDaySummary? = null,
    val templateItems: List<TemplateItemEntity> = emptyList(),
    val historySummaries: List<DailyDaySummary> = emptyList(),
    val visibleAdhkarSuggestions: List<SuggestedDhikr> = AuthenticAdhkarCatalog.allSuggestions.take(3),
    val currentMotivation: MotivationalMessage = MotivationalMessage(
        id = "default",
        kind = MotivationKind.WELCOME_OPTIMISM,
        title = "إشراقة وتفاؤل",
        text = "القليل الدائم خير وأحب إلى الله.. لن تتعب ولن يأخذ من وقتك شيئاً!"
    ),
    val overlay: OverlayUiState = OverlayUiState()
) {
    val ageGroupEnum: AgeGroup
        get() = runCatching { AgeGroup.valueOf(settings.ageGroup) }.getOrDefault(AgeGroup.YOUNG_ADULTS)
    val genderGroupEnum: GenderGroup
        get() = runCatching { GenderGroup.valueOf(settings.genderGroup) }.getOrDefault(GenderGroup.UNSPECIFIED)
    val intensityEnum: WirdIntensity
        get() = runCatching { WirdIntensity.valueOf(settings.intensityLevel) }.getOrDefault(WirdIntensity.BALANCED)
    val darkModeEnum: DarkModePreference
        get() = runCatching { DarkModePreference.valueOf(settings.darkModePref) }.getOrDefault(DarkModePreference.SYSTEM)
}

@OptIn(ExperimentalCoroutinesApi::class)
class WirdViewModel(
    private val repository: WirdRepository,
    private val clock: ClockProvider = SystemClockProvider
) : ViewModel() {

    private val activeDateKeyFlow = MutableStateFlow(repository.currentDateKey())
    private val overlayFlow = MutableStateFlow(OverlayUiState())
    private val lastTapTimeByItemId = mutableMapOf<Long, Long>()

    private val todaySummaryFlow = activeDateKeyFlow.flatMapLatest { dateKey ->
        repository.observeDaySummary(dateKey)
    }

    val uiState: StateFlow<WirdUiState> = combine(
        activeDateKeyFlow,
        repository.settingsFlow,
        todaySummaryFlow,
        repository.templateItemsFlow,
        combine(repository.observeAllHistorySummaries(), overlayFlow) { h, o -> h to o }
    ) { dateKey, settings, todaySummary, templates, (historyList, overlay) ->
        val localDate = ArabicDateUtils.parseDateKey(dateKey)
        val allAdhkar = AuthenticAdhkarCatalog.allSuggestions
        val startIndex = (overlay.adhkarSetIndex * 3) % allAdhkar.size
        val visibleAdhkar = (0 until 3).map { offset ->
            allAdhkar[(startIndex + offset) % allAdhkar.size]
        }

        val resolvedSettings = settings ?: UserSettingsEntity()
        val ageEnum = runCatching { AgeGroup.valueOf(resolvedSettings.ageGroup) }.getOrDefault(AgeGroup.YOUNG_ADULTS)
        val genderEnum = runCatching { GenderGroup.valueOf(resolvedSettings.genderGroup) }.getOrDefault(GenderGroup.UNSPECIFIED)
        val curatedMotivations = MotivationalLibrary.getCuratedMessages(
            ageGroup = ageEnum,
            genderGroup = genderEnum,
            todaySummary = todaySummary,
            hourOfDay = clock.currentHourOfDay()
        )
        val activeMotivation = curatedMotivations[overlay.motivationIndex.mod(curatedMotivations.size)]

        WirdUiState(
            isLoading = settings == null || todaySummary == null,
            todayDateKey = dateKey,
            gregorianDateText = ArabicDateUtils.formatGregorianArabic(localDate),
            hijriDateText = ArabicDateUtils.formatHijriArabic(localDate),
            greetingText = ArabicDateUtils.greetingForHour(clock.currentHourOfDay()),
            settings = resolvedSettings,
            todaySummary = todaySummary,
            templateItems = templates,
            historySummaries = historyList,
            visibleAdhkarSuggestions = visibleAdhkar,
            currentMotivation = activeMotivation,
            overlay = overlay
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WirdUiState()
    )

    init {
        refreshToday()
    }

    fun refreshToday() {
        viewModelScope.launch {
            val syncedDateKey = repository.ensureInitializedAndSyncToday()
            activeDateKeyFlow.value = syncedDateKey
        }
    }

    fun onToggleDailyItem(item: DailyRecordItemEntity) {
        val now = clock.currentTimeMillis()
        val lastTap = lastTapTimeByItemId[item.id] ?: 0L
        // Prevent accidental duplicate double-tap within 250ms
        if (now - lastTap < 250L && lastTap != 0L) return
        lastTapTimeByItemId[item.id] = now

        val willComplete = !item.isCompleted
        val currentState = uiState.value

        viewModelScope.launch {
            repository.toggleDailyItemCompletion(item)
            if (willComplete) {
                val praise = MotivationalLibrary.getCelebrationPraise(
                    deedTitle = item.title,
                    genderGroup = currentState.genderGroupEnum,
                    ageGroup = currentState.ageGroupEnum
                )
                overlayFlow.update {
                    it.copy(
                        celebrationBurstId = it.celebrationBurstId + 1,
                        celebrationText = praise,
                        recentStatusBanner = praise
                    )
                }
            } else {
                overlayFlow.update {
                    it.copy(
                        celebrationText = null,
                        recentStatusBanner = "تم إلغاء علامة «${item.title}» لتصحيح السجل"
                    )
                }
            }
        }
    }

    fun onToggleFivePrayersExpanded() {
        overlayFlow.update { it.copy(isFivePrayersExpanded = !it.isFivePrayersExpanded) }
    }

    fun onToggleSinglePrayer(item: DailyRecordItemEntity, prayer: FivePrayer) {
        val wasCompleted = item.isPrayerCompleted(prayer)
        val currentState = uiState.value
        viewModelScope.launch {
            repository.toggleSingleFivePrayer(item, prayer)
            if (!wasCompleted) {
                val praise = MotivationalLibrary.getCelebrationPraise(
                    deedTitle = "صلاة ${prayer.arabicName}",
                    genderGroup = currentState.genderGroupEnum,
                    ageGroup = currentState.ageGroupEnum
                )
                overlayFlow.update {
                    it.copy(
                        isFivePrayersExpanded = true,
                        celebrationBurstId = it.celebrationBurstId + 1,
                        celebrationText = praise,
                        recentStatusBanner = praise
                    )
                }
            } else {
                overlayFlow.update {
                    it.copy(
                        isFivePrayersExpanded = true,
                        celebrationText = null,
                        recentStatusBanner = "تم تصحيح سجل صلاة ${prayer.arabicName}"
                    )
                }
            }
        }
    }

    fun onSetAllFivePrayers(item: DailyRecordItemEntity, completed: Boolean) {
        val currentState = uiState.value
        viewModelScope.launch {
            repository.setAllFivePrayers(item, completed)
            if (completed) {
                val praise = MotivationalLibrary.getCelebrationPraise(
                    deedTitle = "الصلوات الخمس كاملة",
                    genderGroup = currentState.genderGroupEnum,
                    ageGroup = currentState.ageGroupEnum
                )
                overlayFlow.update {
                    it.copy(
                        isFivePrayersExpanded = true,
                        celebrationBurstId = it.celebrationBurstId + 1,
                        celebrationText = praise,
                        recentStatusBanner = praise
                    )
                }
            } else {
                overlayFlow.update {
                    it.copy(
                        isFivePrayersExpanded = true,
                        celebrationText = null,
                        recentStatusBanner = "تمت إعادة ضبط جدول الصلوات الخمس لتصحيح السجل"
                    )
                }
            }
        }
    }

    fun cycleMotivation() {
        overlayFlow.update { it.copy(motivationIndex = it.motivationIndex + 1) }
    }

    fun onFlashNotificationSent(title: String, body: String) {
        viewModelScope.launch {
            repository.updateReminderPreference(true, uiState.value.settings.reminderTime)
            overlayFlow.update {
                it.copy(
                    celebrationBurstId = it.celebrationBurstId + 1,
                    recentStatusBanner = "$title: $body"
                )
            }
        }
    }

    fun clearStatusBanner() {
        overlayFlow.update { it.copy(recentStatusBanner = null, celebrationText = null) }
    }

    // Electronic Counter actions
    fun openCounter(dhikrTitle: String? = null) {
        viewModelScope.launch {
            if (!dhikrTitle.isNullOrBlank()) {
                repository.selectDhikrForCounter(dhikrTitle)
            }
            overlayFlow.update {
                it.copy(
                    isCounterOpen = true,
                    isResetConfirmVisible = false,
                    selectedDhikr = null
                )
            }
        }
    }

    fun closeCounter() {
        overlayFlow.update {
            it.copy(isCounterOpen = false, isResetConfirmVisible = false)
        }
    }

    fun incrementCounter() {
        viewModelScope.launch {
            repository.incrementCounter(1)
        }
    }

    fun requestResetCounter() {
        overlayFlow.update { it.copy(isResetConfirmVisible = true) }
    }

    fun cancelResetCounter() {
        overlayFlow.update { it.copy(isResetConfirmVisible = false) }
    }

    fun confirmResetCounter() {
        viewModelScope.launch {
            repository.resetCounter()
            overlayFlow.update { it.copy(isResetConfirmVisible = false) }
        }
    }

    // Suggested Adhkar actions
    fun cycleSuggestedAdhkar() {
        overlayFlow.update { it.copy(adhkarSetIndex = it.adhkarSetIndex + 1) }
    }

    fun openDhikrReader(dhikr: SuggestedDhikr) {
        overlayFlow.update { it.copy(selectedDhikr = dhikr) }
    }

    fun closeDhikrReader() {
        overlayFlow.update { it.copy(selectedDhikr = null) }
    }

    // Customization & Onboarding actions
    fun openCustomization() {
        overlayFlow.update { it.copy(isCustomizationOpen = true) }
    }

    fun closeCustomization() {
        overlayFlow.update { it.copy(isCustomizationOpen = false) }
    }

    fun applyProfilePreset(
        ageGroup: AgeGroup,
        genderGroup: GenderGroup,
        intensity: WirdIntensity
    ) {
        viewModelScope.launch {
            repository.applyProfilePreset(
                ageGroup = ageGroup,
                genderGroup = genderGroup,
                intensity = intensity,
                markOnboardingComplete = true
            )
            overlayFlow.update {
                it.copy(
                    recentStatusBanner = "تكيّف المظهر والجدول مع (${genderGroup.arabicLabel} • ${ageGroup.arabicLabel})"
                )
            }
        }
    }

    fun dismissInitialOnboarding() {
        viewModelScope.launch {
            repository.completeOnboardingWithCurrentSettings()
        }
    }

    fun toggleTemplateItem(item: TemplateItemEntity) {
        viewModelScope.launch {
            repository.toggleTemplateItemEnabled(item)
        }
    }

    fun addCustomDeed(title: String, subtitle: String) {
        viewModelScope.launch {
            repository.addCustomTemplateItem(title, subtitle)
            overlayFlow.update {
                it.copy(recentStatusBanner = "تمت إضافة «${title.trim()}» إلى جدولك")
            }
        }
    }

    fun deleteCustomDeed(item: TemplateItemEntity) {
        viewModelScope.launch {
            repository.deleteCustomTemplateItem(item)
        }
    }

    fun moveTemplateItem(item: TemplateItemEntity, moveUp: Boolean) {
        viewModelScope.launch {
            repository.moveTemplateItem(item, moveUp)
        }
    }

    fun cycleDarkMode() {
        val current = uiState.value.darkModeEnum
        val next = when (current) {
            DarkModePreference.SYSTEM -> DarkModePreference.DARK
            DarkModePreference.DARK -> DarkModePreference.LIGHT
            DarkModePreference.LIGHT -> DarkModePreference.SYSTEM
        }
        setDarkModePreference(next)
    }

    fun setDarkModePreference(pref: DarkModePreference) {
        viewModelScope.launch {
            repository.updateDarkModePreference(pref)
        }
    }

    // History sheet actions
    fun openHistory() {
        overlayFlow.update { it.copy(isHistoryOpen = true) }
    }

    fun closeHistory() {
        overlayFlow.update { it.copy(isHistoryOpen = false) }
    }

    class Factory(
        private val repository: WirdRepository,
        private val clock: ClockProvider = SystemClockProvider
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WirdViewModel(repository, clock) as T
        }
    }
}
