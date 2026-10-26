package com.example

import com.example.core.common.ArabicDateFormatter
import com.example.data.local.catalog.SeedCatalogs
import com.example.domain.model.ContentSourceType
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `date formatting and gap calculation handle local dates accurately`() {
        val d1 = LocalDate.of(2026, 5, 14)
        val d2 = LocalDate.of(2026, 5, 17)
        assertEquals("2026-05-14", ArabicDateFormatter.toDateKey(d1))
        assertEquals(3L, ArabicDateFormatter.daysBetween("2026-05-14", "2026-05-17"))

        val greg = ArabicDateFormatter.formatGregorianArabic(d2, useEasternArabicDigits = false)
        assertTrue(greg.contains("مايو"))
        assertTrue(greg.contains("17"))

        val hijri = ArabicDateFormatter.formatHijriArabicWithSource(d2, useEasternArabicDigits = false)
        assertNotNull(hijri)
        assertTrue(hijri!!.contains("تقويم أم القرى"))
    }

    @Test
    fun `seed catalog strictly distinguishes Quran, Hadith, and general Duas with documented sources`() {
        val messages = SeedCatalogs.heartMessages
        assertTrue(messages.isNotEmpty())
        messages.forEach { msg ->
            assertTrue(msg.text.isNotBlank())
            assertTrue(msg.documentedAttribution.isNotBlank())
        }
        assertTrue(messages.any { it.sourceType == ContentSourceType.QURAN_VERSE })
        assertTrue(messages.any { it.sourceType == ContentSourceType.AUTHENTIC_HADITH })
        assertTrue(messages.any { it.sourceType == ContentSourceType.GENERAL_DUA })
    }
}
