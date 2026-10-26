package com.example.data.local.catalog

import com.example.domain.model.ActivityCategory
import com.example.domain.model.ActivityDefinition
import com.example.domain.model.AuthenticDhikrItem
import com.example.domain.model.ContentSourceType
import com.example.domain.model.HeartMessage
import com.example.domain.model.MessageMomentCategory
import com.example.domain.model.MessageToneStyle

object SeedCatalogs {

    val defaultActivities: List<ActivityDefinition> = listOf(
        ActivityDefinition(
            code = "jamaah_mosque",
            title = "صلاة الجماعة في المسجد",
            subtitle = "لكل خطوة إلى المسجد أجر وسكينة",
            category = ActivityCategory.SOCIAL,
            iconKey = "mosque",
            sortOrder = 1,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "silat_rahim",
            title = "صلة الرحم",
            subtitle = "تزرع المحبة وتزيد البركة في العمر والرزق",
            category = ActivityCategory.FAMILY,
            iconKey = "family_heart",
            sortOrder = 2,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "helping_others",
            title = "مساعدة المحتاجين",
            subtitle = "من أبواب الخير العظيمة وقضاء الحوائج",
            category = ActivityCategory.SOCIAL,
            iconKey = "volunteer",
            sortOrder = 3,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "sadaqah",
            title = "الصدقة",
            subtitle = "تطهر المال وتزيد البركة ولو بالقليل",
            category = ActivityCategory.SOCIAL,
            iconKey = "charity_box",
            sortOrder = 4,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "quran_wird",
            title = "قراءة القرآن",
            subtitle = "نور في القلب والبيت واليوم",
            category = ActivityCategory.QURAN,
            iconKey = "quran_book",
            sortOrder = 5,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "morning_adhkar",
            title = "أذكار الصباح",
            subtitle = "تحصين الصباح وطمأنينة بداية اليوم",
            category = ActivityCategory.ADHKAR,
            iconKey = "morning_sun",
            sortOrder = 6,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "evening_adhkar",
            title = "أذكار المساء",
            subtitle = "سكينة المساء وحفظ الله في ليلك",
            category = ActivityCategory.ADHKAR,
            iconKey = "evening_moon",
            sortOrder = 7,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "birr_walidayn",
            title = "بر الوالدين والدعاء لهما",
            subtitle = "كلمة طيبة أو اتصال أو دعاء صادق",
            category = ActivityCategory.FAMILY,
            iconKey = "family_heart",
            sortOrder = 8,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "duha_or_witr",
            title = "ركعتا الضحى أو صلاة الوتر",
            subtitle = "نافلة يسيرة تقرّب القلب إلى الله",
            category = ActivityCategory.SELF,
            iconKey = "self_leaf",
            sortOrder = 9,
            targetCount = 1
        ),
        ActivityDefinition(
            code = "kind_word",
            title = "الكلمة الطيبة وإدخال السرور",
            subtitle = "الكلمة الطيبة صدقة وأثرها باقٍ",
            category = ActivityCategory.SELF,
            iconKey = "self_leaf",
            sortOrder = 10,
            targetCount = 1
        )
    )

    val authenticAdhkar: List<AuthenticDhikrItem> = listOf(
        AuthenticDhikrItem(
            id = "subhan_allah",
            categoryTitle = "سبحان الله",
            shortText = "سُبْحَانَ اللَّهِ",
            fullArabicText = "«سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ»",
            documentedSource = "رواه مسلم في صحيحه",
            documentedNote = "تُقال دبر الصلوات المكتوبة ثلاثًا وثلاثين، أو مطلقًا في كل وقت.",
            recommendedCount = 33,
            isPostPrayer = true
        ),
        AuthenticDhikrItem(
            id = "morning_adhkar_item",
            categoryTitle = "أذكار الصباح",
            shortText = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ",
            fullArabicText = "«اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ»",
            documentedSource = "رواه الترمذي وأبو داود بسند صحيح",
            documentedNote = "من أذكار الصباح المأثورة، يُقال مرة واحدة في الصباح.",
            recommendedCount = 1,
            isPostPrayer = false
        ),
        AuthenticDhikrItem(
            id = "evening_adhkar_item",
            categoryTitle = "أذكار المساء",
            shortText = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ",
            fullArabicText = "«اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ»",
            documentedSource = "رواه الترمذي وأبو داود بسند صحيح",
            documentedNote = "من أذكار المساء المأثورة، يُقال مرة واحدة في المساء.",
            recommendedCount = 1,
            isPostPrayer = false
        ),
        AuthenticDhikrItem(
            id = "istighfar",
            categoryTitle = "الاستغفار",
            shortText = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            fullArabicText = "«أَسْتَغْفِرُ اللَّهَ الْعَظِيمَ وَأَتُوبُ إِلَيْهِ»",
            documentedSource = "رواه البخاري ومسلم",
            documentedNote = "ثبت في الصحيح الاستغفار في اليوم مائة مرة أو أكثر من سبعين مرة، ويُشرع بلا قيد.",
            recommendedCount = 100,
            isPostPrayer = true
        ),
        AuthenticDhikrItem(
            id = "tasbih_bihamdihi",
            categoryTitle = "سبحان الله وبحمده",
            shortText = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            fullArabicText = "«سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ»",
            documentedSource = "متفق عليه (البخاري ومسلم)",
            documentedNote = "كلمتان خفيفتان على اللسان، ثقيلتان في الميزان، حبيبتان إلى الرحمن.",
            recommendedCount = 100,
            isPostPrayer = false
        ),
        AuthenticDhikrItem(
            id = "salat_alan_nabi",
            categoryTitle = "الصلاة على النبي ﷺ",
            shortText = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
            fullArabicText = "«اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ»",
            documentedSource = "رواه البخاري ومسلم",
            documentedNote = "من صلى على النبي ﷺ صلاة صلى الله عليه بها عشرًا (رواه مسلم).",
            recommendedCount = 10,
            isPostPrayer = false
        ),
        AuthenticDhikrItem(
            id = "sayyid_istighfar",
            categoryTitle = "سيد الاستغفار",
            shortText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ...",
            fullArabicText = "«اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي، فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ»",
            documentedSource = "رواه البخاري في صحيحه",
            documentedNote = "يُقال في الصباح والمساء مرة واحدة بيقين.",
            recommendedCount = 1,
            isPostPrayer = false
        ),
        AuthenticDhikrItem(
            id = "post_prayer_salam",
            categoryTitle = "أذكار بعد السلام من الصلاة",
            shortText = "أَسْتَغْفِرُ اللَّهَ (ثلاثًا)، اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ...",
            fullArabicText = "«أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ.. اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ»",
            documentedSource = "رواه مسلم في صحيحه",
            documentedNote = "كان النبي ﷺ إذا انصرف من صلاته استغفر ثلاثًا وقالها.",
            recommendedCount = 1,
            isPostPrayer = true
        )
    )

    val heartMessages: List<HeartMessage> = listOf(
        HeartMessage(
            id = "msg_close_to_allah",
            title = "رسالة اليوم",
            text = "ما أجمل أن تكون قريبًا من الله في كل تفاصيل يومك، ولو بخطوة هادئة.",
            sourceType = ContentSourceType.GENTLE_REFLECTION,
            documentedAttribution = "خاطرة إيمانية عامة",
            momentCategory = MessageMomentCategory.START_OF_DAY,
            toneStyle = MessageToneStyle.CALM
        ),
        HeartMessage(
            id = "msg_quran_tumaaninah",
            title = "طمأنينة القلب",
            text = "﴿أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ﴾",
            sourceType = ContentSourceType.QURAN_VERSE,
            documentedAttribution = "سورة الرعد — الآية 28",
            momentCategory = MessageMomentCategory.GENTLE_ENCOURAGEMENT,
            toneStyle = MessageToneStyle.CALM
        ),
        HeartMessage(
            id = "msg_quran_tawfiq",
            title = "التوكل والتوفيق",
            text = "﴿وَمَا تَوْفِيقِي إِلَّا بِاللَّهِ ۚ عَلَيْهِ تَوَكَّلْتُ وَإِلَيْهِ أُنِيبُ﴾",
            sourceType = ContentSourceType.QURAN_VERSE,
            documentedAttribution = "سورة هود — الآية 88",
            momentCategory = MessageMomentCategory.START_OF_DAY,
            toneStyle = MessageToneStyle.OPTIMISTIC
        ),
        HeartMessage(
            id = "msg_hadith_consistent",
            title = "اليسر والمداومة",
            text = "«أَحَبُّ الْأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ»",
            sourceType = ContentSourceType.AUTHENTIC_HADITH,
            documentedAttribution = "متفق عليه (البخاري ومسلم)",
            momentCategory = MessageMomentCategory.GENTLE_ENCOURAGEMENT,
            toneStyle = MessageToneStyle.OPTIMISTIC
        ),
        HeartMessage(
            id = "msg_hadith_dua_iana",
            title = "دعاء الإعانة على الذكر",
            text = "«اللَّهُمَّ أَعِنِّي عَلَى ذِكْرِكَ، وَشُكْرِكَ، وَحُسْنِ عِبَادَتِكَ»",
            sourceType = ContentSourceType.AUTHENTIC_HADITH,
            documentedAttribution = "رواه أبو داود والنسائي بسند صحيح",
            momentCategory = MessageMomentCategory.START_OF_DAY,
            toneStyle = MessageToneStyle.SUPPLICATION
        ),
        HeartMessage(
            id = "msg_dua_inshirah",
            title = "دعاء انشراح الصدر",
            text = "اللهم اشرح صدورنا بنور طاعتك، وبارك لنا في أوقاتنا وأعمالنا، واجعل يومنا عامرًا بالسكينة.",
            sourceType = ContentSourceType.GENERAL_DUA,
            documentedAttribution = "دعاء عام غير مقيّد بلفظ حديث",
            momentCategory = MessageMomentCategory.GRATITUDE,
            toneStyle = MessageToneStyle.SUPPLICATION
        ),
        HeartMessage(
            id = "msg_return_kindness",
            title = "باب الخير مفتوح",
            text = "كل يومٍ جديد هو بداية طيبة؛ لا تُثقل قلبك بما مضى، وابدأ بما يتيسّر لك الآن.",
            sourceType = ContentSourceType.GENTLE_REFLECTION,
            documentedAttribution = "رسالة تفاؤل عامة",
            momentCategory = MessageMomentCategory.RETURN_AFTER_PAUSE,
            toneStyle = MessageToneStyle.OPTIMISTIC
        ),
        HeartMessage(
            id = "msg_end_of_day_peace",
            title = "سكينة المساء",
            text = "اللهم تقبّل منا ما قدّمنا من خير في هذا اليوم، واغفر لنا تقصيرنا، واختم يومنا برضاك.",
            sourceType = ContentSourceType.GENERAL_DUA,
            documentedAttribution = "دعاء عام لختام اليوم",
            momentCategory = MessageMomentCategory.END_OF_DAY,
            toneStyle = MessageToneStyle.SUPPLICATION
        )
    )
}
