package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.QuizHistory
import com.example.data.database.UserStats
import com.example.ui.viewmodel.JapaneseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Representation of badge structures
data class BadgeSpec(
    val nameId: String,
    val title: String,
    val description: String,
    val emoji: String,
    val activeColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: JapaneseViewModel,
    stats: UserStats,
    quizHistoryList: List<QuizHistory>,
    onBack: () -> Unit
) {
    val unlockedBadges = stats.getBadgesList().toSet()

    val isScreenshotProtected by viewModel.isScreenshotProtectionEnabled.collectAsState()
    val isPinLockedEnabled by viewModel.pinLockEnabled.collectAsState()
    val savedPin by viewModel.appPin.collectAsState()

    var showPinDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val badgeSpecsList = listOf(
        BadgeSpec("First Step", "पहिलो कदम (First Step)", "पहिलो पटक जापानी अक्षर कोरेर अभ्यास पुरा गर्नुभयो।", "🌱", Color(0xFF81C784)),
        BadgeSpec("Perfect Score", "उत्कृष्ट अङ्क (Perfect Score)", "क्विजको सबै प्रश्नको सही उत्तर मिलाउनुभयो।", "💯", Color(0xFFFFD54F)),
        BadgeSpec("Three Day Flame", "तीन दिने ज्वाला (3-Day Streak)", "लगातार ३ दिनसम्म जापानी साथी एप प्रयोग गर्नुभयो।", "🔥", Color(0xFFFF8A65)),
        BadgeSpec("Beginner Explorer", "सुरुआती खोजकर्ता (Beginner Explorer)", "५० ताराहरू (Stars) बटुलन सफल हुनुभयो।", "⭐", Color(0xFFFFD54F)),
        BadgeSpec("Determined Learner", "दृढ शिक्षार्थी (Determined)", "१५० ताराहरू (Stars) सङ्कलन गर्नुभयो।", "🎯", Color(0xFF64B5F6)),
        BadgeSpec("Hiragana Master", "हिरागाना मास्टर (Hiragana Master)", "३०० ताराहरू बटुलेर हिरागानाको ज्ञान प्राप्त।", "🎏", Color(0xFFFF8A80)),
        BadgeSpec("Kanji Sage", "कान्जी ज्ञानी (Kanji Sage)", "५०० ताराहरू सङ्कलन गरी कान्जी शिखर चुम्नुभयो।", "🦉", Color(0xFFB0BEC5))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("मेरो विवरण (Your Profile & Badges)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_btn")) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            
            // 1. STATS SUMMARY COMPACTS CARD
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(18.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        colors = listOf(Color(0xFF6750A4), Color(0xFFB583FF))
                                    ),
                                    shape = CircleShape
                                )
                        ) {
                            Text(text = "🦉", fontSize = 36.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "जापानी शिक्षार्थी (Sathi)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "स्तर Level: ${stats.level}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("profile_level_display")
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.LightGray.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD600), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${stats.stars}", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.testTag("profile_stars_display"))
                                }
                                Text(text = "कुल ताराहरू", fontSize = 11.sp, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFF9800), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${stats.currentStreak}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = "सक्रिय दिन", fontSize = 11.sp, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color(0xFF2196F3), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "${quizHistoryList.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(text = "कुल क्विजहरू", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // 2. BADGE CABINET SECTION
            item {
                Text(
                    text = "मेरो पदक सूची (Unlocked Badges)",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Display badges list
            items(badgeSpecsList) { badge ->
                val isUnlocked = unlockedBadges.contains(badge.nameId)
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, if (isUnlocked) badge.activeColor.copy(alpha = 0.3f) else Color(0xFFF1F5F9)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (isUnlocked) 1f else 0.5f)
                        .testTag("badge_row_${badge.nameId}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge Icon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .background(if (isUnlocked) badge.activeColor.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f), shape = CircleShape)
                                .border(1.5.dp, if (isUnlocked) badge.activeColor else Color.LightGray, CircleShape)
                        ) {
                            Text(text = if (isUnlocked) badge.emoji else "🔒", fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = badge.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) Color.Black else Color.Gray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = badge.description,
                                fontSize = 11.sp,
                                color = if (isUnlocked) Color.DarkGray else Color.LightGray
                            )
                        }
                    }
                }
            }

            // 2.5 SECURITY & PRIVACY SETTINGS
            item {
                Text(
                    text = "सुरक्षा र गोपनीयता (Security & Privacy)",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            
            item {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Screenshot protection switch row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "स्क्रिनसट सुरक्षा (Prevent Screenshot)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "स्क्रिन रेकर्डिङ र कास्टिङ रोक्छ।",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Switch(
                                checked = isScreenshotProtected,
                                onCheckedChange = { viewModel.setScreenshotProtectionEnabled(it) },
                                thumbContent = if (isScreenshotProtected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else null
                            )
                        }
                        
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        
                        // 2. PIN Lock row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "अनुप्रयोग पिन लक (App PIN Lock)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isPinLockedEnabled) "४-अङ्कको पिन सक्रिय छ: $savedPin" else "एप खोल्न पिन लक आवश्यक बनाउनुहोस्।",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Button(
                                onClick = { showPinDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPinLockedEnabled) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if (isPinLockedEnabled) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = if (isPinLockedEnabled) "हटाउनुहोस्" else "सेट गर्नुहोस्",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        
                        // 3. Clear data row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFFFEBEE), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteForever,
                                        contentDescription = null,
                                        tint = Color(0xFFC62828),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "सुरक्षित डाटा विसर्जन (Secure Wipe)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC62828)
                                    )
                                    Text(
                                        text = "सबै स्थानीय इतिहास स्थायी रूपमा मेटाउँछ।",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { showResetDialog = true },
                                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = "रीसेट गर्नुहोस्",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. QUIZ HISTORIES LIST
            item {
                Text(
                    text = "क्विज इतिहास (Quiz Scoring History)",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (quizHistoryList.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                                .testTag("quiz_history_empty")
                        ) {
                            Text(
                                text = "कुनै क्विज इतिहास भेटिएन। एउटा क्विज पुरा गर्नुहोस्!",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(quizHistoryList) { hItem ->
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quiz_history_item")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = when (hItem.category) {
                                        "HIRAGANA" -> "हिरागाना क्विज"
                                        "KATAKANA" -> "काताकाना क्विज"
                                        "KANJI" -> "कान्जी खेल"
                                        "VOCABULARY" -> "शब्द भण्डार"
                                        else -> hItem.category
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = formatDate(hItem.timestamp),
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }

                            // Correct answers percentage indicator
                            val ratioPercent = (hItem.score.toFloat() / hItem.totalQuestions * 100).toInt()
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (ratioPercent >= 60) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${hItem.score} / ${hItem.totalQuestions} मिलेको",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (ratioPercent >= 60) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPinDialog) {
        var enteredPinText by remember { mutableStateOf("") }
        var isPinError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = {
                Text(
                    text = if (isPinLockedEnabled) "पिन लक हटाउनुहोस्" else "सुरक्षित पिन लक सेट गर्नुहोस्",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isPinLockedEnabled) 
                            "सुरक्षा पिन हटाउनको लागि हालको पिन टाइप गर्नुहोस्:"
                        else 
                            "एप खोल्न सुरक्षित राख्नको लागि नयाँ ४-अङ्कको पिन राख्नुहोस्:"
                    )
                    OutlinedTextField(
                        value = enteredPinText,
                        onValueChange = { input -> 
                            if (input.all { it.isDigit() } && input.length <= 4) {
                                enteredPinText = input
                                isPinError = false
                            }
                        },
                        label = { Text("४-अङ्कको पिन (4-Digit PIN)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (isPinError) {
                        Text(
                            text = "पिन मिलेन वा ४ अङ्कको छैन!",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (isPinLockedEnabled) {
                            if (enteredPinText == savedPin) {
                                viewModel.setPin("")
                                showPinDialog = false
                            } else {
                                isPinError = true
                            }
                        } else {
                            if (enteredPinText.length == 4) {
                                viewModel.setPin(enteredPinText)
                                showPinDialog = false
                            } else {
                                isPinError = true
                            }
                        }
                    }
                ) {
                    Text(text = if (isPinLockedEnabled) "हटाउनुहोस्" else "सुरक्षित गर्नुहोस्")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text(text = "रद्द गर्नुहोस्")
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "डाटा पूर्ण रीसेटको चेतावनी",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFFC62828)
                )
            },
            text = {
                Text(text = "तपाईंको सबै जापानी सिकाइ प्रगति र क्विजका स्कोरहरू मेटिनेछन्। के तपाईं वास्तवमै अगाडि बढ्न चाहनुहुन्छ?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.secureWipeData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                ) {
                    Text(text = "हो, मेटाउनुहोस्")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = "रद्द")
                }
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
