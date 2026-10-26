package com.example.domain.model

enum class ThemeModePreference(val arabicLabel: String) {
    SYSTEM("تلقائي حسب النظام"),
    LIGHT("الوضع الفاتح"),
    DARK("الوضع الداكن")
}

enum class ReminderCategory(
    val key: String,
    val arabicTitle: String,
    val arabicSubtitle: String,
    val defaultTime: String
) {
    PRAYER_TIMES("PRAYER_TIMES", "تذكير الصلوات", "تنبيه لطيف لمراجعة الصلوات اليومية", "12:30"),
    MORNING_EVENING_ADHKAR("ADHKAR", "الأذكار اليومية", "تذكير هادئ لأذكار الصباح والمساء", "07:00"),
    GOOD_DEEDS("GOOD_DEEDS", "أعمال الخير والقرآن", "تذكير ودي بمساحة الخير أو ورد القرآن", "17:00"),
    DAILY_REFLECTION("DAILY_REFLECTION", "ختام اليوم", "مراجعة هادئة لسجل يومك قبل النوم", "21:00")
}

data class ReminderItemPreference(
    val category: ReminderCategory,
    val isEnabled: Boolean = false,
    val timeHHmm: String = category.defaultTime,
    val enabledDaysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7)
)

data class UserPreferences(
    val displayName: String = "",
    val isOnboardingCompleted: Boolean = false,
    val themeMode: ThemeModePreference = ThemeModePreference.LIGHT,
    val messageTone: MessageToneStyle = MessageToneStyle.CALM,
    val showHeartMessageCard: Boolean = true,
    val showGoodnessSpaceCard: Boolean = true,
    val showJourneyGlimpseCard: Boolean = true,
    val showHijriDate: Boolean = true,
    val useEasternArabicDigits: Boolean = false,
    val quietModeAllRemindersOff: Boolean = false,
    val lastActiveDateKey: String = "",
    val reminders: Map<ReminderCategory, ReminderItemPreference> = ReminderCategory.entries.associateWith {
        ReminderItemPreference(category = it)
    }
)
