package com.example.core.common

import java.time.LocalDate
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import kotlin.math.floor

object ArabicDateFormatter {

    private val hijriMonths = arrayOf(
        "محرّم",
        "صفر",
        "ربيع الأول",
        "ربيع الآخر",
        "جمادى الأولى",
        "جمادى الآخرة",
        "رجب",
        "شعبان",
        "رمضان",
        "شوّال",
        "ذو القعدة",
        "ذو الحجة"
    )

    private val gregorianMonths = arrayOf(
        "يناير",
        "فبراير",
        "مارس",
        "أبريل",
        "مايو",
        "يونيو",
        "يوليو",
        "أغسطس",
        "سبتمبر",
        "أكتوبر",
        "نوفمبر",
        "ديسمبر"
    )

    private val arabicDaysOfWeek = mapOf(
        1 to "الإثنين",
        2 to "الثلاثاء",
        3 to "الأربعاء",
        4 to "الخميس",
        5 to "الجمعة",
        6 to "السبت",
        7 to "الأحد"
    )

    private val shortArabicDaysOfWeek = mapOf(
        1 to "إثنين",
        2 to "ثلاثاء",
        3 to "أربعاء",
        4 to "خميس",
        5 to "جمعة",
        6 to "سبت",
        7 to "أحد"
    )

    fun toDateKey(date: LocalDate): String = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

    fun parseDateKey(dateKey: String, fallback: LocalDate = LocalDate.now()): LocalDate {
        return try {
            LocalDate.parse(dateKey, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (_: Exception) {
            fallback
        }
    }

    fun daysBetween(startDateKey: String, endDateKey: String): Long {
        if (startDateKey.isBlank() || endDateKey.isBlank()) return 0L
        return try {
            val start = LocalDate.parse(startDateKey, DateTimeFormatter.ISO_LOCAL_DATE)
            val end = LocalDate.parse(endDateKey, DateTimeFormatter.ISO_LOCAL_DATE)
            ChronoUnit.DAYS.between(start, end)
        } catch (_: Exception) {
            0L
        }
    }

    fun formatGregorianArabic(date: LocalDate, useEasternArabicDigits: Boolean = false): String {
        val dayName = arabicDaysOfWeek[date.dayOfWeek.value] ?: ""
        val day = formatNumber(date.dayOfMonth, useEasternArabicDigits)
        val month = gregorianMonths.getOrElse(date.monthValue - 1) { "" }
        val year = formatNumber(date.year, useEasternArabicDigits)
        return "$dayName $day $month $year"
    }

    fun formatMonthNameArabic(date: LocalDate): String {
        return gregorianMonths.getOrElse(date.monthValue - 1) { "" }
    }

    fun formatShortDayNameArabic(date: LocalDate): String {
        return shortArabicDaysOfWeek[date.dayOfWeek.value] ?: ""
    }

    fun formatHijriArabicWithSource(date: LocalDate, useEasternArabicDigits: Boolean = false): String? {
        val raw = formatHijriArabic(date, useEasternArabicDigits) ?: return null
        return "$raw (تقويم أم القرى)"
    }

    fun formatHijriArabic(date: LocalDate, useEasternArabicDigits: Boolean = false): String? {
        return try {
            val hijriDate: HijrahDate = HijrahChronology.INSTANCE.date(date)
            val day = formatNumber(hijriDate.get(ChronoField.DAY_OF_MONTH), useEasternArabicDigits)
            val monthIndex = hijriDate.get(ChronoField.MONTH_OF_YEAR) - 1
            val monthName = hijriMonths.getOrElse(monthIndex) { "" }
            val year = formatNumber(hijriDate.get(ChronoField.YEAR), useEasternArabicDigits)
            if (monthName.isNotEmpty()) {
                "$day $monthName $year هـ"
            } else {
                computeTabularHijriArabic(date, useEasternArabicDigits)
            }
        } catch (_: Exception) {
            computeTabularHijriArabic(date, useEasternArabicDigits)
        }
    }

    private fun computeTabularHijriArabic(date: LocalDate, useEasternArabicDigits: Boolean): String? {
        return try {
            var y = date.year.toDouble()
            var m = date.monthValue.toDouble()
            val d = date.dayOfMonth.toDouble()
            if (m < 3.0) {
                y -= 1.0
                m += 12.0
            }
            val a = floor(y / 100.0)
            val b = 2.0 - a + floor(a / 4.0)
            val jd = floor(365.25 * (y + 4716.0)) + floor(30.6001 * (m + 1.0)) + d + b - 1524.0

            val iYearCycle = 10631.0 / 30.0
            val epochAstro = 1948084.0
            val shift1 = 8.01 / 60.0

            var z = jd - epochAstro
            val cyc = floor(z / 10631.0)
            z -= 10631.0 * cyc
            val j = floor((z - shift1) / iYearCycle)
            val iy = (30.0 * cyc + j).toInt()
            z -= floor(j * iYearCycle + shift1)
            var im = floor((z + 28.5001) / 29.5).toInt()
            if (im == 13) im = 12
            val id = (z - floor(29.5001 * im - 29.0)).toInt().coerceIn(1, 30)

            val monthName = hijriMonths.getOrElse(im - 1) { hijriMonths[0] }
            "${formatNumber(id, useEasternArabicDigits)} $monthName ${formatNumber(iy, useEasternArabicDigits)} هـ"
        } catch (_: Exception) {
            null
        }
    }

    fun greetingForHour(hour: Int): String {
        return when (hour) {
            in 4..11 -> "صباح السكينة والبركة"
            in 12..16 -> "طاب يومك بذكر الله"
            else -> "مساء الطمأنينة والقبول"
        }
    }

    fun recentDateWindow(anchorDate: LocalDate, count: Int = 7): List<LocalDate> {
        return (count - 1 downTo 0).map { offset -> anchorDate.minusDays(offset.toLong()) }
    }

    fun formatNumber(number: Int, useEasternArabicDigits: Boolean = false): String {
        if (!useEasternArabicDigits) return number.toString()
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return number.toString().map { ch ->
            if (ch in '0'..'9') arabicDigits[ch - '0'] else ch
        }.joinToString("")
    }
}
