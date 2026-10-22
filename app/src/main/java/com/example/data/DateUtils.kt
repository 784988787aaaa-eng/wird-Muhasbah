package com.example.data

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import kotlin.math.floor

interface ClockProvider {
    fun currentLocalDate(): LocalDate
    fun currentTimeMillis(): Long
    fun currentZoneId(): ZoneId
    fun currentHourOfDay(): Int
}

object SystemClockProvider : ClockProvider {
    override fun currentLocalDate(): LocalDate = LocalDate.now()
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
    override fun currentZoneId(): ZoneId = ZoneId.systemDefault()
    override fun currentHourOfDay(): Int = LocalTime.now().hour
}

object ArabicDateUtils {

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

    fun toDateKey(date: LocalDate): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun parseDateKey(dateKey: String): LocalDate {
        return try {
            LocalDate.parse(dateKey, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    fun formatGregorianArabic(date: LocalDate): String {
        val dayName = arabicDaysOfWeek[date.dayOfWeek.value] ?: ""
        val day = toArabicDigits(date.dayOfMonth)
        val month = gregorianMonths.getOrElse(date.monthValue - 1) { "" }
        val year = toArabicDigits(date.year)
        return "$dayName، $day $month $year م"
    }

    /**
     * Uses Java Time's Umm al-Qura HijrahChronology when available on Android runtime,
     * with a standard Julian-Day Islamic calendar calculation fallback for headless JVM environments.
     */
    fun formatHijriArabic(date: LocalDate): String? {
        return try {
            val hijriDate: HijrahDate = HijrahChronology.INSTANCE.date(date)
            val day = toArabicDigits(hijriDate.get(ChronoField.DAY_OF_MONTH))
            val monthIndex = hijriDate.get(ChronoField.MONTH_OF_YEAR) - 1
            val monthName = hijriMonths.getOrElse(monthIndex) { "" }
            val year = toArabicDigits(hijriDate.get(ChronoField.YEAR))
            if (monthName.isNotEmpty()) {
                "$day $monthName $year هـ"
            } else {
                computeTabularHijriArabic(date)
            }
        } catch (e: Exception) {
            computeTabularHijriArabic(date)
        }
    }

    private fun computeTabularHijriArabic(date: LocalDate): String? {
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
            "${toArabicDigits(id)} $monthName ${toArabicDigits(iy)} هـ"
        } catch (e: Exception) {
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

    fun toArabicDigits(number: Int): String {
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return number.toString().map { ch ->
            if (ch in '0'..'9') arabicDigits[ch - '0'] else ch
        }.joinToString("")
    }
}
