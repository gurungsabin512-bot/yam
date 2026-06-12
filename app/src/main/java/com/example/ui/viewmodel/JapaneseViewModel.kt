package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.JapaneseData
import com.example.data.LessonCharacter
import com.example.data.LessonType
import com.example.data.SentenceData
import com.example.data.VocabularyWord
import com.example.data.database.AppDatabase
import com.example.data.database.CharacterProgress
import com.example.data.database.ProgressRepository
import com.example.data.database.QuizHistory
import com.example.data.database.UserStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class QuizQuestion(
    val jpText: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

sealed interface QuizState {
    object Idle : QuizState
    data class Active(
        val category: String,
        val questions: List<QuizQuestion>,
        val currentIndex: Int,
        val score: Int,
        val selectedOption: Int?, // Selected option index, null if not selected yet
        val isAnswerChecked: Boolean, // True when submitted but not proceeded to next yet
        val isCompleted: Boolean = false
    ) : QuizState
}

class JapaneseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProgressRepository
    private val prefs = application.getSharedPreferences("secure_sathi_prefs", android.content.Context.MODE_PRIVATE)

    private val _isScreenshotProtectionEnabled = MutableStateFlow(prefs.getBoolean("screenshot_protection", false))
    val isScreenshotProtectionEnabled: StateFlow<Boolean> = _isScreenshotProtectionEnabled.asStateFlow()

    private val _pinLockEnabled = MutableStateFlow(prefs.getBoolean("pin_lock_enabled", false))
    val pinLockEnabled: StateFlow<Boolean> = _pinLockEnabled.asStateFlow()

    private val _appPin = MutableStateFlow(prefs.getString("app_pin", "") ?: "")
    val appPin: StateFlow<String> = _appPin.asStateFlow()

    private val _isAppLocked = MutableStateFlow(prefs.getBoolean("pin_lock_enabled", false))
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProgressRepository(database.progressDao())
        
        // Trigger streak validation
        viewModelScope.launch {
            repository.updateStreak()
        }
    }

    fun setScreenshotProtectionEnabled(enabled: Boolean) {
        _isScreenshotProtectionEnabled.value = enabled
        prefs.edit().putBoolean("screenshot_protection", enabled).apply()
    }

    fun setPin(pin: String) {
        val hasPin = pin.isNotEmpty() && pin.length == 4
        _appPin.value = pin
        _pinLockEnabled.value = hasPin
        prefs.edit().putString("app_pin", pin).putBoolean("pin_lock_enabled", hasPin).apply()
        if (!hasPin) {
            _isAppLocked.value = false
        }
    }

    fun unlockApp(enteredPin: String): Boolean {
        if (enteredPin == _appPin.value) {
            _isAppLocked.value = false
            return true
        }
        return false
    }

    fun lockAppManual() {
        if (_pinLockEnabled.value) {
            _isAppLocked.value = true
        }
    }

    fun secureWipeData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    // Reactively observe room flows
    val userStatsState: StateFlow<UserStats> = repository.userStatsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats()
        )

    val allCharacterProgress: StateFlow<List<CharacterProgress>> = repository.allCharacterProgressFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val quizHistory: StateFlow<List<QuizHistory>> = repository.quizHistoryFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current lists available
    val hiraganaList = JapaneseData.hiraganaList
    val katakanaList = JapaneseData.katakanaList
    val kanjiList = JapaneseData.kanjiList
    val vocabularyList = JapaneseData.vocabularyList
    val sentencesList = JapaneseData.sentencesList

    // State for practice tracing
    private val _selectedChar = MutableStateFlow<LessonCharacter?>(null)
    val selectedChar = _selectedChar.asStateFlow()

    // State for quiz game
    private val _quizState = MutableStateFlow<QuizState>(QuizState.Idle)
    val quizState = _quizState.asStateFlow()

    fun selectCharacter(character: LessonCharacter?) {
        _selectedChar.value = character
    }

    fun completeTracing(char: LessonCharacter) {
        viewModelScope.launch {
            repository.recordTracingSuccess(char.id, char.type.name, starReward = 15)
        }
    }

    // Quiz functions (Gamified mechanics)
    fun startNewQuiz(category: String) {
        viewModelScope.launch {
            val questions = generateRandomQuiz(category)
            _quizState.value = QuizState.Active(
                category = category,
                questions = questions,
                currentIndex = 0,
                score = 0,
                selectedOption = null,
                isAnswerChecked = false
            )
        }
    }

    fun selectQuizOption(index: Int) {
        val current = _quizState.value
        if (current is QuizState.Active && !current.isAnswerChecked) {
            _quizState.value = current.copy(selectedOption = index)
        }
    }

    fun checkAnswer() {
        val current = _quizState.value
        if (current is QuizState.Active && current.selectedOption != null && !current.isAnswerChecked) {
            val question = current.questions[current.currentIndex]
            val isCorrect = current.selectedOption == question.correctIndex
            val newScore = if (isCorrect) current.score + 1 else current.score
            
            _quizState.value = current.copy(
                isAnswerChecked = true,
                score = newScore
            )
        }
    }

    fun nextQuizQuestion() {
        val current = _quizState.value
        if (current is QuizState.Active && current.isAnswerChecked) {
            if (current.currentIndex + 1 < current.questions.size) {
                _quizState.value = current.copy(
                    currentIndex = current.currentIndex + 1,
                    selectedOption = null,
                    isAnswerChecked = false
                )
            } else {
                // Quiz completed! Save to DB & award stars
                viewModelScope.launch {
                    repository.recordQuizScore(current.category, current.score, current.questions.size)
                    _quizState.value = current.copy(isCompleted = true)
                }
            }
        }
    }

    fun resetQuiz() {
        _quizState.value = QuizState.Idle
    }

    private fun generateRandomQuiz(category: String): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        
        when (category) {
            "HIRAGANA" -> {
                val pool = hiraganaList.shuffled()
                val size = minOf(5, pool.size)
                for (i in 0 until size) {
                    val target = pool[i]
                    // Determine question style: Character -> Pronunciation or Romaji -> Chars
                    val isCharToSound = (i % 2 == 0)
                    if (isCharToSound) {
                        val wrongAnswers = hiraganaList.filter { it.id != target.id }.map { "${it.romaji} (${it.nepaliPronunciation})" }.shuffled().take(3)
                        val choices = (wrongAnswers + "${target.romaji} (${target.nepaliPronunciation})").shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = target.char,
                                prompt = "यो हिरागाना अक्षरको उच्चारण कुन हो?",
                                options = choices,
                                correctIndex = choices.indexOf("${target.romaji} (${target.nepaliPronunciation})"),
                                explanation = "अक्षर: ${target.char} | उच्चारण: ${target.romaji} (${target.nepaliPronunciation})"
                            )
                        )
                    } else {
                        val wrongAnswers = hiraganaList.filter { it.id != target.id }.map { it.char }.shuffled().take(3)
                        val choices = (wrongAnswers + target.char).shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = "${target.romaji} (${target.nepaliPronunciation})",
                                prompt = "यो उच्चारणका लागि सही हिरागाना अक्षर कुन हो?",
                                options = choices,
                                correctIndex = choices.indexOf(target.char),
                                explanation = "उच्चारण: ${target.romaji} (${target.nepaliPronunciation}) -> अक्षर: ${target.char}"
                            )
                        )
                    }
                }
            }
            "KATAKANA" -> {
                val pool = katakanaList.shuffled()
                val size = minOf(5, pool.size)
                for (i in 0 until size) {
                    val target = pool[i]
                    val isCharToSound = (i % 2 == 0)
                    if (isCharToSound) {
                        val wrongAnswers = katakanaList.filter { it.id != target.id }.map { "${it.romaji} (${it.nepaliPronunciation})" }.shuffled().take(3)
                        val choices = (wrongAnswers + "${target.romaji} (${target.nepaliPronunciation})").shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = target.char,
                                prompt = "यो काताकाना अक्षरको सही उच्चारण रोज्नुहोस्:",
                                options = choices,
                                correctIndex = choices.indexOf("${target.romaji} (${target.nepaliPronunciation})"),
                                explanation = "अक्षर: ${target.char} | उच्चारण: ${target.romaji} (${target.nepaliPronunciation})"
                            )
                        )
                    } else {
                        val wrongAnswers = katakanaList.filter { it.id != target.id }.map { it.char }.shuffled().take(3)
                        val choices = (wrongAnswers + target.char).shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = "${target.romaji} (${target.nepaliPronunciation})",
                                prompt = "यो उच्चारण भएको सही काताकाना अक्षर कुन हो?",
                                options = choices,
                                correctIndex = choices.indexOf(target.char),
                                explanation = "उच्चारण: ${target.romaji} (${target.nepaliPronunciation}) हो भने अक्षर: ${target.char}"
                            )
                        )
                    }
                }
            }
            "KANJI" -> {
                val pool = kanjiList.shuffled()
                val size = minOf(5, pool.size)
                for (i in 0 until size) {
                    val target = pool[i]
                    val isCharToMeaning = (i % 2 == 0)
                    if (isCharToMeaning) {
                        val wrongAnswers = kanjiList.filter { it.id != target.id }.map { "${it.meaning} (${it.nepaliMeaning})" }.shuffled().take(3)
                        val choices = (wrongAnswers + "${target.meaning} (${target.nepaliMeaning})").shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = target.char,
                                prompt = "यो कान्जी (Kanji) को मतलव/नेपाली अनुवाद के हो?",
                                options = choices,
                                correctIndex = choices.indexOf("${target.meaning} (${target.nepaliMeaning})"),
                                explanation = "कान्जी: ${target.char} | अर्थ: ${target.meaning} (${target.nepaliMeaning}) | उच्चारण: ${target.romaji}"
                            )
                        )
                    } else {
                        val wrongAnswers = kanjiList.filter { it.id != target.id }.map { it.char }.shuffled().take(3)
                        val choices = (wrongAnswers + target.char).shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = "${target.meaning} (${target.nepaliMeaning})",
                                prompt = "यो अर्थ बुझाउने सही कान्जी (Kanji) रोजी पहिचान गर्नुहोस्:",
                                options = choices,
                                correctIndex = choices.indexOf(target.char),
                                explanation = "अर्थ: ${target.meaning} (${target.nepaliMeaning})का लागि कान्जी '${target.char}' हो।"
                            )
                        )
                    }
                }
            }
            else -> { // VOCABULARY
                val pool = vocabularyList.shuffled()
                val size = minOf(5, pool.size)
                for (i in 0 until size) {
                    val target = pool[i]
                    val isJpToNep = (i % 2 == 0)
                    if (isJpToNep) {
                        val wrongAnswers = vocabularyList.filter { it.jp != target.jp }.map { it.nepaliText }.shuffled().take(3)
                        val choices = (wrongAnswers + target.nepaliText).shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = target.jp,
                                prompt = "यो जापानी शब्दको नेपालीमा के अर्थ हुन्छ?",
                                options = choices,
                                correctIndex = choices.indexOf(target.nepaliText),
                                explanation = "शब्द: ${target.jp} (${target.romaji}) \nनेपाली: ${target.nepaliText} | उच्चारण: '${target.pronunciationNepali}'"
                            )
                        )
                    } else {
                        val wrongAnswers = vocabularyList.filter { it.jp != target.jp }.map { "${it.jp} (${it.romaji})" }.shuffled().take(3)
                        val choices = (wrongAnswers + "${target.jp} (${target.romaji})").shuffled()
                        questions.add(
                            QuizQuestion(
                                jpText = target.nepaliText,
                                prompt = "यो नेपाली अर्थका लागि सही जापानी शब्द कुन हो?",
                                options = choices,
                                correctIndex = choices.indexOf("${target.jp} (${target.romaji})"),
                                explanation = "अर्थ: ${target.nepaliText} -> जापानी शब्द: ${target.jp} (${target.romaji})"
                            )
                        )
                    }
                }
            }
        }
        return questions
    }
}
