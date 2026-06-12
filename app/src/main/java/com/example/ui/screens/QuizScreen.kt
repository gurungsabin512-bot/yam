package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.viewmodel.QuizState

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    state: QuizState,
    onStartQuiz: (String) -> Unit, // "HIRAGANA", "KATAKANA", "KANJI", "VOCABULARY"
    onSelectOption: (Int) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onCloseQuiz: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("क्विज खेल (Quiz Challenge)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                navigationIcon = {
                    IconButton(onClick = onCloseQuiz, modifier = Modifier.testTag("quiz_close_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                .padding(16.dp)
        ) {
            when (state) {
                is QuizState.Idle -> {
                    // Category Selection Menu
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFD600),
                            modifier = Modifier
                                .size(96.dp)
                                .padding(bottom = 16.dp)
                        )

                        Text(
                            text = "क्विज खेल्नुहोस् र ताराहरू कमाउनुहोस्!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "आफ्नो जापानी ज्ञान जाँच्न र नयाँ ब्याजहरू अनलक गर्न कुनै एक विकल्प रोज्नुहोस्:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        QuizCategorySelectorBtn(
                            title = "हिरागाना क्विज (Hiragana Quiz)",
                            description = "४६ मौलिक अक्षरसँग सम्बन्धित खेल",
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.testTag("start_quiz_hiragana"),
                            onClick = { onStartQuiz("HIRAGANA") }
                        )

                        QuizCategorySelectorBtn(
                            title = "काताकाना क्विज (Katakana Quiz)",
                            description = "कातिकाना पहिचान सम्बन्धी खेल",
                            color = Color(0xFF2196F3),
                            modifier = Modifier.testTag("start_quiz_katakana"),
                            onClick = { onStartQuiz("KATAKANA") }
                        )

                        QuizCategorySelectorBtn(
                            title = "कान्जी खेल (Kanji Match)",
                            description = "कान्जी र यसको नेपाली हिज्जे खेल",
                            color = Color(0xFF9C27B0),
                            modifier = Modifier.testTag("start_quiz_kanji"),
                            onClick = { onStartQuiz("KANJI") }
                        )

                        QuizCategorySelectorBtn(
                            title = "शब्द भण्डार क्विज (Vocabulary Quiz)",
                            description = "महत्वपूर्ण जापानी शब्दको नेपाली अर्थ खेल",
                            color = Color(0xFFFF9800),
                            modifier = Modifier.testTag("start_quiz_vocab"),
                            onClick = { onStartQuiz("VOCABULARY") }
                        )
                    }
                }
                is QuizState.Active -> {
                    if (state.isCompleted) {
                        // Display Final Quiz Results Summary
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val percent = (state.score.toFloat() / state.questions.size * 100).toInt()
                            
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(160.dp)
                            ) {
                                CircularProgressIndicator(
                                    progress = percent.toFloat() / 100f,
                                    color = if (percent >= 60) Color(0xFF4CAF50) else Color(0xFFFF9800),
                                    trackColor = Color.LightGray.copy(alpha = 0.3f),
                                    strokeWidth = 12.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$percent%",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "नतिजा (Result)",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = if (percent == 100) "उत्कृष्ट! पूर्ण अङ्क प्राप्त 🎉" else if (percent >= 60) "बधाई छ साथी! पास हुनुभयो 🏆" else "राम्रो प्रयत्न! फेरि प्रयास गर्नुहोस् 👍",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (percent >= 60) Color(0xFF388E3C) else Color(0xFFE65100),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "तपाईंले ${state.questions.size} मध्ये ${state.score} सही जवाफ मिलाउनुभयो।",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "अर्जित पुरस्कार (Rewards Earned)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD600),
                                            modifier = Modifier.size(28.dp)
                                        )
                                        val totalGainedStars = state.score * 15 + (if (percent == 100) 25 else 0)
                                        Text(
                                            text = "+$totalGainedStars ताराहरू (Stars)",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (percent == 100) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "🏅 नयाँ ब्याज अनलक भयो: Excel Perfect Score!",
                                            fontSize = 11.sp,
                                            color = Color(0xFFD81B60),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Button(
                                onClick = onCloseQuiz,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                                    .height(56.dp)
                                    .testTag("quiz_finish_back_home")
                            ) {
                                Text("मुख्य पृष्ठमा फर्कनुहोस्", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    } else {
                        // Question Card (Active gameplay)
                        val question = state.questions[state.currentIndex]
                        
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Question progress bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "प्रश्न ${state.currentIndex + 1} / ${state.questions.size}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "मिलाएको अङ्क: ${state.score}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }

                            val quizProgress = (state.currentIndex).toFloat() / state.questions.size
                            LinearProgressIndicator(
                                progress = quizProgress,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.LightGray.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                            )

                            // Display Challenge Word / Character
                            Card(
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = question.jpText,
                                            fontSize = if (question.jpText.length > 5) 28.sp else 46.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = question.prompt,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            // Multiple choice buttons list
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                question.options.forEachIndexed { optIdx, choiceText ->
                                    val isSelected = state.selectedOption == optIdx
                                    val isChecked = state.isAnswerChecked
                                    val isCorrectOpt = optIdx == question.correctIndex
                                    
                                    // Determine styling color
                                    val rowBgColor = when {
                                        isChecked && isCorrectOpt -> Color(0xFFE8F5E9)      // Success Soft Green
                                        isChecked && isSelected && !isCorrectOpt -> Color(0xFFFFEBEE) // Error Soft Red
                                        isSelected -> MaterialTheme.colorScheme.secondaryContainer    // Selected normal
                                        else -> Color.White
                                    }

                                    val rowBorderColor = when {
                                        isChecked && isCorrectOpt -> Color(0xFF4CAF50)
                                        isChecked && isSelected && !isCorrectOpt -> Color(0xFFE53935)
                                        isSelected -> MaterialTheme.colorScheme.secondary
                                        else -> Color.LightGray.copy(alpha = 0.5f)
                                    }

                                    Card(
                                        shape = RoundedCornerShape(24.dp),
                                        colors = CardDefaults.cardColors(containerColor = rowBgColor),
                                        border = BorderStroke(1.dp, rowBorderColor),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !isChecked) { onSelectOption(optIdx) }
                                            .testTag("quiz_option_$optIdx")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = choiceText,
                                                fontSize = 16.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            // Trailing status indicator icon
                                            when {
                                                isChecked && isCorrectOpt -> {
                                                    Icon(
                                                        Icons.Default.CheckCircle,
                                                        contentDescription = "Correct",
                                                        tint = Color(0xFF4CAF50)
                                                    )
                                                }
                                                isChecked && isSelected && !isCorrectOpt -> {
                                                    Icon(
                                                        Icons.Default.Cancel,
                                                        contentDescription = "Incorrect",
                                                        tint = Color(0xFFE53935)
                                                    )
                                                }
                                                isSelected -> {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(18.dp)
                                                            .background(MaterialTheme.colorScheme.secondary, shape = CircleShape)
                                                    )
                                                }
                                                else -> {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(18.dp)
                                                            .background(Color.White, shape = CircleShape)
                                                            .border(1.5.dp, Color.Gray, CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Action submission buttons
                            AnimatedVisibility(
                                visible = state.selectedOption != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut()
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    if (!state.isAnswerChecked) {
                                        Button(
                                            onClick = onCheckAnswer,
                                            shape = RoundedCornerShape(20.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(54.dp)
                                                .testTag("quiz_submit_btn")
                                        ) {
                                            Text("उत्तर पेश गर्नुहोस् (Submit)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        }
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                            // Quick Explanatory panel
                                            Card(
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (state.selectedOption == question.correctIndex) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Text(
                                                        text = "स्पष्टीकरण (Explanation):",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.DarkGray
                                                    )
                                                    Text(
                                                        text = question.explanation,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.Black
                                                    )
                                                }
                                            }

                                            Button(
                                                onClick = onNextQuestion,
                                                shape = RoundedCornerShape(20.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(54.dp)
                                                    .testTag("quiz_next_btn")
                                            ) {
                                                Text(
                                                    text = if (state.currentIndex + 1 < state.questions.size) "अर्को प्रश्न (Next)" else "क्विज समाप्त (Finish)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCategorySelectorBtn(
    title: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), shape = CircleShape)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
