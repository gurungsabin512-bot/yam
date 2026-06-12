package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LessonCharacter
import com.example.data.LessonType
import com.example.data.SentenceData
import com.example.data.VocabularyWord
import com.example.data.database.CharacterProgress
import com.example.ui.components.rememberNihongoSpeaker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    lessonTypeStr: String, // "HIRAGANA", "KATAKANA", "KANJI", "VOCABULARY", "SENTENCES"
    characters: List<LessonCharacter>,
    vocabulary: List<VocabularyWord>,
    sentences: List<SentenceData>,
    progressList: List<CharacterProgress>,
    onBack: () -> Unit,
    onPracticeChar: (LessonCharacter) -> Unit
) {
    val speaker = rememberNihongoSpeaker()
    
    // Convert progress list to map for instant lookup
    val progressMap = remember(progressList) {
        progressList.associateBy { it.charId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = when (lessonTypeStr) {
                                "HIRAGANA" -> "हिरागाना वर्णमाला (Hiragana)"
                                "KATAKANA" -> "काताकाना अक्षरहरू (Katakana)"
                                "KANJI" -> "कान्जी लिपि (Kanji)"
                                "VOCABULARY" -> "जापानी उपयोगी शब्दहरू (Vocabulary)"
                                "SENTENCES" -> "आधारभूत वाक्यहरू (Sentences)"
                                else -> "शिक्षक पाठ (Lessons)"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = when (lessonTypeStr) {
                                "HIRAGANA" -> "मौलिक शब्दावली र नेपाली उच्चारण"
                                "KATAKANA" -> "विदेशी तथा लोन शब्दहरू"
                                "KANJI" -> "चित्र र आकारबाट बनेका ऐतिहासिक प्राचीन लिपि"
                                "VOCABULARY" -> "दैनिक व्यवहारमा प्रयोग हुने आवश्यक जापानी शब्द भण्डार"
                                "SENTENCES" -> "नेपाली अनुवाद र रोमान्जी उच्चारण सहित"
                                else -> "lessons sathi"
                            },
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("lessons_back")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            when (lessonTypeStr) {
                "HIRAGANA", "KATAKANA", "KANJI" -> {
                    // Display characters in a grid format
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize().testTag("lessons_grid")
                    ) {
                        items(characters) { charItem ->
                            val progress = progressMap[charItem.id]
                            val isLearned = progress?.isLearned == true
                            CharacterGridCard(
                                charItem = charItem,
                                isLearned = isLearned,
                                onSpeak = { speaker.speak(charItem.char) },
                                onPractice = { onPracticeChar(charItem) }
                            )
                        }
                    }
                }
                "VOCABULARY" -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize().testTag("vocabulary_list")
                    ) {
                        items(vocabulary) { word ->
                            VocabularyRowCard(
                                word = word,
                                onSpeak = { speaker.speak(word.jp) }
                            )
                        }
                    }
                }
                "SENTENCES" -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize().testTag("sentences_list")
                    ) {
                        items(sentences) { sentence ->
                            SentenceRowCard(
                                sentence = sentence,
                                onSpeak = { speaker.speak(sentence.jp) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterGridCard(
    charItem: LessonCharacter,
    isLearned: Boolean,
    onSpeak: () -> Unit,
    onPractice: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, if (isLearned) Color(0xFF10B981) else Color(0xFFF1F5F9)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("char_card_${charItem.id}")
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Checked indicator + Speaker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Done status
                if (isLearned) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Learned",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(20.dp))
                }

                // Volume Up
                IconButton(onClick = onSpeak, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Play Audio",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Big character visualization
            Text(
                text = charItem.char,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Romaji label
            Text(
                text = charItem.romaji,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Nepali phonetic
            Text(
                text = "नेपाली: ${charItem.nepaliPronunciation}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )

            // Kanji translation label if needed
            if (charItem.type == LessonType.KANJI && charItem.meaning.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${charItem.meaning} (${charItem.nepaliMeaning})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Tracing Button
            Button(
                onClick = onPractice,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLearned) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (isLearned) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onPrimaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("practice_btn_${charItem.id}")
            ) {
                Icon(
                    Icons.Default.Draw,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isLearned) "कोर्नुहोस् (Retrace)" else "कोर्नुहोस् (Trace)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun VocabularyRowCard(
    word: VocabularyWord,
    onSpeak: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vocab_card_${word.jp}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = word.jp,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "(${word.romaji})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "उच्चारण: '${word.pronunNepali()}'",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "नेपाली अर्थ: ${word.nepaliText}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2E7D32) // Fresh emerald green for success meanings
                )
            }

            // Speak Audio
            IconButton(
                onClick = onSpeak,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.Default.VolumeUp,
                    contentDescription = "Speak Vocabulary",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SentenceRowCard(
    sentence: SentenceData,
    onSpeak: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sentence_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                IconButton(
                    onClick = onSpeak,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        Icons.Default.VolumeUp,
                        contentDescription = "Speak Sentence",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = sentence.jp,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Romaji: ${sentence.romaji}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Divider(color = Color.LightGray.copy(alpha = 0.4f))

            Text(
                text = "नेपाली अनुवाद: ${sentence.nepali}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0) // Soft deep blue
            )
        }
    }
}

private fun VocabularyWord.pronunNepali(): String {
    return this.pronunciationNepali
}
