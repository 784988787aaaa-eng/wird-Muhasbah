package com.example.domain.model

enum class ContentSourceType(val arabicBadge: String) {
    QURAN_VERSE("آية قرآنية"),
    AUTHENTIC_HADITH("حديث نبوي موثق"),
    GENERAL_DUA("دعاء عام"),
    GENTLE_REFLECTION("رسالة تفاؤل عامة")
}

enum class MessageMomentCategory(val arabicLabel: String) {
    START_OF_DAY("بداية اليوم"),
    GENTLE_ENCOURAGEMENT("تشجيع لطيف"),
    RETURN_AFTER_PAUSE("العودة بعد انقطاع"),
    GRATITUDE("الامتنان"),
    END_OF_DAY("ختام اليوم")
}

enum class MessageToneStyle(val arabicLabel: String, val description: String) {
    CALM("هادئة", "عبارات سكينة وطمأنينة مختصرة"),
    OPTIMISTIC("متفائلة", "رسائل أمل وانشراح للصدر"),
    SUPPLICATION("دعائية عامة", "أدعية طيبة مباركة للقلب واليوم")
}

data class HeartMessage(
    val id: String,
    val title: String,
    val text: String,
    val sourceType: ContentSourceType,
    val documentedAttribution: String,
    val momentCategory: MessageMomentCategory,
    val toneStyle: MessageToneStyle
)
