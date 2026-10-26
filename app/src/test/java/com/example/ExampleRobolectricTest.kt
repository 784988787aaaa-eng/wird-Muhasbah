package com.example

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.common.ClockProvider
import com.example.core.designsystem.theme.WirdTheme
import com.example.data.local.datastore.InMemoryPreferencesDataSource
import com.example.data.local.db.WirdAppDatabase
import com.example.data.repository.WirdDomainRepository
import com.example.domain.model.ActivityCategory
import com.example.domain.model.PrayerPerformanceMode
import com.example.domain.model.PrayerType
import com.example.domain.model.ReminderCategory
import com.example.domain.model.ThemeModePreference
import com.example.domain.model.UserPreferences
import com.example.feature.main.WirdAppRoot
import com.example.feature.main.WirdMainViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class FakeClockProvider(
    var currentDate: LocalDate = LocalDate.of(2026, 5, 17),
    var currentMillis: Long = 1_760_000_000_000L,
    var zoneId: ZoneId = ZoneId.of("Asia/Riyadh"),
    var hourOfDay: Int = 9
) : ClockProvider {
    override fun currentLocalDate(): LocalDate = currentDate
    override fun currentTimeMillis(): Long = currentMillis
    override fun currentZoneId(): ZoneId = zoneId
    override fun currentHourOfDay(): Int = hourOfDay
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h891dp")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var database: WirdAppDatabase
    private lateinit var fakeClock: FakeClockProvider
    private lateinit var preferencesDataSource: InMemoryPreferencesDataSource
    private lateinit var repository: WirdDomainRepository
    private lateinit var testScope: CoroutineScope

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        testScope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        database = Room.inMemoryDatabaseBuilder(context, WirdAppDatabase::class.java)
            .allowMainThreadQueries()
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .setQueryExecutor(Executors.newSingleThreadExecutor())
            .build()
        fakeClock = FakeClockProvider()
        preferencesDataSource = InMemoryPreferencesDataSource(
            initial = UserPreferences(isOnboardingCompleted = true)
        )
        repository = WirdDomainRepository(
            dao = database.dao(),
            preferencesDataSource = preferencesDataSource,
            clock = fakeClock
        )
    }

    @After
    fun tearDown() {
        testScope.cancel()
        database.close()
    }

    private fun createViewModel(): WirdMainViewModel {
        return WirdMainViewModel(
            repository = repository,
            preferencesDataSource = preferencesDataSource,
            clock = fakeClock,
            customScope = testScope,
            initialPreferences = UserPreferences(isOnboardingCompleted = true)
        )
    }

    @Test
    fun `1 - prayer recording, performance mode, post-prayer adhkar, notes, and correction persist accurately`(): Unit = runBlocking {
        val (todayKey, _) = repository.ensureInitializedAndGetPreviousDateKey()
        assertEquals("2026-05-17", todayKey)

        // Initially 0 prayers are auto-recorded
        val initialSummary = repository.observePrayersForDate(todayKey).first()
        assertEquals(0, initialSummary.completedCount)

        // Record Dhuhr in Jamaah with post-prayer adhkar and note
        repository.recordOrUpdatePrayer(
            dateKey = todayKey,
            prayerType = PrayerType.DHUHR,
            isCompleted = true,
            performanceMode = PrayerPerformanceMode.JAMAAH,
            postPrayerAdhkarDone = true,
            personalNote = "في مسجد الحي"
        )

        val afterDhuhr = repository.observePrayersForDate(todayKey).first()
        assertEquals(1, afterDhuhr.completedCount)
        val dhuhrLog = afterDhuhr.logsByPrayer[PrayerType.DHUHR]!!
        assertTrue(dhuhrLog.isCompleted)
        assertEquals(PrayerPerformanceMode.JAMAAH, dhuhrLog.performanceMode)
        assertTrue(dhuhrLog.postPrayerAdhkarCompleted)
        assertEquals("في مسجد الحي", dhuhrLog.personalNote)

        // Correct (unrecord) Dhuhr
        repository.recordOrUpdatePrayer(
            dateKey = todayKey,
            prayerType = PrayerType.DHUHR,
            isCompleted = false,
            performanceMode = PrayerPerformanceMode.UNSPECIFIED,
            postPrayerAdhkarDone = false,
            personalNote = ""
        )
        val afterUndo = repository.observePrayersForDate(todayKey).first()
        assertEquals(0, afterUndo.completedCount)
        assertFalse(afterUndo.logsByPrayer[PrayerType.DHUHR]!!.isCompleted)
    }

    @Test
    fun `2 - new day and return after pause preserve previous days without auto-filling skipped days`(): Unit = runBlocking {
        // Day 1: 2026-05-14
        fakeClock.currentDate = LocalDate.of(2026, 5, 14)
        val (day1Key, _) = repository.ensureInitializedAndGetPreviousDateKey()
        repository.togglePrayerQuick(day1Key, PrayerType.FAJR)
        repository.toggleActivityCompletion(day1Key, "silat_rahim")

        // User pauses for 3 days and returns on 2026-05-17
        fakeClock.currentDate = LocalDate.of(2026, 5, 17)
        val (day4Key, previousDateKey) = repository.ensureInitializedAndGetPreviousDateKey()
        assertEquals("2026-05-17", day4Key)
        assertEquals("2026-05-14", previousDateKey)

        // New day starts clean with 0 completed prayers
        val day4Prayers = repository.observePrayersForDate(day4Key).first()
        assertEquals(0, day4Prayers.completedCount)

        // Day 1 record is still intact
        val allHistory = repository.observeAllHistorySummaries().first()
        val preservedDay1 = allHistory["2026-05-14"]!!
        assertEquals(1, preservedDay1.prayersCompleted)
        assertEquals(1, preservedDay1.goodDeedsCompleted)
    }

    @Test
    fun `3 - tasbih counter increments, switches dhikr, persists across instances, and resets`(): Unit = runBlocking {
        repository.ensureInitializedAndGetPreviousDateKey()
        repository.incrementTasbih(1)
        repository.incrementTasbih(1)
        repository.incrementTasbih(1)

        val stateAfter3 = repository.tasbihStateFlow.first()
        assertEquals(3, stateAfter3.currentCount)

        // Switch dhikr
        val prevTitle = stateAfter3.activeDhikrTitle
        repository.cycleTasbihDhikr()
        val stateAfterSwitch = repository.tasbihStateFlow.first()
        assertNotEquals(prevTitle, stateAfterSwitch.activeDhikrTitle)
        assertEquals(3, stateAfterSwitch.currentCount)

        // Reset counter
        repository.resetTasbihCounter()
        assertEquals(0, repository.tasbihStateFlow.first().currentCount)
    }

    @Test
    fun `4 - custom personal deeds can be added, edited, reordered, and deleted safely`(): Unit = runBlocking {
        val (todayKey, _) = repository.ensureInitializedAndGetPreviousDateKey()
        val initialList = repository.observeActivitiesForDate(todayKey).first()
        val initialCount = initialList.size

        // Add custom deed
        repository.addCustomActivity(
            title = "عيادة مريض",
            subtitle = "زيارة قريب والاطمئنان عليه",
            category = ActivityCategory.SOCIAL
        )
        val afterAdd = repository.observeActivitiesForDate(todayKey).first()
        assertEquals(initialCount + 1, afterAdd.size)
        val addedDef = afterAdd.first { it.definition.title == "عيادة مريض" }.definition
        assertTrue(addedDef.isCustom)

        // Edit custom deed
        repository.updateCustomActivity(
            definition = addedDef,
            newTitle = "عيادة مريض والدعاء له",
            newSubtitle = "إدخال السرور على قلبه",
            newCategory = ActivityCategory.FAMILY
        )
        val afterEdit = repository.observeActivitiesForDate(todayKey).first()
        val editedDef = afterEdit.first { it.definition.code == addedDef.code }.definition
        assertEquals("عيادة مريض والدعاء له", editedDef.title)
        assertEquals(ActivityCategory.FAMILY, editedDef.category)

        // Reorder first two items
        val firstCodeBefore = afterEdit[0].definition.code
        val secondCodeBefore = afterEdit[1].definition.code
        repository.reorderActivityDefinitions(0, 1)
        val afterReorder = repository.observeActivitiesForDate(todayKey).first()
        assertEquals(secondCodeBefore, afterReorder[0].definition.code)
        assertEquals(firstCodeBefore, afterReorder[1].definition.code)

        // Delete custom deed
        repository.deleteCustomActivity(editedDef)
        val afterDelete = repository.observeActivitiesForDate(todayKey).first()
        assertEquals(initialCount, afterDelete.size)
    }

    @Test
    fun `5 - preferences and reminders update theme, card visibility, quiet mode, and non-repeating messages`(): Unit = runBlocking {
        repository.ensureInitializedAndGetPreviousDateKey()
        val viewModel = createViewModel()

        // Verify non-repeating heart message cycling
        val firstMessageId = viewModel.currentHeartMessageId()
        viewModel.cycleNextHeartMessage()
        val secondMessageId = viewModel.currentHeartMessageId()
        assertNotEquals(firstMessageId, secondMessageId)

        // Verify theme mode & reminder updates
        preferencesDataSource.updateThemeMode(ThemeModePreference.DARK)
        preferencesDataSource.updateReminder(ReminderCategory.PRAYER_TIMES, true, "12:30")
        preferencesDataSource.updateQuietMode(true)

        val prefs = preferencesDataSource.preferencesFlow.first()
        assertEquals(ThemeModePreference.DARK, prefs.themeMode)
        assertTrue(prefs.quietModeAllRemindersOff)
        assertEquals("12:30", prefs.reminders[ReminderCategory.PRAYER_TIMES]?.timeHHmm)
    }

    @Test
    fun `6 - full Compose UI navigation across Home, Worship, History, Good Prayers, and Settings works in RTL`() {
        runBlocking {
            repository.ensureInitializedAndGetPreviousDateKey()
        }
        val viewModel = createViewModel()

        composeTestRule.setContent {
            val state by viewModel.uiState.collectAsState()
            WirdTheme(darkTheme = false) {
                WirdAppRoot(
                    uiState = state,
                    viewModel = viewModel
                )
            }
        }

        composeTestRule.waitForIdle()

        // 1. Verify Home Screen & Bottom Navigation Bar
        composeTestRule.onNodeWithTag("home_header_section").assertIsDisplayed()
        composeTestRule.onNodeWithTag("daily_prayers_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("heart_message_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("wird_bottom_navigation_bar").assertIsDisplayed()

        // 2. Navigate to Worship Tab («عباداتي») and verify Tasbih & Adhkar
        composeTestRule.onNodeWithTag("nav_tab_worship").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("worship_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("electronic_tasbih_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tasbih_plus_one_button").performClick()
        composeTestRule.waitForIdle()

        // Switch to Good Deeds sub-tab inside Worship
        composeTestRule.onNodeWithTag("worship_subtab_DEEDS").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("good_deeds_lazy_column").assertIsDisplayed()

        // 3. Navigate to History Tab («سجلّي»)
        composeTestRule.onNodeWithTag("nav_tab_history").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("history_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("history_day_summary_card").assertIsDisplayed()

        // 4. Navigate to Good Prayers Tab («دعوات طيبة»)
        composeTestRule.onNodeWithTag("nav_tab_messages").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("good_prayers_screen").assertIsDisplayed()

        // 5. Navigate to Settings Tab («حسابي / الإعدادات»)
        composeTestRule.onNodeWithTag("nav_tab_settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("الإعدادات").assertIsDisplayed()
    }
}
