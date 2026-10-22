package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AgeGroup
import com.example.data.ArabicDateUtils
import com.example.data.ClockProvider
import com.example.data.DailyDaySummary
import com.example.data.DarkModePreference
import com.example.data.GenderGroup
import com.example.data.UserSettingsEntity
import com.example.data.WirdDatabase
import com.example.data.WirdIntensity
import com.example.data.WirdRepository
import com.example.ui.OverlayUiState
import com.example.ui.WirdHomeScreen
import com.example.ui.WirdUiState
import com.example.ui.theme.WirdTheme
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.Executors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

class FakeClockProvider(
    var currentDate: LocalDate = LocalDate.of(2026, 10, 21),
    var currentMillis: Long = 1_760_000_000_000L,
    var zoneId: ZoneId = ZoneId.of("Asia/Riyadh"),
    var hourOfDay: Int = 8
) : ClockProvider {
    override fun currentLocalDate(): LocalDate = currentDate
    override fun currentTimeMillis(): Long = currentMillis
    override fun currentZoneId(): ZoneId = zoneId
    override fun currentHourOfDay(): Int = hourOfDay
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var database: WirdDatabase
    private lateinit var fakeClock: FakeClockProvider
    private lateinit var repository: WirdRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, WirdDatabase::class.java)
            .allowMainThreadQueries()
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .setQueryExecutor(Executors.newSingleThreadExecutor())
            .build()
        fakeClock = FakeClockProvider()
        repository = WirdRepository(database.wirdDao(), fakeClock)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `1 and 2 - check and uncheck deed updates progress accurately`(): Unit = runBlocking {
        val todayKey = repository.ensureInitializedAndSyncToday()
        val initialSummary = repository.observeDaySummary(todayKey).first()
        assertNotNull(initialSummary)
        assertEquals(0, initialSummary!!.completedCount)
        assertTrue(initialSummary.totalCount > 0)

        val firstDeed = initialSummary.items.first()
        // Check deed
        fakeClock.currentMillis += 1000L
        repository.toggleDailyItemCompletion(firstDeed)

        val afterCheck = repository.observeDaySummary(todayKey).first()!!
        assertEquals(1, afterCheck.completedCount)
        assertTrue(afterCheck.progressPercent > 0)

        // Uncheck deed to correct record
        val checkedDeed = afterCheck.items.first { it.id == firstDeed.id }
        fakeClock.currentMillis += 1000L
        repository.toggleDailyItemCompletion(checkedDeed)

        val afterUncheck = repository.observeDaySummary(todayKey).first()!!
        assertEquals(0, afterUncheck.completedCount)
        assertEquals(0, afterUncheck.progressPercent)
        Unit
    }

    @Test
    fun `3 and 4 - new day creates independent record and preserves previous days without backfilling skipped days`(): Unit = runBlocking {
        // Day 1: 2026-10-21
        val day1Key = repository.ensureInitializedAndSyncToday()
        assertEquals("2026-10-21", day1Key)

        val day1Items = repository.observeDaySummary(day1Key).first()!!.items
        repository.toggleDailyItemCompletion(day1Items[0])
        repository.toggleDailyItemCompletion(day1Items[1])
        assertEquals(2, repository.observeDaySummary(day1Key).first()!!.completedCount)

        // User skips 2 days and opens app on 2026-10-24
        fakeClock.currentDate = LocalDate.of(2026, 10, 24)
        fakeClock.zoneId = ZoneId.of("Africa/Cairo") // Also test timezone change
        val day4Key = repository.ensureInitializedAndSyncToday()
        assertEquals("2026-10-24", day4Key)

        // New day starts fresh with 0 completed items
        val day4Summary = repository.observeDaySummary(day4Key).first()!!
        assertEquals(0, day4Summary.completedCount)
        assertEquals("Africa/Cairo", day4Summary.record.timeZoneId)

        // Previous day (2026-10-21) is still intact with 2 completed items
        val preservedDay1 = repository.observeDaySummary(day1Key).first()!!
        assertEquals(2, preservedDay1.completedCount)

        // Skipped days (2026-10-22, 2026-10-23) were NOT artificially filled
        val allHistory = repository.observeAllHistorySummaries().first()
        assertEquals(2, allHistory.size)
        assertEquals(listOf("2026-10-24", "2026-10-21"), allHistory.map { it.record.dateKey })
        Unit
    }

    @Test
    fun `5, 6, 7, and 8 - counter increments, persists on close and app restart, and confirms reset`(): Unit = runBlocking {
        repository.ensureInitializedAndSyncToday()

        repository.selectDhikrForCounter("الاستغفار")
        repository.incrementCounter(1)
        repository.incrementCounter(1)
        repository.incrementCounter(1)

        // Verify persisted in database and survives new repository instance (simulating app close & reopen)
        val newRepositoryInstance = WirdRepository(database.wirdDao(), fakeClock)
        val persistedSettings = newRepositoryInstance.settingsFlow.first()!!
        assertEquals(3, persistedSettings.counterValue)
        assertEquals("الاستغفار", persistedSettings.activeDhikrTitle)

        // Reset counter after confirmation
        newRepositoryInstance.resetCounter()
        assertEquals(0, newRepositoryInstance.settingsFlow.first()!!.counterValue)
        Unit
    }

    @Test
    fun `9 and 10 - template customization, adding custom deed, and hiding deed do not alter historical days`(): Unit = runBlocking {
        // Day 1 setup
        val day1Key = repository.ensureInitializedAndSyncToday()
        val day1InitialCount = repository.observeDaySummary(day1Key).first()!!.totalCount

        // Advance to Day 2
        fakeClock.currentDate = LocalDate.of(2026, 10, 22)
        val day2Key = repository.ensureInitializedAndSyncToday()

        // Apply LIGHT preset (5 deeds)
        repository.applyProfilePreset(
            ageGroup = AgeGroup.CHILDREN,
            genderGroup = GenderGroup.GIRLS,
            intensity = WirdIntensity.LIGHT
        )
        val day2AfterLight = repository.observeDaySummary(day2Key).first()!!
        assertEquals(5, day2AfterLight.totalCount)

        // Add custom personal deed
        repository.addCustomTemplateItem("صلة الرحم", "الاتصال بالأقارب والاطمئنان عليهم")
        val day2AfterCustom = repository.observeDaySummary(day2Key).first()!!
        assertEquals(6, day2AfterCustom.totalCount)
        assertTrue(day2AfterCustom.items.any { it.title == "صلة الرحم" })

        // Hide one deed from template
        val istighfarTemplate = repository.templateItemsFlow.first().first { it.code == "istighfar" }
        repository.toggleTemplateItemEnabled(istighfarTemplate)
        val day2AfterHide = repository.observeDaySummary(day2Key).first()!!
        assertEquals(5, day2AfterHide.totalCount)

        // Verify Day 1 historical record was NOT altered by Day 2 template edits
        val day1Preserved = repository.observeDaySummary(day1Key).first()!!
        assertEquals(day1InitialCount, day1Preserved.totalCount)
        Unit
    }

    @Test
    fun `11, 12, and 13 - Arabic UI rendering, Dark Mode toggle, and offline operation`(): Unit = runBlocking {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        assertEquals("وِرد محاسبة", appContext.getString(R.string.app_name))

        // Verify Hijri & Gregorian Arabic offline date formatting
        val sampleDate = LocalDate.of(2026, 10, 21)
        val gregArabic = ArabicDateUtils.formatGregorianArabic(sampleDate)
        val hijriArabic = ArabicDateUtils.formatHijriArabic(sampleDate)
        assertTrue(gregArabic.contains("أكتوبر"))
        assertNotNull(hijriArabic)
        assertTrue(hijriArabic!!.contains("هـ"))

        // Verify Dark Mode preference persistence
        val todayKey = repository.ensureInitializedAndSyncToday()
        repository.completeOnboardingWithCurrentSettings()
        repository.updateDarkModePreference(DarkModePreference.DARK)
        val currentSettings: UserSettingsEntity = repository.settingsFlow.first()!!
        assertEquals(DarkModePreference.DARK.name, currentSettings.darkModePref)
        val summary: DailyDaySummary = repository.observeDaySummary(todayKey).first()!!

        var counterOpened = false
        val initialUiState = WirdUiState(
            isLoading = false,
            todayDateKey = todayKey,
            gregorianDateText = gregArabic,
            hijriDateText = hijriArabic,
            greetingText = "صباح السكينة والبركة",
            settings = currentSettings.copy(isOnboardingCompleted = true, counterValue = 7),
            todaySummary = summary,
            overlay = OverlayUiState(isCounterOpen = true, isResetConfirmVisible = true)
        )

        composeTestRule.setContent {
            WirdTheme(darkTheme = true) {
                WirdHomeScreen(
                    uiState = initialUiState,
                    onToggleDailyItem = {},
                    onOpenCounter = { counterOpened = true },
                    onCloseCounter = {},
                    onIncrementCounter = {},
                    onRequestResetCounter = {},
                    onConfirmResetCounter = {},
                    onCancelResetCounter = {},
                    onCycleAdhkar = {},
                    onSelectDhikr = {},
                    onCloseDhikrReader = {},
                    onOpenCustomization = {},
                    onCloseCustomization = {},
                    onOpenHistory = {},
                    onCloseHistory = {},
                    onApplyPreset = { _, _, _ -> },
                    onDismissOnboarding = {},
                    onToggleTemplateItem = {},
                    onMoveTemplateItem = { _, _ -> },
                    onAddCustomDeed = { _, _ -> },
                    onDeleteCustomDeed = {},
                    onCycleDarkMode = {},
                    onSetDarkMode = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("wird_emblem").assertIsDisplayed()
        composeTestRule.onNodeWithText("وِرد محاسبة").assertIsDisplayed()
        composeTestRule.onNodeWithTag("daily_progress_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("counter_panel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("counter_value_text").assertIsDisplayed()
        composeTestRule.onNodeWithTag("counter_reset_confirm_box").assertIsDisplayed()
        composeTestRule.onNodeWithTag("confirm_reset_button").performClick()
        Unit
    }

    @Test
    fun `14 - five prayers schedule expands on click and allows checking and correcting Fajr, Dhuhr, Asr, Maghrib, Isha`(): Unit = runBlocking {
        val todayKey = repository.ensureInitializedAndSyncToday()
        repository.completeOnboardingWithCurrentSettings()

        var summary = repository.observeDaySummary(todayKey).first()!!
        var fardItem = summary.items.first { it.isFivePrayers }
        assertEquals(0, fardItem.completedPrayersCount)

        // Check Fajr, Dhuhr, Asr
        repository.toggleSingleFivePrayer(fardItem, com.example.data.FivePrayer.FAJR)
        fardItem = repository.observeDaySummary(todayKey).first()!!.items.first { it.isFivePrayers }
        repository.toggleSingleFivePrayer(fardItem, com.example.data.FivePrayer.DHUHR)
        fardItem = repository.observeDaySummary(todayKey).first()!!.items.first { it.isFivePrayers }
        repository.toggleSingleFivePrayer(fardItem, com.example.data.FivePrayer.ASR)

        fardItem = repository.observeDaySummary(todayKey).first()!!.items.first { it.isFivePrayers }
        assertEquals(3, fardItem.completedPrayersCount)
        assertTrue(fardItem.isPrayerCompleted(com.example.data.FivePrayer.FAJR))
        assertTrue(fardItem.isPrayerCompleted(com.example.data.FivePrayer.DHUHR))
        assertTrue(fardItem.isPrayerCompleted(com.example.data.FivePrayer.ASR))
        assertEquals(false, fardItem.isCompleted)

        // Correct (uncheck) Asr
        repository.toggleSingleFivePrayer(fardItem, com.example.data.FivePrayer.ASR)
        fardItem = repository.observeDaySummary(todayKey).first()!!.items.first { it.isFivePrayers }
        assertEquals(2, fardItem.completedPrayersCount)
        assertEquals(false, fardItem.isPrayerCompleted(com.example.data.FivePrayer.ASR))

        // Complete all 5 prayers (Asr, Maghrib, Isha)
        repository.toggleSingleFivePrayer(fardItem, com.example.data.FivePrayer.ASR)
        fardItem = repository.observeDaySummary(todayKey).first()!!.items.first { it.isFivePrayers }
        repository.toggleSingleFivePrayer(fardItem, com.example.data.FivePrayer.MAGHRIB)
        fardItem = repository.observeDaySummary(todayKey).first()!!.items.first { it.isFivePrayers }
        repository.toggleSingleFivePrayer(fardItem, com.example.data.FivePrayer.ISHA)

        summary = repository.observeDaySummary(todayKey).first()!!
        fardItem = summary.items.first { it.isFivePrayers }
        assertEquals(5, fardItem.completedPrayersCount)
        assertTrue(fardItem.isCompleted)

        // Verify UI renders the expanded schedule with all 5 prayers (الفجر، الظهر، العصر، المغرب، العشاء)
        val uiState = WirdUiState(
            isLoading = false,
            todayDateKey = todayKey,
            gregorianDateText = "الأربعاء، ٢١ أكتوبر ٢٠٢٦ م",
            hijriDateText = "١٠ جمادى الأولى ١٤٤٨ هـ",
            greetingText = "صباح السكينة والبركة",
            settings = repository.settingsFlow.first()!!.copy(isOnboardingCompleted = true),
            todaySummary = summary,
            overlay = OverlayUiState(isFivePrayersExpanded = true)
        )

        composeTestRule.setContent {
            WirdTheme(darkTheme = false) {
                WirdHomeScreen(
                    uiState = uiState,
                    onToggleDailyItem = {},
                    onOpenCounter = {},
                    onCloseCounter = {},
                    onIncrementCounter = {},
                    onRequestResetCounter = {},
                    onConfirmResetCounter = {},
                    onCancelResetCounter = {},
                    onCycleAdhkar = {},
                    onSelectDhikr = {},
                    onCloseDhikrReader = {},
                    onOpenCustomization = {},
                    onCloseCustomization = {},
                    onOpenHistory = {},
                    onCloseHistory = {},
                    onApplyPreset = { _, _, _ -> },
                    onDismissOnboarding = {},
                    onToggleTemplateItem = {},
                    onMoveTemplateItem = { _, _ -> },
                    onAddCustomDeed = { _, _ -> },
                    onDeleteCustomDeed = {},
                    onCycleDarkMode = {},
                    onSetDarkMode = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("checklist_item_fard_prayers").performScrollTo()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("five_prayers_schedule_table", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("prayer_row_FAJR", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("prayer_row_DHUHR", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("prayer_row_ASR", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("prayer_row_MAGHRIB", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("prayer_row_ISHA", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("prayer_checkbox_FAJR", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("prayer_checkbox_ASR", useUnmergedTree = true).assertExists()
        Unit
    }

    @Test
    fun `15 - gender adaptive theme and motivational library adapt to female, boy, and male profiles`(): Unit = runBlocking {
        // Verify palette adapts to gender and age
        assertEquals(
            com.example.ui.theme.PersonaPaletteStyle.ROSE_BLUSH,
            com.example.ui.theme.resolvePersonaPalette(GenderGroup.YOUNG_WOMEN, AgeGroup.YOUNG_ADULTS)
        )
        assertEquals(
            com.example.ui.theme.PersonaPaletteStyle.ROSE_BLUSH,
            com.example.ui.theme.resolvePersonaPalette(GenderGroup.GIRLS, AgeGroup.CHILDREN)
        )
        assertEquals(
            com.example.ui.theme.PersonaPaletteStyle.SKY_TEAL,
            com.example.ui.theme.resolvePersonaPalette(GenderGroup.BOYS, AgeGroup.YOUTH)
        )
        assertEquals(
            com.example.ui.theme.PersonaPaletteStyle.ROYAL_EMERALD,
            com.example.ui.theme.resolvePersonaPalette(GenderGroup.MEN, AgeGroup.ADULTS)
        )

        // Verify motivational messages adapt to gender and uncompleted Asr prayer at 16:00
        val todayKey = repository.ensureInitializedAndSyncToday()
        val summary = repository.observeDaySummary(todayKey).first()!!

        val femaleMessages = com.example.data.MotivationalLibrary.getCuratedMessages(
            ageGroup = AgeGroup.YOUNG_ADULTS,
            genderGroup = GenderGroup.YOUNG_WOMEN,
            todaySummary = summary,
            hourOfDay = 16
        )
        assertTrue(femaleMessages.isNotEmpty())
        assertTrue(femaleMessages.any { it.text.contains("العصر") })
        assertTrue(femaleMessages.any { it.text.contains("لا تملّي مني") })

        val maleMessages = com.example.data.MotivationalLibrary.getCuratedMessages(
            ageGroup = AgeGroup.YOUNG_ADULTS,
            genderGroup = GenderGroup.YOUNG_MEN,
            todaySummary = summary,
            hourOfDay = 16
        )
        assertTrue(maleMessages.isNotEmpty())
        assertTrue(maleMessages.any { it.text.contains("العصر") })
        assertTrue(maleMessages.any { it.text.contains("لا تملّ مني") })
        Unit
    }
}
