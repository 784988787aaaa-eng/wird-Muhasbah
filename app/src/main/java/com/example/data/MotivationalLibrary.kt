package com.example.data

enum class MotivationKind(val arabicBadge: String) {
    WELCOME_OPTIMISM("إشراقة"),
    EFFORTLESS_REWARD("سكينة"),
    LOVING_GENTLE_NUDGE("فسحة"),
    CELEBRATION("قبول"),
    FLASH_REMINDER("نور")
}

data class MotivationalMessage(
    val id: String,
    val kind: MotivationKind,
    val title: String,
    val text: String,
    val actionHint: String? = null
)

object MotivationalLibrary {

    fun isFemale(genderGroup: GenderGroup): Boolean {
        return genderGroup in setOf(GenderGroup.GIRLS, GenderGroup.YOUNG_WOMEN, GenderGroup.WOMEN)
    }

    /**
     * Mindful, serene flash notification phrases.
     */
    fun getFlashNotificationPhrases(genderGroup: GenderGroup): List<Pair<String, String>> {
        val fem = isFemale(genderGroup)
        return if (fem) {
            listOf(
                "استراحة روح" to "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ.. خطوة واحدة تُعيد لقلبكِ هدوءه.",
                "طوبى للذاكرين" to "طوبى لمن جعل ليله ونهاره عامراً بذكر ربه.",
                "فسحة وسكينة" to "ما زال في اليوم فسحة، ركعتان تستردين بهما سكينة قلبكِ.",
                "نور على نور" to "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ."
            )
        } else {
            listOf(
                "استراحة روح" to "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ.. خطوة واحدة تُعيد لقلبك هدوءه.",
                "طوبى للذاكرين" to "طوبى لمن جعل ليله ونهاره عامراً بذكر ربه.",
                "فسحة وسكينة" to "ما زال في اليوم فسحة، ركعتان تسترد بهما سكينة قلبك.",
                "نور على نور" to "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ."
            )
        }
    }

    /**
     * [3] Mindful UX Copy: Serene, luminous, concise inspiration phrases.
     */
    fun getCuratedMessages(
        ageGroup: AgeGroup,
        genderGroup: GenderGroup,
        todaySummary: DailyDaySummary?,
        hourOfDay: Int
    ): List<MotivationalMessage> {
        val fem = isFemale(genderGroup)
        val fardItem = todaySummary?.items?.firstOrNull { it.isFivePrayers }
        val completedPrayersCount = fardItem?.completedPrayersCount ?: 0

        val list = mutableListOf<MotivationalMessage>()

        if (completedPrayersCount < 5) {
            list.add(
                MotivationalMessage(
                    id = "mindful_space",
                    kind = MotivationKind.LOVING_GENTLE_NUDGE,
                    title = "فسحة اليوم",
                    text = if (fem) {
                        "ما زال في اليوم فسحة، ركعتان تستردين بهما سكينة قلبكِ."
                    } else {
                        "ما زال في اليوم فسحة، ركعتان تسترد بهما سكينة قلبك."
                    }
                )
            )
        }

        list.add(
            MotivationalMessage(
                id = "mindful_rest",
                kind = MotivationKind.WELCOME_OPTIMISM,
                title = "استراحة روح",
                text = "استراحة روح.. خطوة واحدة تُعيد لقلبك هدوءه."
            )
        )

        list.add(
            MotivationalMessage(
                id = "mindful_tuba",
                kind = MotivationKind.EFFORTLESS_REWARD,
                title = "أنس الذكر",
                text = "طوبى لمن جعل ليله ونهاره عامراً بذكر ربه."
            )
        )

        list.add(
            MotivationalMessage(
                id = "mindful_consistent",
                kind = MotivationKind.WELCOME_OPTIMISM,
                title = "صفاء القلب",
                text = if (fem) {
                    "القليل الدائم خيرٌ وأبقى، لحظة صفاء تبارك يومكِ."
                } else {
                    "القليل الدائم خيرٌ وأبقى، لحظة صفاء تبارك يومك."
                }
            )
        )

        return list
    }

    fun getCelebrationPraise(
        deedTitle: String,
        genderGroup: GenderGroup,
        ageGroup: AgeGroup
    ): String {
        val fem = isFemale(genderGroup)
        return if (fem) {
            "تقبّل الله طاعتكِ • $deedTitle"
        } else {
            "تقبّل الله طاعتك • $deedTitle"
        }
    }
}
