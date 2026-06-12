package com.example

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.LessonType
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LessonsScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.JapaneseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: JapaneseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Listen dynamically to secure layout preferences
        lifecycleScope.launch {
            viewModel.isScreenshotProtectionEnabled.collectLatest { enabled ->
                if (enabled) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_SECURE,
                        WindowManager.LayoutParams.FLAG_SECURE
                    )
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }
        }

        setContent {
            MyApplicationTheme {
                PinLockScreenOverlay(viewModel = viewModel) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NihongoAppNavHost(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PinLockScreenOverlay(
    viewModel: JapaneseViewModel,
    content: @Composable () -> Unit
) {
    val isLocked by viewModel.isAppLocked.collectAsState()
    
    if (isLocked) {
        var pinInput by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Icon Header
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                
                Text(
                    text = "सुरक्षित पहुँच (Secure Access)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "कृपया अगाडि बढ्नको लागि ४-अङ्कको पिन हाल्नुहोस्।",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Indicators dots representing digits
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { idx ->
                        val filled = idx < pinInput.length
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(
                                    color = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
                
                if (isError) {
                    Text(
                        text = "गलत पिन! फेरि सही पिन थिच्नुहोस्।",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Spacer(modifier = Modifier.height(18.dp))
                }
                
                // Keypad grid
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("Clear", "0", "Back")
                    )
                    
                    keys.forEach { rowKeys ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowKeys.forEach { key ->
                                if (key == "Clear") {
                                    IconButton(
                                        onClick = {
                                            pinInput = ""
                                            isError = false
                                        },
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Text(
                                            text = "CLR",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else if (key == "Back") {
                                    IconButton(
                                        onClick = {
                                            if (pinInput.isNotEmpty()) {
                                                pinInput = pinInput.dropLast(1)
                                            }
                                        },
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Backspace,
                                            contentDescription = "Backspace",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                                            .clip(CircleShape)
                                            .clickable {
                                                if (pinInput.length < 4) {
                                                    pinInput += key
                                                    isError = false
                                                    if (pinInput.length == 4) {
                                                        val correct = viewModel.unlockApp(pinInput)
                                                        if (!correct) {
                                                            isError = true
                                                            pinInput = ""
                                                        }
                                                    }
                                                }
                                            }
                                    ) {
                                        Text(
                                            text = key,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        content()
    }
}

@Composable
fun NihongoAppNavHost(
    viewModel: JapaneseViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Collect Room Flows
    val userStats by viewModel.userStatsState.collectAsState()
    val allProgress by viewModel.allCharacterProgress.collectAsState()
    val quizHistoryList by viewModel.quizHistory.collectAsState()
    val quizState by viewModel.quizState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        // 1. Dashboard Screen (Entry point)
        composable("dashboard") {
            DashboardScreen(
                stats = userStats,
                onNavigateToLesson = { lessonType ->
                    navController.navigate("lessons/$lessonType")
                },
                onNavigateToQuizMenu = {
                    viewModel.resetQuiz()
                    navController.navigate("quiz")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                }
            )
        }

        // 2. Lessons Selection Hub Screen
        composable(
            route = "lessons/{type}",
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonType = backStackEntry.arguments?.getString("type") ?: "HIRAGANA"
            
            LessonsScreen(
                lessonTypeStr = lessonType,
                characters = when (lessonType) {
                    "HIRAGANA" -> viewModel.hiraganaList
                    "KATAKANA" -> viewModel.katakanaList
                    "KANJI" -> viewModel.kanjiList
                    else -> emptyList()
                },
                vocabulary = viewModel.vocabularyList,
                sentences = viewModel.sentencesList,
                progressList = allProgress,
                onBack = { navController.popBackStack() },
                onPracticeChar = { characterItem ->
                    navController.navigate("practice/${characterItem.id}/${characterItem.type.name}")
                }
            )
        }

        // 3. Tracing Practice screen
        composable(
            route = "practice/{charId}/{type}",
            arguments = listOf(
                navArgument("charId") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val charId = backStackEntry.arguments?.getString("charId") ?: ""
            val typeStr = backStackEntry.arguments?.getString("type") ?: "HIRAGANA"
            
            // Find corresponding character from localized state stores
            val character = when (typeStr) {
                "HIRAGANA" -> viewModel.hiraganaList.find { it.id == charId }
                "KATAKANA" -> viewModel.katakanaList.find { it.id == charId }
                "KANJI" -> viewModel.kanjiList.find { it.id == charId }
                else -> null
            }

            if (character != null) {
                PracticeScreen(
                    character = character,
                    onBack = { navController.popBackStack() },
                    onComplete = {
                        // Mark traced inside the db
                        viewModel.completeTracing(character)
                        navController.popBackStack()
                    }
                )
            } else {
                navController.popBackStack()
            }
        }

        // 4. Interactive Quiz challenges screen
        composable("quiz") {
            QuizScreen(
                state = quizState,
                onStartQuiz = { category ->
                    viewModel.startNewQuiz(category)
                },
                onSelectOption = { index ->
                    viewModel.selectQuizOption(index)
                },
                onCheckAnswer = {
                    viewModel.checkAnswer()
                },
                onNextQuestion = {
                    viewModel.nextQuizQuestion()
                },
                onCloseQuiz = {
                    viewModel.resetQuiz()
                    navController.popBackStack()
                }
            )
        }

        // 5. Historical Quiz Stats / Badge Cabinet screen
        composable("profile") {
            ProfileScreen(
                viewModel = viewModel,
                stats = userStats,
                quizHistoryList = quizHistoryList,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
