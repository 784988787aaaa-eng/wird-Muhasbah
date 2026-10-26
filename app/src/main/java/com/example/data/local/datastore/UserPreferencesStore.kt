package com.example.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.model.MessageToneStyle
import com.example.domain.model.ReminderCategory
import com.example.domain.model.ReminderItemPreference
import com.example.domain.model.ThemeModePreference
import com.example.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private val Context.wirdDataStore: DataStore<Preferences> by preferencesDataStore(name = "wird_user_preferences")

interface PreferencesDataSource {
    val preferencesFlow: Flow<UserPreferences>
    suspend fun updateDisplayName(name: String)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun updateThemeMode(mode: ThemeModePreference)
    suspend fun updateMessageTone(tone: MessageToneStyle)
    suspend fun updateHomeCardVisibility(
        showHeartMessage: Boolean? = null,
        showGoodnessSpace: Boolean? = null,
        showJourneyGlimpse: Boolean? = null
    )
    suspend fun updateHijriAndDigits(showHijri: Boolean? = null, easternDigits: Boolean? = null)
    suspend fun updateQuietMode(quietAllOff: Boolean)
    suspend fun updateReminder(category: ReminderCategory, isEnabled: Boolean, timeHHmm: String)
    suspend fun updateLastActiveDateKey(dateKey: String)
}

class DataStorePreferencesDataSource(
    private val context: Context
) : PreferencesDataSource {

    private object Keys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val MESSAGE_TONE = stringPreferencesKey("message_tone")
        val SHOW_HEART_CARD = booleanPreferencesKey("show_heart_card")
        val SHOW_GOODNESS_CARD = booleanPreferencesKey("show_goodness_card")
        val SHOW_JOURNEY_CARD = booleanPreferencesKey("show_journey_card")
        val SHOW_HIJRI = booleanPreferencesKey("show_hijri")
        val USE_EASTERN_DIGITS = booleanPreferencesKey("use_eastern_digits")
        val QUIET_MODE_ALL_OFF = booleanPreferencesKey("quiet_mode_all_off")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")

        fun reminderEnabledKey(cat: ReminderCategory) = booleanPreferencesKey("rem_en_${cat.key}")
        fun reminderTimeKey(cat: ReminderCategory) = stringPreferencesKey("rem_time_${cat.key}")
    }

    override val preferencesFlow: Flow<UserPreferences> = context.wirdDataStore.data.map { prefs ->
        val theme = runCatching {
            ThemeModePreference.valueOf(prefs[Keys.THEME_MODE] ?: ThemeModePreference.LIGHT.name)
        }.getOrDefault(ThemeModePreference.LIGHT)

        val tone = runCatching {
            MessageToneStyle.valueOf(prefs[Keys.MESSAGE_TONE] ?: MessageToneStyle.CALM.name)
        }.getOrDefault(MessageToneStyle.CALM)

        val remindersMap = ReminderCategory.entries.associateWith { cat ->
            ReminderItemPreference(
                category = cat,
                isEnabled = prefs[Keys.reminderEnabledKey(cat)] ?: false,
                timeHHmm = prefs[Keys.reminderTimeKey(cat)] ?: cat.defaultTime
            )
        }

        UserPreferences(
            displayName = prefs[Keys.DISPLAY_NAME] ?: "",
            isOnboardingCompleted = prefs[Keys.ONBOARDING_DONE] ?: false,
            themeMode = theme,
            messageTone = tone,
            showHeartMessageCard = prefs[Keys.SHOW_HEART_CARD] ?: true,
            showGoodnessSpaceCard = prefs[Keys.SHOW_GOODNESS_CARD] ?: true,
            showJourneyGlimpseCard = prefs[Keys.SHOW_JOURNEY_CARD] ?: true,
            showHijriDate = prefs[Keys.SHOW_HIJRI] ?: true,
            useEasternArabicDigits = prefs[Keys.USE_EASTERN_DIGITS] ?: false,
            quietModeAllRemindersOff = prefs[Keys.QUIET_MODE_ALL_OFF] ?: false,
            lastActiveDateKey = prefs[Keys.LAST_ACTIVE_DATE] ?: "",
            reminders = remindersMap
        )
    }

    override suspend fun updateDisplayName(name: String) {
        context.wirdDataStore.edit { it[Keys.DISPLAY_NAME] = name.trim() }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.wirdDataStore.edit { it[Keys.ONBOARDING_DONE] = completed }
    }

    override suspend fun updateThemeMode(mode: ThemeModePreference) {
        context.wirdDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    override suspend fun updateMessageTone(tone: MessageToneStyle) {
        context.wirdDataStore.edit { it[Keys.MESSAGE_TONE] = tone.name }
    }

    override suspend fun updateHomeCardVisibility(
        showHeartMessage: Boolean?,
        showGoodnessSpace: Boolean?,
        showJourneyGlimpse: Boolean?
    ) {
        context.wirdDataStore.edit { prefs ->
            showHeartMessage?.let { prefs[Keys.SHOW_HEART_CARD] = it }
            showGoodnessSpace?.let { prefs[Keys.SHOW_GOODNESS_CARD] = it }
            showJourneyGlimpse?.let { prefs[Keys.SHOW_JOURNEY_CARD] = it }
        }
    }

    override suspend fun updateHijriAndDigits(showHijri: Boolean?, easternDigits: Boolean?) {
        context.wirdDataStore.edit { prefs ->
            showHijri?.let { prefs[Keys.SHOW_HIJRI] = it }
            easternDigits?.let { prefs[Keys.USE_EASTERN_DIGITS] = it }
        }
    }

    override suspend fun updateQuietMode(quietAllOff: Boolean) {
        context.wirdDataStore.edit { it[Keys.QUIET_MODE_ALL_OFF] = quietAllOff }
    }

    override suspend fun updateReminder(category: ReminderCategory, isEnabled: Boolean, timeHHmm: String) {
        context.wirdDataStore.edit { prefs ->
            prefs[Keys.reminderEnabledKey(category)] = isEnabled
            prefs[Keys.reminderTimeKey(category)] = timeHHmm
        }
    }

    override suspend fun updateLastActiveDateKey(dateKey: String) {
        context.wirdDataStore.edit { it[Keys.LAST_ACTIVE_DATE] = dateKey }
    }
}

/**
 * Fast, deterministic in-memory implementation of PreferencesDataSource for JVM & Robolectric tests.
 */
class InMemoryPreferencesDataSource(
    initial: UserPreferences = UserPreferences(isOnboardingCompleted = true)
) : PreferencesDataSource {
    private val state = MutableStateFlow(initial)
    override val preferencesFlow: Flow<UserPreferences> = state.asStateFlow()

    override suspend fun updateDisplayName(name: String) {
        state.update { it.copy(displayName = name.trim()) }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        state.update { it.copy(isOnboardingCompleted = completed) }
    }

    override suspend fun updateThemeMode(mode: ThemeModePreference) {
        state.update { it.copy(themeMode = mode) }
    }

    override suspend fun updateMessageTone(tone: MessageToneStyle) {
        state.update { it.copy(messageTone = tone) }
    }

    override suspend fun updateHomeCardVisibility(
        showHeartMessage: Boolean?,
        showGoodnessSpace: Boolean?,
        showJourneyGlimpse: Boolean?
    ) {
        state.update {
            it.copy(
                showHeartMessageCard = showHeartMessage ?: it.showHeartMessageCard,
                showGoodnessSpaceCard = showGoodnessSpace ?: it.showGoodnessSpaceCard,
                showJourneyGlimpseCard = showJourneyGlimpse ?: it.showJourneyGlimpseCard
            )
        }
    }

    override suspend fun updateHijriAndDigits(showHijri: Boolean?, easternDigits: Boolean?) {
        state.update {
            it.copy(
                showHijriDate = showHijri ?: it.showHijriDate,
                useEasternArabicDigits = easternDigits ?: it.useEasternArabicDigits
            )
        }
    }

    override suspend fun updateQuietMode(quietAllOff: Boolean) {
        state.update { it.copy(quietModeAllRemindersOff = quietAllOff) }
    }

    override suspend fun updateReminder(category: ReminderCategory, isEnabled: Boolean, timeHHmm: String) {
        state.update { current ->
            val updatedMap = current.reminders.toMutableMap()
            updatedMap[category] = ReminderItemPreference(
                category = category,
                isEnabled = isEnabled,
                timeHHmm = timeHHmm
            )
            current.copy(reminders = updatedMap)
        }
    }

    override suspend fun updateLastActiveDateKey(dateKey: String) {
        state.update { it.copy(lastActiveDateKey = dateKey) }
    }
}
