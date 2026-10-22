package com.example.data

enum class MotivationKind(val arabicBadge: String) {
    WELCOME_OPTIMISM("إشراقة وتفاؤل"),
    EFFORTLESS_REWARD("غنيمة بلا تعب"),
    LOVING_GENTLE_NUDGE("همسة محب"),
    CELEBRATION("بشرى وإنجاز"),
    FLASH_REMINDER("تذكير خاطف")
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
     * Ultra-short flash Dhikr notification phrases ("قصيرة جداً خاطفة منتقاة انتقاءً شديداً لا تمل أبداً").
     */
    fun getFlashNotificationPhrases(genderGroup: GenderGroup): List<Pair<String, String>> {
        val fem = isFemale(genderGroup)
        return if (fem) {
            listOf(
                "همسة نور ✨" to "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ.. ثانية واحدة لا تتعبكِ وتملأ ميزانكِ نوراً 🌸",
                "صلِّي على الحبيب ﷺ" to "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ.. صلِّي عليه الآن وأبشري بالسكينة 🌷",
                "غنيمة باردة 🌿" to "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ.. لن تأخذ من وقتكِ شيئاً وتفتح أبواب الخير!",
                "رسالة محبة 💌" to "حتى لو أزعجتكِ بتذكيري.. فأنا أحبكِ ولصالحكِ، لا تملّي مني وقولي: يا رب!",
                "كنز من كنوز الجنة 💎" to "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ.. قوليها الآن بقلب مطمئن 🌸"
            )
        } else {
            listOf(
                "همسة نور ✨" to "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ.. ثانية واحدة لا تتعبك وتملأ ميزانك نوراً 🌿",
                "صلِّ على الحبيب ﷺ" to "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ.. صلِّ عليه الآن وأبشر بالخير 🌟",
                "غنيمة باردة 🌿" to "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ.. لن تأخذ من وقتك شيئاً وتفتح أبواب الرزق!",
                "رسالة محبة 🤝" to "حتى لو أزعجتك بتذكيري.. فأنا أحبك ولصالحك، لا تملّ مني وقل: سبحان الله!",
                "كنز من كنوز الجنة 💎" to "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ.. قلها الآن بقلب مطمئن ✨"
            )
        }
    }

    /**
     * Rich curated library tailored by AgeGroup, GenderGroup, and current daily progress.
     */
    fun getCuratedMessages(
        ageGroup: AgeGroup,
        genderGroup: GenderGroup,
        todaySummary: DailyDaySummary?,
        hourOfDay: Int
    ): List<MotivationalMessage> {
        val fem = isFemale(genderGroup)
        val addressTerm = when {
            ageGroup == AgeGroup.CHILDREN && fem -> "يا أميرتنا الجميلة"
            ageGroup == AgeGroup.CHILDREN && !fem -> "يا بطلنا الرائع"
            ageGroup == AgeGroup.YOUTH && fem -> "يا مبدعة"
            ageGroup == AgeGroup.YOUTH && !fem -> "يا همّة الشباب"
            fem -> "يا غالية"
            else -> "يا طيب القلب"
        }

        val list = mutableListOf<MotivationalMessage>()

        // 1. Effortless Reward ("إثابة بسيطة، لن تتعب، لن يأخذ من وقتك شيء")
        if (fem) {
            list.add(
                MotivationalMessage(
                    id = "effortless_1_f",
                    kind = MotivationKind.EFFORTLESS_REWARD,
                    title = "إثابة عظيمة بلا تعب 🌸",
                    text = "ركعتان خفيفتان أو تسبيحة صادقة $addressTerm.. لن تتعبي، ولن تأخذ من وقتكِ شيئاً، لكنها عند الله عظيمة!",
                    actionHint = "سبحان الله وبحمده"
                )
            )
            list.add(
                MotivationalMessage(
                    id = "effortless_2_f",
                    kind = MotivationKind.EFFORTLESS_REWARD,
                    title = "ألم تصلِّي على النبي ﷺ؟ 🌷",
                    text = "صلِّي عليه الآن مرة واحدة.. لن تأخذ منكِ ثلاث ثوانٍ، ويصلي الله عليكِ بها عشراً!",
                    actionHint = "اللهم صل وسلم على نبينا محمد"
                )
            )
        } else {
            list.add(
                MotivationalMessage(
                    id = "effortless_1_m",
                    kind = MotivationKind.EFFORTLESS_REWARD,
                    title = "إثابة عظيمة بلا تعب 🌿",
                    text = "ركعتان خفيفتان أو تسبيحة صادقة $addressTerm.. لن تتعب، ولن تأخذ من وقتك شيئاً، لكنها عند الله عظيمة!",
                    actionHint = "سبحان الله وبحمده"
                )
            )
            list.add(
                MotivationalMessage(
                    id = "effortless_2_m",
                    kind = MotivationKind.EFFORTLESS_REWARD,
                    title = "ألم تصلِّ على النبي ﷺ؟ ✨",
                    text = "صلِّ عليه الآن مرة واحدة.. لن تأخذ منك ثلاث ثوانٍ، ويصلي الله عليك بها عشراً!",
                    actionHint = "اللهم صل وسلم على نبينا محمد"
                )
            )
        }

        // 2. Context-aware gentle/playful nudge ("عتاب المحب النادر واللطيف - أصلحك الله لم لا تصلي العصر؟")
        val fardItem = todaySummary?.items?.firstOrNull { it.isFivePrayers }
        val asrDone = fardItem?.isPrayerCompleted(FivePrayer.ASR) ?: false
        val dhuhrDone = fardItem?.isPrayerCompleted(FivePrayer.DHUHR) ?: false
        val birrDone = todaySummary?.items?.firstOrNull { it.templateCode == "birr_walidayn" }?.isCompleted ?: false

        if (!asrDone && hourOfDay >= 15) {
            list.add(
                0,
                if (fem) {
                    MotivationalMessage(
                        id = "nudge_asr_f",
                        kind = MotivationKind.LOVING_GENTLE_NUDGE,
                        title = "همسة محبة لا تملّي منها 💗",
                        text = "أصلحكِ الله $addressTerm.. لِمَ لم تسجلي صلاة العصر بعد؟ قومي إليها أو سجليها الآن، فرؤية اسمكِ في سجل المصلين تفرح القلب!"
                    )
                } else {
                    MotivationalMessage(
                        id = "nudge_asr_m",
                        kind = MotivationKind.LOVING_GENTLE_NUDGE,
                        title = "همسة محب ناصح 🤝",
                        text = "أصلحك الله $addressTerm.. لِمَ لا تصلي العصر وتطمئن بها روحك؟ دقائق معدودة لا تتعبك وتملأ يومك بركة!"
                    )
                }
            )
        } else if (!dhuhrDone && hourOfDay in 12..14) {
            list.add(
                0,
                if (fem) {
                    MotivationalMessage(
                        id = "nudge_dhuhr_f",
                        kind = MotivationKind.LOVING_GENTLE_NUDGE,
                        title = "نسيم الظهيرة 🌸",
                        text = "أصلحكِ الله $addressTerm.. صلاة الظهر برد وسلام لقلبكِ وسط انشغالات اليوم، لا تفوّتي أنسها!"
                    )
                } else {
                    MotivationalMessage(
                        id = "nudge_dhuhr_m",
                        kind = MotivationKind.LOVING_GENTLE_NUDGE,
                        title = "استراحة المحارب 🌿",
                        text = "أصلحك الله $addressTerm.. صلاة الظهر استراحة لروحك وسط زحام اليوم، قم إليها بقلب مقبل!"
                    )
                }
            )
        }

        if (!birrDone) {
            list.add(
                if (fem) {
                    MotivationalMessage(
                        id = "nudge_birr_f",
                        kind = MotivationKind.LOVING_GENTLE_NUDGE,
                        title = "دعابة محب 🌷",
                        text = "حتى لو أزعجتكِ بتذكيري فهذا لأني أحبكِ ولصالحكِ.. لا تملّي مني! ابتسامة أو دعوة لوالديكِ الآن تفتح لكِ أبواب السماء."
                    )
                } else {
                    MotivationalMessage(
                        id = "nudge_birr_m",
                        kind = MotivationKind.LOVING_GENTLE_NUDGE,
                        title = "دعابة محب 🌟",
                        text = "حتى لو أزعجتك بتذكيري فهذا لأني أحبك ولصالحك.. لا تملّ مني! كلمة طيبة أو دعوة لوالديك الآن لا تكلفك شيئاً."
                    )
                }
            )
        }

        // 3. Welcoming & Anti-Despair Optimism ("عبارات ترحيبية وعبارات عدم اليأس")
        if (fem) {
            list.add(
                MotivationalMessage(
                    id = "welcome_hope_f",
                    kind = MotivationKind.WELCOME_OPTIMISM,
                    title = "لا يأس مع رحمة الله 🌷",
                    text = "مهما فاتكِ من ورد الأمس، فاليوم صفحة بيضاء نقية! القليل الدائم خير وأحب إلى الله، وأنتِ على خير عظيم."
                )
            )
            list.add(
                MotivationalMessage(
                    id = "playful_counter_f",
                    kind = MotivationKind.EFFORTLESS_REWARD,
                    title = "العداد يشتاق إليكِ ✨",
                    text = "عدّاد التسبيح ينتظر لمستكِ اللطيفة اليوم.. ثلاث تسبيحات الآن لن تأخذ منكِ خمس ثوانٍ!"
                )
            )
        } else {
            list.add(
                MotivationalMessage(
                    id = "welcome_hope_m",
                    kind = MotivationKind.WELCOME_OPTIMISM,
                    title = "لا يأس مع رحمة الله 🌿",
                    text = "مهما فاتك من ورد الأمس، فاليوم صفحة بيضاء جديدة! القليل الدائم خير وأحب إلى الله، وأنت على خير عظيم."
                )
            )
            list.add(
                MotivationalMessage(
                    id = "playful_counter_m",
                    kind = MotivationKind.EFFORTLESS_REWARD,
                    title = "العداد يشتاق إليك ✨",
                    text = "عدّاد التسبيح ينتظر نقرتك المباركة اليوم.. ثلاث تسبيحات الآن لن تأخذ منك خمس ثوانٍ!"
                )
            )
        }

        return list
    }

    /**
     * Joyful celebration text when a user completes a deed or prayer.
     */
    fun getCelebrationPraise(
        deedTitle: String,
        genderGroup: GenderGroup,
        ageGroup: AgeGroup
    ): String {
        val fem = isFemale(genderGroup)
        return when {
            ageGroup == AgeGroup.CHILDREN && fem ->
                "ما شاء الله يا أميرة! نجمة مضيئة في سجلّكِ لتسجيل «$deedTitle» 🌟🌸"
            ageGroup == AgeGroup.CHILDREN && !fem ->
                "أحسنت يا بطل! خطوة رائعة بتسجيل «$deedTitle» 🌟🏆"
            fem ->
                "تقبّل الله منكِ يا غالية! نور على نور بتسجيل «$deedTitle» ✨🌷"
            else ->
                "تقبّل الله طاعتك وأعلى درجتك! خطوة مباركة بتسجيل «$deedTitle» ✨🌿"
        }
    }
}
