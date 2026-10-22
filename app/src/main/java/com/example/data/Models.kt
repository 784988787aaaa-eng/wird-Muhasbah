package com.example.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AgeGroup(val arabicLabel: String, val description: String) {
    CHILDREN("الأطفال", "قالب مبسّط يركز على الصلوات والأذكار القصيرة وبر الوالدين"),
    YOUTH("اليافعين", "قالب مشجّع يناسب الناشئة وطلبة المدارس"),
    YOUNG_ADULTS("الشباب", "قالب متوازن يناسب طلاب الجامعات وبداية الحياة العملية"),
    ADULTS("البالغين", "قالب متكامل يناسب الحياة اليومية والأسرية")
}

enum class GenderGroup(val arabicLabel: String) {
    UNSPECIFIED("اختيار عام"),
    BOYS("الفتيان"),
    GIRLS("الفتيات"),
    YOUNG_MEN("الشباب"),
    YOUNG_WOMEN("الشابات"),
    MEN("الرجال"),
    WOMEN("النساء")
}

enum class WirdIntensity(val arabicLabel: String, val badgeText: String, val description: String) {
    LIGHT("ورد خفيف", "٥ أعمال", "يركز على أمهات الأعمال اليومية بيسر وسهولة"),
    BALANCED("ورد متوازن", "٨ أعمال", "توازن يومي بين الفرائض والأذكار والنوافل القريبة"),
    EXPANDED("ورد موسع", "١١ عملاً", "يشمل النوافل الإضافية وقيام الليل وأعمال البر اليومية")
}

enum class DarkModePreference(val arabicLabel: String) {
    SYSTEM("تلقائي"),
    LIGHT("فاتح"),
    DARK("داكن")
}

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val isOnboardingCompleted: Boolean = false,
    val ageGroup: String = AgeGroup.YOUNG_ADULTS.name,
    val genderGroup: String = GenderGroup.UNSPECIFIED.name,
    val intensityLevel: String = WirdIntensity.BALANCED.name,
    val darkModePref: String = DarkModePreference.SYSTEM.name,
    val counterValue: Int = 0,
    val activeDhikrTitle: String = "ذكر مطلق",
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "20:00",
    val lastActiveDateKey: String = ""
)

@Entity(
    tableName = "template_items",
    indices = [Index(value = ["code"], unique = true)]
)
data class TemplateItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val subtitle: String,
    val iconKey: String,
    val isEnabled: Boolean,
    val sortOrder: Int,
    val isCustom: Boolean = false,
    val minIntensity: String = WirdIntensity.LIGHT.name
)

@Entity(tableName = "daily_records")
data class DailyRecordEntity(
    @PrimaryKey val dateKey: String, // Format: YYYY-MM-DD in user's local timezone
    val createdAtMillis: Long,
    val timeZoneId: String,
    val templateSummary: String
)

enum class FivePrayer(val code: String, val arabicName: String) {
    FAJR("FAJR", "الفجر"),
    DHUHR("DHUHR", "الظهر"),
    ASR("ASR", "العصر"),
    MAGHRIB("MAGHRIB", "المغرب"),
    ISHA("ISHA", "العشاء")
}

@Entity(
    tableName = "daily_record_items",
    indices = [
        Index(value = ["dateKey"]),
        Index(value = ["dateKey", "templateCode"], unique = true)
    ]
)
data class DailyRecordItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateKey: String,
    val templateCode: String,
    val title: String,
    val subtitle: String,
    val iconKey: String,
    val sortOrder: Int,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val subItemsMask: String = ""
) {
    val isFivePrayers: Boolean
        get() = templateCode == "fard_prayers"

    val completedPrayers: Set<FivePrayer>
        get() {
            if (!isFivePrayers) return emptySet()
            if (subItemsMask.isBlank()) {
                return if (isCompleted) FivePrayer.entries.toSet() else emptySet()
            }
            val codes = subItemsMask.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            return FivePrayer.entries.filter { it.code in codes }.toSet()
        }

    val completedPrayersCount: Int
        get() = completedPrayers.size

    fun isPrayerCompleted(prayer: FivePrayer): Boolean = prayer in completedPrayers
}

data class DailyDaySummary(
    val record: DailyRecordEntity,
    val items: List<DailyRecordItemEntity>
) {
    val totalCount: Int get() = items.size
    val completedCount: Int get() = items.count { it.isCompleted }
    val progressFraction: Float
        get() = if (totalCount == 0) 0f else {
            val sumUnits = items.sumOf { item ->
                if (item.isFivePrayers) {
                    item.completedPrayersCount.toDouble() / FivePrayer.entries.size.toDouble()
                } else {
                    if (item.isCompleted) 1.0 else 0.0
                }
            }.toFloat()
            (sumUnits / totalCount.toFloat()).coerceIn(0f, 1f)
        }
    val progressPercent: Int
        get() = (progressFraction * 100f).toInt()
}

data class SuggestedDhikr(
    val id: String,
    val categoryTitle: String,
    val shortPreview: String,
    val fullArabicText: String,
    val documentedSource: String,
    val documentedCountNote: String,
    val suggestedTargetCount: Int? = null
)

object AuthenticAdhkarCatalog {
    val allSuggestions: List<SuggestedDhikr> = listOf(
        SuggestedDhikr(
            id = "istighfar",
            categoryTitle = "الاستغفار",
            shortPreview = "أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ",
            fullArabicText = "«أَسْتَغْفِرُ اللَّهَ وَأَتُوبُ إِلَيْهِ»",
            documentedSource = "رواه البخاري ومسلم",
            documentedCountNote = "مشروع في كل وقت بلا قيد، وثبت في الصحيح الاستغفار في اليوم مائة مرة أو أكثر من سبعين مرة.",
            suggestedTargetCount = 100
        ),
        SuggestedDhikr(
            id = "tasbih",
            categoryTitle = "التسبيح والتحميد",
            shortPreview = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            fullArabicText = "«سُبْحَانَ اللَّهِ وَبِحَمْدِهِ»\n«سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ»",
            documentedSource = "متفق عليه (البخاري ومسلم)",
            documentedCountNote = "ثبت في الصحيحين قول: «سُبْحَانَ اللَّهِ وَبِحَمْدِهِ» في يوم مائة مرة، كما يُشرع مطلقاً دون تقييد بعدد.",
            suggestedTargetCount = 100
        ),
        SuggestedDhikr(
            id = "salat_alan_nabi",
            categoryTitle = "الصلاة على النبي ﷺ",
            shortPreview = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
            fullArabicText = "«اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ وَعَلَى آلِ مُحَمَّدٍ، كَمَا صَلَّيْتَ عَلَى إِبْرَاهِيمَ وَعَلَى آلِ إِبْرَاهِيمَ، إِنَّكَ حَمِيدٌ مَجِيدٌ»",
            documentedSource = "رواه البخاري ومسلم (ومن صلى عليّ صلاة صلى الله عليه بها عشراً - رواه مسلم)",
            documentedCountNote = "مشروعة في كل وقت بغير تقييد بعدد معين، ويُستحب الإكثار منها.",
            suggestedTargetCount = null
        ),
        SuggestedDhikr(
            id = "tahmid_takbir",
            categoryTitle = "التسبيح والتحميد والتكبير",
            shortPreview = "سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ",
            fullArabicText = "«سُبْحَانَ اللَّهِ، وَالْحَمْدُ لِلَّهِ، وَلَا إِلَهَ إِلَّا اللَّهُ، وَاللَّهُ أَكْبَرُ»",
            documentedSource = "رواه مسلم في صحيحه",
            documentedCountNote = "من أحب الكلام إلى الله، ويُذكر مطلقاً في كل الأوقات، أو ثلاثاً وثلاثين دبر الصلوات المكتوبة.",
            suggestedTargetCount = 33
        ),
        SuggestedDhikr(
            id = "morning_evening",
            categoryTitle = "من أذكار الصباح والمساء",
            shortPreview = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ",
            fullArabicText = "في الصباح: «اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ النُّشُورُ»\nوفي المساء: «اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ وَإِلَيْكَ الْمَصِيرُ»",
            documentedSource = "رواه الترمذي وأبو داود بسند صحيح",
            documentedCountNote = "يُقال مرة واحدة عند الصباح ومرة واحدة عند المساء.",
            suggestedTargetCount = 1
        ),
        SuggestedDhikr(
            id = "sayyid_istighfar",
            categoryTitle = "سيد الاستغفار",
            shortPreview = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ...",
            fullArabicText = "«اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي، فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ»",
            documentedSource = "رواه البخاري في صحيحه",
            documentedCountNote = "ثبت في صحيح البخاري قوله موقناً به حين يمسي وحين يصبح (مرة واحدة).",
            suggestedTargetCount = 1
        )
    )
}

object DefaultTemplateCatalog {
    fun buildDefaultItems(
        ageGroup: AgeGroup = AgeGroup.YOUNG_ADULTS,
        genderGroup: GenderGroup = GenderGroup.UNSPECIFIED,
        intensity: WirdIntensity = WirdIntensity.BALANCED
    ): List<TemplateItemEntity> {
        val jamaahSubtitle = when (genderGroup) {
            GenderGroup.GIRLS, GenderGroup.YOUNG_WOMEN, GenderGroup.WOMEN ->
                "أداء الصلاة في أول وقتها أو في جماعة"
            else ->
                "الحرص على الصلاة في جماعة أو في أول الوقت"
        }

        val baseItems = listOf(
            // LIGHT tier (5 core items)
            TemplateItemEntity(
                code = "fard_prayers",
                title = "الصلوات الخمس",
                subtitle = "الفجر، الظهر، العصر، المغرب، العشاء",
                iconKey = "mosque",
                isEnabled = true,
                sortOrder = 1,
                minIntensity = WirdIntensity.LIGHT.name
            ),
            TemplateItemEntity(
                code = "morning_adhkar",
                title = "أذكار الصباح",
                subtitle = "تحصين الصباح المأثور بعد الفجر",
                iconKey = "wb_sunny",
                isEnabled = true,
                sortOrder = 2,
                minIntensity = WirdIntensity.LIGHT.name
            ),
            TemplateItemEntity(
                code = "evening_adhkar",
                title = "أذكار المساء",
                subtitle = "تحصين المساء المأثور بعد العصر",
                iconKey = "nights_stay",
                isEnabled = true,
                sortOrder = 3,
                minIntensity = WirdIntensity.LIGHT.name
            ),
            TemplateItemEntity(
                code = "istighfar",
                title = "الاستغفار",
                subtitle = "أستغفر الله وأتوب إليه",
                iconKey = "spa",
                isEnabled = true,
                sortOrder = 4,
                minIntensity = WirdIntensity.LIGHT.name
            ),
            TemplateItemEntity(
                code = "birr_walidayn",
                title = "بر الوالدين",
                subtitle = "إحسان، تواصل، أو دعاء للوالدين",
                iconKey = "favorite",
                isEnabled = true,
                sortOrder = 5,
                minIntensity = WirdIntensity.LIGHT.name
            ),
            // BALANCED tier (+3 items = 8 items)
            TemplateItemEntity(
                code = "salat_alan_nabi",
                title = "الصلاة على النبي ﷺ",
                subtitle = "اللهم صل وسلم على نبينا محمد",
                iconKey = "auto_awesome",
                isEnabled = intensity != WirdIntensity.LIGHT,
                sortOrder = 6,
                minIntensity = WirdIntensity.BALANCED.name
            ),
            TemplateItemEntity(
                code = "jamaah_prayer",
                title = "متابعة الصلاة في جماعة",
                subtitle = jamaahSubtitle,
                iconKey = "groups",
                isEnabled = intensity != WirdIntensity.LIGHT && ageGroup != AgeGroup.CHILDREN,
                sortOrder = 7,
                minIntensity = WirdIntensity.BALANCED.name
            ),
            TemplateItemEntity(
                code = "witr_prayer",
                title = "صلاة الوتر",
                subtitle = "ختام صلاة الليل ولو بركعة",
                iconKey = "bedtime",
                isEnabled = intensity != WirdIntensity.LIGHT,
                sortOrder = 8,
                minIntensity = WirdIntensity.BALANCED.name
            ),
            // EXPANDED tier (+3 items = 11 items)
            TemplateItemEntity(
                code = "duha_prayer",
                title = "صلاة الضحى",
                subtitle = "صلاة الأوابين بعد ارتفاع الشمس",
                iconKey = "light_mode",
                isEnabled = intensity == WirdIntensity.EXPANDED ||
                    (intensity == WirdIntensity.BALANCED && ageGroup == AgeGroup.CHILDREN),
                sortOrder = 9,
                minIntensity = WirdIntensity.EXPANDED.name
            ),
            TemplateItemEntity(
                code = "sadaqah_khayr",
                title = "الصدقة أو عمل خير",
                subtitle = "صدقة، كلمة طيبة، أو نفع للناس",
                iconKey = "volunteer_activism",
                isEnabled = intensity == WirdIntensity.EXPANDED,
                sortOrder = 10,
                minIntensity = WirdIntensity.EXPANDED.name
            ),
            TemplateItemEntity(
                code = "qiyam_layl",
                title = "قيام الليل",
                subtitle = "ركعات في جوف الليل",
                iconKey = "dark_mode",
                isEnabled = intensity == WirdIntensity.EXPANDED && ageGroup != AgeGroup.CHILDREN,
                sortOrder = 11,
                minIntensity = WirdIntensity.EXPANDED.name
            )
        )
        return baseItems
    }
}
