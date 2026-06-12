package com.example.data

import android.graphics.PointF

enum class LessonType {
    HIRAGANA,
    KATAKANA,
    KANJI
}

data class StrokePath(
    val points: List<PointF>
)

data class LessonCharacter(
    val id: String,
    val char: String,
    val romaji: String,
    val nepaliPronunciation: String,
    val type: LessonType,
    val strokes: List<StrokePath> = emptyList(),
    val meaning: String = "",
    val nepaliMeaning: String = "",
    val strokeCount: Int = 0,
    val exampleWord: String = "",
    val exampleWordRomaji: String = "",
    val exampleWordNepali: String = ""
)

data class VocabularyWord(
    val jp: String,
    val romaji: String,
    val nepaliText: String,
    val type: String, // e.g., "Greetings", "Common Essentials", "Basics"
    val pronunciationNepali: String
)

data class SentenceData(
    val jp: String,
    val romaji: String,
    val nepali: String,
    val category: String
)

object JapaneseData {
    
    // Help helper function to create relative strokes easily
    private fun createStroke(vararg coords: Pair<Float, Float>): StrokePath {
        return StrokePath(coords.map { PointF(it.first, it.second) })
    }

    val hiraganaList = listOf(
        LessonCharacter(
            id = "h_a", char = "あ", romaji = "a", nepaliPronunciation = "अ",
            type = LessonType.HIRAGANA,
            exampleWord = "あさ (asa)", exampleWordRomaji = "Morning", exampleWordNepali = "बिहान",
            strokes = listOf(
                createStroke(0.2f to 0.4f, 0.8f to 0.4f),
                createStroke(0.5f to 0.15f, 0.5f to 0.8f),
                createStroke(0.4f to 0.45f, 0.3f to 0.6f, 0.45f to 0.75f, 0.7f to 0.6f, 0.65f to 0.42f, 0.48f to 0.42f)
            )
        ),
        LessonCharacter(
            id = "h_i", char = "い", romaji = "i", nepaliPronunciation = "इ",
            type = LessonType.HIRAGANA,
            exampleWord = "いぬ (inu)", exampleWordRomaji = "Dog", exampleWordNepali = "कुकुर",
            strokes = listOf(
                createStroke(0.3f to 0.25f, 0.25f to 0.5f, 0.28f to 0.75f, 0.38f to 0.68f),
                createStroke(0.7f to 0.3f, 0.72f to 0.55f)
            )
        ),
        LessonCharacter(
            id = "h_u", char = "う", romaji = "u", nepaliPronunciation = "उ",
            type = LessonType.HIRAGANA,
            exampleWord = "うさぎ (usagi)", exampleWordRomaji = "Rabbit", exampleWordNepali = "खरायो",
            strokes = listOf(
                createStroke(0.42f to 0.2f, 0.58f to 0.25f),
                createStroke(0.35f to 0.4f, 0.65f to 0.45f, 0.6f to 0.72f, 0.35f to 0.82f)
            )
        ),
        LessonCharacter(
            id = "h_e", char = "え", romaji = "e", nepaliPronunciation = "ए",
            type = LessonType.HIRAGANA,
            exampleWord = "えき (eki)", exampleWordRomaji = "Station", exampleWordNepali = "स्टेशन",
            strokes = listOf(
                createStroke(0.45f to 0.2f, 0.55f to 0.25f),
                createStroke(0.3f to 0.45f, 0.7f to 0.45f, 0.35f to 0.75f, 0.5f to 0.75f, 0.65f to 0.65f, 0.75f to 0.78f)
            )
        ),
        LessonCharacter(
            id = "h_o", char = "お", romaji = "o", nepaliPronunciation = "ओ",
            type = LessonType.HIRAGANA,
            exampleWord = "おちゃ (ocha)", exampleWordRomaji = "Green tea", exampleWordNepali = "हरियो चिया",
            strokes = listOf(
                createStroke(0.2f to 0.4f, 0.75f to 0.4f),
                createStroke(0.48f to 0.15f, 0.48f to 0.6f, 0.38f to 0.75f, 0.55f to 0.8f, 0.7f to 0.65f, 0.68f to 0.45f, 0.48f to 0.45f),
                createStroke(0.72f to 0.3f, 0.78f to 0.38f)
            )
        ),
        LessonCharacter(
            id = "h_ka", char = "か", romaji = "ka", nepaliPronunciation = "का",
            type = LessonType.HIRAGANA,
            exampleWord = "かさ (kasa)", exampleWordRomaji = "Umbrella", exampleWordNepali = "छाता",
            strokes = listOf(
                createStroke(0.3f to 0.35f, 0.65f to 0.35f, 0.68f to 0.55f, 0.52f to 0.75f),
                createStroke(0.48f to 0.22f, 0.35f to 0.78f),
                createStroke(0.72f to 0.28f, 0.8f to 0.42f)
            )
        ),
        LessonCharacter(
            id = "h_ki", char = "き", romaji = "ki", nepaliPronunciation = "कि",
            type = LessonType.HIRAGANA,
            exampleWord = "きつね (kitsune)", exampleWordRomaji = "Fox", exampleWordNepali = "फ्याउरो",
            strokes = listOf(
                createStroke(0.3f to 0.32f, 0.7f to 0.32f),
                createStroke(0.32f to 0.44f, 0.68f to 0.44f),
                createStroke(0.5f to 0.18f, 0.45f to 0.72f),
                createStroke(0.35f to 0.76f, 0.55f to 0.78f)
            )
        ),
        LessonCharacter(
            id = "h_ku", char = "く", romaji = "ku", nepaliPronunciation = "कु",
            type = LessonType.HIRAGANA,
            exampleWord = "くるま (kuruma)", exampleWordRomaji = "Car", exampleWordNepali = "गाडी / कार",
            strokes = listOf(
                createStroke(0.65f to 0.3f, 0.3f to 0.5f, 0.65f to 0.7f)
            )
        ),
        LessonCharacter(
            id = "h_ke", char = "け", romaji = "ke", nepaliPronunciation = "के",
            type = LessonType.HIRAGANA,
            exampleWord = "けん (ken)", exampleWordRomaji = "Sword", exampleWordNepali = "तरवार",
            strokes = listOf(
                createStroke(0.3f to 0.25f, 0.28f to 0.75f, 0.35f to 0.68f),
                createStroke(0.48f to 0.38f, 0.68f to 0.38f),
                createStroke(0.58f to 0.2f, 0.58f to 0.8f)
            )
        ),
        LessonCharacter(
            id = "h_ko", char = "こ", romaji = "ko", nepaliPronunciation = "को",
            type = LessonType.HIRAGANA,
            exampleWord = "こころ (kokoro)", exampleWordRomaji = "Heart", exampleWordNepali = "मन / मुटु",
            strokes = listOf(
                createStroke(0.3f to 0.35f, 0.7f to 0.35f, 0.65f to 0.45f),
                createStroke(0.32f to 0.75f, 0.68f to 0.7f)
            )
        )
    )

    val katakanaList = listOf(
        LessonCharacter(
            id = "k_a", char = "ア", romaji = "a", nepaliPronunciation = "अ",
            type = LessonType.KATAKANA,
            exampleWord = "アイス (aisu)", exampleWordRomaji = "Ice cream", exampleWordNepali = "आइसक्रिम",
            strokes = listOf(
                createStroke(0.25f to 0.35f, 0.75f to 0.35f, 0.65f to 0.55f),
                createStroke(0.48f to 0.48f, 0.32f to 0.78f)
            )
        ),
        LessonCharacter(
            id = "k_i", char = "イ", romaji = "i", nepaliPronunciation = "इ",
            type = LessonType.KATAKANA,
            exampleWord = "インク (inku)", exampleWordRomaji = "Ink", exampleWordNepali = "मसी",
            strokes = listOf(
                createStroke(0.65f to 0.25f, 0.35f to 0.58f),
                createStroke(0.5f to 0.46f, 0.5f to 0.85f)
            )
        ),
        LessonCharacter(
            id = "k_u", char = "ウ", romaji = "u", nepaliPronunciation = "उ",
            type = LessonType.KATAKANA,
            exampleWord = "ウサギ (usagi)", exampleWordRomaji = "Rabbit", exampleWordNepali = "खरायो",
            strokes = listOf(
                createStroke(0.5f to 0.15f, 0.5f to 0.28f),
                createStroke(0.32f to 0.4f, 0.32f to 0.52f),
                createStroke(0.32f to 0.4f, 0.72f to 0.4f, 0.62f to 0.75f)
            )
        ),
        LessonCharacter(
            id = "k_e", char = "エ", romaji = "e", nepaliPronunciation = "ए",
            type = LessonType.KATAKANA,
            exampleWord = "エアコン (eakon)", exampleWordRomaji = "AC", exampleWordNepali = "रुपता अनुकूलक (एसी)",
            strokes = listOf(
                createStroke(0.3f to 0.28f, 0.7f to 0.28f),
                createStroke(0.5f to 0.28f, 0.5f to 0.72f),
                createStroke(0.2f to 0.72f, 0.8f to 0.72f)
            )
        ),
        LessonCharacter(
            id = "k_o", char = "オ", romaji = "o", nepaliPronunciation = "ओ",
            type = LessonType.KATAKANA,
            exampleWord = "オレンジ (orenji)", exampleWordRomaji = "Orange", exampleWordNepali = "सुन्तला",
            strokes = listOf(
                createStroke(0.25f to 0.38f, 0.75f to 0.38f),
                createStroke(0.48f to 0.22f, 0.48f to 0.78f, 0.35f to 0.65f),
                createStroke(0.51f to 0.42f, 0.72f to 0.68f)
            )
        ),
        LessonCharacter(
            id = "k_ka", char = "カ", romaji = "ka", nepaliPronunciation = "का",
            type = LessonType.KATAKANA,
            exampleWord = "カメラ (kamera)", exampleWordRomaji = "Camera", exampleWordNepali = "क्यामेरा",
            strokes = listOf(
                createStroke(0.25f to 0.36f, 0.72f to 0.36f, 0.62f to 0.5f),
                createStroke(0.48f to 0.22f, 0.32f to 0.78f)
            )
        ),
        LessonCharacter(
            id = "k_ki", char = "キ", romaji = "ki", nepaliPronunciation = "कि",
            type = LessonType.KATAKANA,
            exampleWord = "キー (kii)", exampleWordRomaji = "Key", exampleWordNepali = "चाबी",
            strokes = listOf(
                createStroke(0.28f to 0.35f, 0.72f to 0.35f),
                createStroke(0.25f to 0.48f, 0.75f to 0.48f),
                createStroke(0.48f to 0.18f, 0.32f to 0.82f)
            )
        ),
        LessonCharacter(
            id = "k_ku", char = "ク", romaji = "ku", nepaliPronunciation = "कु",
            type = LessonType.KATAKANA,
            exampleWord = "クラス (kurasu)", exampleWordRomaji = "Class", exampleWordNepali = "कक्षा",
            strokes = listOf(
                createStroke(0.55f to 0.28f, 0.35f to 0.48f),
                createStroke(0.35f to 0.48f, 0.78f to 0.48f, 0.55f to 0.82f)
            )
        ),
        LessonCharacter(
            id = "k_ke", char = "ケ", romaji = "ke", nepaliPronunciation = "के",
            type = LessonType.KATAKANA,
            exampleWord = "ケーキ (keeki)", exampleWordRomaji = "Cake", exampleWordNepali = "केक",
            strokes = listOf(
                createStroke(0.55f to 0.28f, 0.35f to 0.48f),
                createStroke(0.28f to 0.48f, 0.78f to 0.48f),
                createStroke(0.52f to 0.48f, 0.38f to 0.82f)
            )
        ),
        LessonCharacter(
            id = "k_ko", char = "コ", romaji = "ko", nepaliPronunciation = "को",
            type = LessonType.KATAKANA,
            exampleWord = "コップ (koppu)", exampleWordRomaji = "Cup", exampleWordNepali = "कप (चिया खाने)",
            strokes = listOf(
                createStroke(0.28f to 0.35f, 0.72f to 0.35f, 0.72f to 0.72f),
                createStroke(0.28f to 0.72f, 0.75f to 0.72f)
            )
        )
    )

    val kanjiList = listOf(
        LessonCharacter(
            id = "kn_ichi", char = "一", romaji = "ichi", nepaliPronunciation = "इची",
            type = LessonType.KANJI, meaning = "One", nepaliMeaning = "एक (१)", strokeCount = 1,
            exampleWord = "一つ (hitotsu)", exampleWordRomaji = "One thing", exampleWordNepali = "एउटा थोक",
            strokes = listOf(
                createStroke(0.2f to 0.5f, 0.8f to 0.5f)
            )
        ),
        LessonCharacter(
            id = "kn_ni", char = "二", romaji = "ni", nepaliPronunciation = "नी",
            type = LessonType.KANJI, meaning = "Two", nepaliMeaning = "दुई (२)", strokeCount = 2,
            exampleWord = "二日 (futsu-ka)", exampleWordRomaji = "2nd day", exampleWordNepali = "दोस्रो दिन",
            strokes = listOf(
                createStroke(0.28f to 0.35f, 0.72f to 0.35f),
                createStroke(0.18f to 0.65f, 0.82f to 0.65f)
            )
        ),
        LessonCharacter(
            id = "kn_san", char = "三", romaji = "san", nepaliPronunciation = "सान",
            type = LessonType.KANJI, meaning = "Three", nepaliMeaning = "तीन (३)", strokeCount = 3,
            exampleWord = "三日 (mikka)", exampleWordRomaji = "3rd day", exampleWordNepali = "तेस्रो दिन",
            strokes = listOf(
                createStroke(0.28f to 0.28f, 0.72f to 0.28f),
                createStroke(0.32f to 0.48f, 0.68f to 0.48f),
                createStroke(0.18f to 0.68f, 0.82f to 0.68f)
            )
        ),
        LessonCharacter(
            id = "kn_hito", char = "人", romaji = "hito / jin", nepaliPronunciation = "हितो / जीन",
            type = LessonType.KANJI, meaning = "Person", nepaliMeaning = "मानिस", strokeCount = 2,
            exampleWord = "日本人 (Nihon-jin)", exampleWordRomaji = "Japanese person", exampleWordNepali = "जापानी नागरिक",
            strokes = listOf(
                createStroke(0.5f to 0.18f, 0.22f to 0.78f),
                createStroke(0.48f to 0.45f, 0.78f to 0.78f)
            )
        ),
        LessonCharacter(
            id = "kn_hi", char = "日", romaji = "hi / nichi", nepaliPronunciation = "ही / निची",
            type = LessonType.KANJI, meaning = "Sun / Day", nepaliMeaning = "सूर्य / दिन", strokeCount = 4,
            exampleWord = "今日 (kyou)", exampleWordRomaji = "Today", exampleWordNepali = "आज",
            strokes = listOf(
                createStroke(0.3f to 0.25f, 0.3f to 0.75f),
                createStroke(0.3f to 0.25f, 0.7f to 0.25f, 0.7f to 0.75f),
                createStroke(0.3f to 0.5f, 0.7f to 0.5f),
                createStroke(0.3f to 0.75f, 0.7f to 0.75f)
            )
        ),
        LessonCharacter(
            id = "kn_mizu", char = "水", romaji = "mizu / sui", nepaliPronunciation = "मिजु / सुइ",
            type = LessonType.KANJI, meaning = "Water", nepaliMeaning = "पानी", strokeCount = 4,
            exampleWord = "お水 (omizu)", exampleWordRomaji = "Water", exampleWordNepali = "पिउने पानी",
            strokes = listOf(
                createStroke(0.5f to 0.15f, 0.5f to 0.75f, 0.42f to 0.65f),
                createStroke(0.22f to 0.42f, 0.42f to 0.35f),
                createStroke(0.5f to 0.38f, 0.78f to 0.28f),
                createStroke(0.58f to 0.52f, 0.8f to 0.78f)
            )
        ),
        LessonCharacter(
            id = "kn_ki", char = "木", romaji = "ki / moku", nepaliPronunciation = "की / मोकु",
            type = LessonType.KANJI, meaning = "Tree / Wood", nepaliMeaning = "रुख / काठ", strokeCount = 4,
            exampleWord = "木曜日 (mokuyoubi)", exampleWordRomaji = "Thursday", exampleWordNepali = "बिहीबार",
            strokes = listOf(
                createStroke(0.2f to 0.38f, 0.8f to 0.38f),
                createStroke(0.5f to 0.15f, 0.5f to 0.75f),
                createStroke(0.5f to 0.38f, 0.22f to 0.72f),
                createStroke(0.5f to 0.38f, 0.78f to 0.72f)
            )
        ),
        LessonCharacter(
            id = "kn_kawa", char = "川", romaji = "kawa / sen", nepaliPronunciation = "कावा / सेन",
            type = LessonType.KANJI, meaning = "River", nepaliMeaning = "खोला / नदी", strokeCount = 3,
            exampleWord = "川上 (kawakami)", exampleWordRomaji = "Upstream", exampleWordNepali = "नदीको माथिल्लो भाग",
            strokes = listOf(
                createStroke(0.3f to 0.25f, 0.28f to 0.75f),
                createStroke(0.5f to 0.32f, 0.5f to 0.68f),
                createStroke(0.7f to 0.22f, 0.7f to 0.78f)
            )
        ),
        LessonCharacter(
            id = "kn_yama", char = "山", romaji = "yama / san", nepaliPronunciation = "यामा / सान",
            type = LessonType.KANJI, meaning = "Mountain", nepaliMeaning = "पहाड", strokeCount = 3,
            exampleWord = "富士山 (fujisan)", exampleWordRomaji = "Mt. Fuji", exampleWordNepali = "फुजी हिमाल",
            strokes = listOf(
                createStroke(0.5f to 0.2f, 0.5f to 0.75f),
                createStroke(0.25f to 0.48f, 0.25f to 0.75f, 0.75f to 0.75f),
                createStroke(0.75f to 0.42f, 0.75f to 0.75f)
            )
        ),
        LessonCharacter(
            id = "kn_ten", char = "十", romaji = "juu", nepaliPronunciation = "जू",
            type = LessonType.KANJI, meaning = "Ten", nepaliMeaning = "दश (१०)", strokeCount = 2,
            exampleWord = "十日 (touka)", exampleWordRomaji = "10th day", exampleWordNepali = "दशौँ दिन",
            strokes = listOf(
                createStroke(0.18f to 0.48f, 0.82f to 0.48f),
                createStroke(0.5f to 0.18f, 0.5f to 0.82f)
            )
        )
    )

    val vocabularyList = listOf(
        VocabularyWord("こんにちは", "Konnichiwa", "नमस्ते (दिउँसो)", "Greetings", "कोन्निचिवा"),
        VocabularyWord("ありがとう", "Arigatou", "धन्यवाद", "Greetings", "अरिगातोउ"),
        VocabularyWord("さようなら", "Sayounara", "अलविदा / विदा", "Greetings", "सायौणारा"),
        VocabularyWord("お元気ですか", "Ogenki desu ka?", "तपाईंलाई सञ्चै छ?", "Greetings", "ओगेन्की देसु का?"),
        VocabularyWord("お名前は何ですか", "Onamae wa nan desu ka?", "तपाईंको नाम के हो?", "Greetings", "ओनामाए वा नान देसु का?"),
        VocabularyWord("すみません", "Sumimasen", "माफ गर्नुहोला / सुनुस् त", "Greetings", "सुमिमासेन"),
        VocabularyWord("はじめまして", "Hajimemashite", "हजुरलाई भेटेर खुसी लाग्यो", "Greetings", "हाजिमेमासिते"),
        
        VocabularyWord("日本語", "Nihongo", "जापानी भाषा", "Basics", "निहोन्गो"),
        VocabularyWord("先生", "Sensei", "शिक्षक / गुरु", "Basics", "सेन्सेइ"),
        VocabularyWord("学生", "Gakusei", "विद्यार्थी", "Basics", "गाकुसेइ"),
        VocabularyWord("友達", "Tomodachi", "साथी", "Basics", "तोमोदाची"),
        VocabularyWord("学校", "Gakkou", "विद्यालय", "Basics", "गाक्कोउ"),
        VocabularyWord("本", "Hon", "किताब", "Basics", "होन्"),
        VocabularyWord("携帯", "Keitai", "मोबाइल", "Basics", "केइताइ"),

        VocabularyWord("水", "Mizu", "पानी", "Essentials", "मिजु"),
        VocabularyWord("ご飯", "Gohan", "भात / खाना", "Essentials", "गोहान्"),
        VocabularyWord("お茶", "Ocha", "जापानी चिया", "Essentials", "ओचा"),
        VocabularyWord("肉", "Niku", "मासु", "Essentials", "निकु"),
        VocabularyWord("野菜", "Yasai", "तरकारी", "Essentials", "यासाइ"),
        VocabularyWord("魚", "Sakana", "माछा", "Essentials", "साकाना"),
        VocabularyWord("美味しい", "Oishii", "मीठो / स्वादिष्ट", "Essentials", "ओइसी")
    )

    val sentencesList = listOf(
        SentenceData(
            jp = "私は日本語を勉強します。",
            romaji = "Watashi wa nihongo o benkyou shimasu.",
            nepali = "म जापानी भाषा सिक्छु (पढ़छु)।",
            category = "Basics"
        ),
        SentenceData(
            jp = "これは何ですか？",
            romaji = "Kore wa nan desu ka?",
            nepali = "यो के हो?",
            category = "Essentials"
        ),
        SentenceData(
            jp = "お水をお願いします。",
            romaji = "Omizu o onegai shimasu.",
            nepali = "कृपया पानी दिनुहोस्।",
            category = "Essentials"
        ),
        SentenceData(
            jp = "トイレはどこですか？",
            romaji = "Toire wa doko desu ka?",
            nepali = "शौचालय कहाँ छ?",
            category = "Essentials"
        ),
        SentenceData(
            jp = "日本が大好きです。",
            romaji = "Nihon ga daisuki desu.",
            nepali = "मलाई जापान धेरै मनपर्छ।",
            category = "Feeling"
        )
    )
}
