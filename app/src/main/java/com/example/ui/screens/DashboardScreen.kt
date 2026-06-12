package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    stats: UserStats,
    onNavigateToLesson: (String) -> Unit, // "HIRAGANA", "KATAKANA", "KANJI", "VOCABULARY", "SENTENCES"
    onNavigateToQuizMenu: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "जापानी साथी (Nihongo Sathi)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile, modifier = Modifier.testTag("dashboard_profile_icon")) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background // Sleek light lavender layout background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // 1. GAMIFIED HERO STATUS HEADER PANEL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6750A4), // Sleek Purple
                                Color(0xFFB583FF)  // Sleek Lavender
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFD1FAE5)) // bg-emerald-100
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "स्तर ${stats.level}",
                                    color = Color(0xFF047857), // text-emerald-700
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.testTag("dashboard_level_label")
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "नमस्ते, साथी! 👋",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Daily Streak Badge in top right corner
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .testTag("dashboard_streak_box")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "${stats.currentStreak} दिन",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stars XP balance bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Stars",
                                tint = Color(0xFFFFD600),
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "${stats.stars} ताराहरू",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("dashboard_stars_count")
                            )
                        }
                        
                        // Progress to next level
                        val starsInLevel = stats.stars % 100
                        Text(
                            text = "${starsInLevel}/100 XP",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    val levelProgress = (stats.stars % 100).toFloat() / 100f
                    LinearProgressIndicator(
                        progress = levelProgress,
                        color = Color(0xFF10B981), // Emerald green progress indicator
                        trackColor = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                    )
                }
            }

            // 2. BADGE STRIP ROW (SNEAK PEEK OF PROGRESS)
            if (stats.getBadgesList().isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "तपाईंको पदकहरू (Unlocked Badges)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            stats.getBadgesList().take(4).forEach { bName ->
                                val (badgeEmoji, badgeColor) = when (bName) {
                                    "First Step" -> "🌱" to Color(0xFF81C784)
                                    "Perfect Score" -> "💯" to Color(0xFFFFD54F)
                                    "Three Day Flame" -> "🔥" to Color(0xFFFF8A65)
                                    "Beginner Explorer" -> "⭐" to Color(0xFFFFD54F)
                                    "Determined Learner" -> "🎯" to Color(0xFF64B5F6)
                                    "Hiragana Master" -> "🎏" to Color(0xFFFF8A80)
                                    "Kanji Sage" -> "🦉" to Color(0xFFB0BEC5)
                                    else -> "🎖️" to Color(0xFFE0E0E0)
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(badgeColor, shape = CircleShape)
                                ) {
                                    Text(text = badgeEmoji, fontSize = 22.sp)
                                }
                            }
                            if (stats.getBadgesList().size > 4) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color.LightGray.copy(alpha = 0.5f), shape = CircleShape)
                                ) {
                                    Text(
                                        text = "+${stats.getBadgesList().size - 4}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. ROADMAP / CATEGORIES SELECTOR
            Text(
                text = "जापानी सिक्ने शैक्षिक रोडम्याप (Learning Roadmap)",
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            // Dynamic grid layout using custom layout or responsive Columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCategoryCard(
                    title = "हिरागाना",
                    subTitle = "Hiragana",
                    nepaliText = "वर्णमाला (४६ अक्षर)",
                    icon = Icons.Default.Translate,
                    accentColor = Color(0xFF4CAF50),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_menu_hiragana"),
                    onClick = { onNavigateToLesson("HIRAGANA") }
                )

                DashboardCategoryCard(
                    title = "काताकाना",
                    subTitle = "Katakana",
                    nepaliText = "विदेशी शब्दहरू",
                    icon = Icons.Default.FontDownload,
                    accentColor = Color(0xFF2196F3),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_menu_katakana"),
                    onClick = { onNavigateToLesson("KATAKANA") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCategoryCard(
                    title = "कान्जी",
                    subTitle = "Kanji",
                    nepaliText = "पिक्चर लिपि",
                    icon = Icons.Default.Eco,
                    accentColor = Color(0xFF9C27B0),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_menu_kanji"),
                    onClick = { onNavigateToLesson("KANJI") }
                )

                DashboardCategoryCard(
                    title = "शब्द भण्डार",
                    subTitle = "Vocabulary",
                    nepaliText = "आधारभूत शब्दहरू",
                    icon = Icons.Default.MenuBook,
                    accentColor = Color(0xFFFF9800),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_menu_vocabulary"),
                    onClick = { onNavigateToLesson("VOCABULARY") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardCategoryCard(
                    title = "वाक्यहरू",
                    subTitle = "Sentences",
                    nepaliText = "दैनिक कुराकानी",
                    icon = Icons.Default.QuestionAnswer,
                    accentColor = Color(0xFF00BCD4),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_menu_sentences"),
                    onClick = { onNavigateToLesson("SENTENCES") }
                )

                DashboardCategoryCard(
                    title = "क्विज खेल्नुस्",
                    subTitle = "Quiz Arena",
                    nepaliText = "स्तर जाँच्नुहोस्",
                    icon = Icons.Default.EmojiEvents,
                    accentColor = Color(0xFFFFEB3B),
                    textColor = Color(0xFF3E2723),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_menu_quizzes"),
                    onClick = onNavigateToQuizMenu
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DashboardCategoryCard(
    title: String,
    subTitle: String,
    nepaliText: String,
    icon: ImageVector,
    accentColor: Color,
    textColor: Color = Color.White,
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
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Accent circle icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .background(accentColor.copy(alpha = 0.15f), shape = CircleShape)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = subTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = nepaliText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
