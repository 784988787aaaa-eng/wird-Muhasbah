package com.example.domain.model

enum class PrayerType(
    val code: String,
    val arabicName: String,
    val defaultTimeHint: String,
    val sortOrder: Int
) {
    FAJR("FAJR", "الفجر", "04:45", 1),
    DHUHR("DHUHR", "الظهر", "12:15", 2),
    ASR("ASR", "العصر", "15:40", 3),
    MAGHRIB("MAGHRIB", "المغرب", "18:20", 4),
    ISHA("ISHA", "العشاء", "19:50", 5);

    companion object {
        fun fromCode(code: String): PrayerType {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: FAJR
        }

        fun suggestedPrayerForHour(hourOfDay: Int): PrayerType {
            return when (hourOfDay) {
                in 4..10 -> FAJR
                in 11..14 -> DHUHR
                in 15..16 -> ASR
                in 17..18 -> MAGHRIB
                else -> ISHA
            }
        }
    }
}

enum class PrayerStatus(val arabicLabel: String) {
    NOT_RECORDED("غير مسجلة"),
    COMPLETED("تم التأكيد")
}

enum class PrayerPerformanceMode(val arabicLabel: String, val badgeText: String) {
    JAMAAH("في جماعة", "جماعة"),
    INDIVIDUAL("منفردًا", "منفردًا"),
    UNSPECIFIED("دون تحديد الكيفية", "تم الأداء")
}

data class PrayerDayLog(
    val dateKey: String,
    val prayerType: PrayerType,
    val status: PrayerStatus = PrayerStatus.NOT_RECORDED,
    val performanceMode: PrayerPerformanceMode = PrayerPerformanceMode.UNSPECIFIED,
    val postPrayerAdhkarCompleted: Boolean = false,
    val personalNote: String = "",
    val updatedAtMillis: Long? = null
) {
    val isCompleted: Boolean
        get() = status == PrayerStatus.COMPLETED
}

data class DailyPrayersSummary(
    val dateKey: String,
    val logsByPrayer: Map<PrayerType, PrayerDayLog>
) {
    val orderedLogs: List<PrayerDayLog>
        get() = PrayerType.entries.map { type ->
            logsByPrayer[type] ?: PrayerDayLog(dateKey = dateKey, prayerType = type)
        }

    val completedCount: Int
        get() = orderedLogs.count { it.isCompleted }

    val totalCount: Int
        get() = PrayerType.entries.size

    val calmDescriptiveLabel: String
        get() = when (completedCount) {
            0 -> "سجّل ما تؤديه من صلواتك اليوم بطمأنينة"
            in 1..4 -> "أنت في الطريق.. استمر برفق وسكينة ($completedCount من $totalCount)"
            else -> "تم تسجيل الصلوات الخمس لهذا اليوم.. تقبّل الله طاعتك"
        }
}
