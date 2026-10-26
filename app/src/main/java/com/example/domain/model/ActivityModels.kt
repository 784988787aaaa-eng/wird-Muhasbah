package com.example.domain.model

enum class ActivityCategory(val key: String, val arabicLabel: String) {
    ALL("ALL", "الكل"),
    ADHKAR("ADHKAR", "الأذكار"),
    QURAN("QURAN", "القرآن"),
    SOCIAL("SOCIAL", "الاجتماعي"),
    FAMILY("FAMILY", "الأسرة"),
    SELF("SELF", "الذات");

    companion object {
        fun fromKey(key: String): ActivityCategory {
            return entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: SELF
        }
    }
}

data class ActivityDefinition(
    val id: Long = 0,
    val code: String,
    val title: String,
    val subtitle: String,
    val category: ActivityCategory,
    val iconKey: String,
    val isCustom: Boolean = false,
    val isEnabled: Boolean = true,
    val sortOrder: Int = 0,
    val targetCount: Int = 1
)

data class DailyActivityRecord(
    val dateKey: String,
    val definition: ActivityDefinition,
    val isCompleted: Boolean = false,
    val currentCount: Int = 0,
    val note: String = "",
    val completedAtMillis: Long? = null
)

data class AuthenticDhikrItem(
    val id: String,
    val categoryTitle: String,
    val shortText: String,
    val fullArabicText: String,
    val documentedSource: String,
    val documentedNote: String,
    val recommendedCount: Int = 33,
    val isPostPrayer: Boolean = false
)

data class TasbihCounterState(
    val activeDhikrId: String = "subhan_allah",
    val activeDhikrTitle: String = "سبحان الله",
    val currentCount: Int = 0,
    val targetCycle: Int = 33,
    val totalTodayCount: Int = 0
)
