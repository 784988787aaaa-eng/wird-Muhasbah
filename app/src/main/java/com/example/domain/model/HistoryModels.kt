package com.example.domain.model

enum class HistoryPeriodFilter(val arabicLabel: String) {
    DAY("يوم"),
    WEEK("أسبوع"),
    MONTH("شهر")
}

data class DayHistorySummary(
    val dateKey: String,
    val prayersCompleted: Int,
    val prayersTotal: Int = 5,
    val goodDeedsCompleted: Int,
    val goodDeedsTotal: Int,
    val adhkarCompleted: Int,
    val adhkarTotal: Int,
    val prayerLogs: List<PrayerDayLog> = emptyList(),
    val activityRecords: List<DailyActivityRecord> = emptyList()
) {
    val hasAnyRecordedAction: Boolean
        get() = prayersCompleted > 0 || goodDeedsCompleted > 0 || adhkarCompleted > 0
}

data class JourneyWeeklyGlimpse(
    val activeDaysInWeek: Int,
    val totalPrayersRecordedInWeek: Int,
    val totalDeedsRecordedInWeek: Int,
    val descriptiveSummaryText: String
)
