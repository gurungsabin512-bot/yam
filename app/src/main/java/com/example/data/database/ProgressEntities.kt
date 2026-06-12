package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1, // Singleton row
    val stars: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val lastActiveMillis: Long = 0L,
    val badgesRaw: String = "" // Comma-separated badge IDs
) {
    fun getBadgesList(): List<String> {
        if (badgesRaw.isEmpty()) return emptyList()
        return badgesRaw.split(",")
    }
}

@Entity(tableName = "character_progress")
data class CharacterProgress(
    @PrimaryKey val charId: String, // e.g. "h_a", "kn_ichi"
    val type: String, // "HIRAGANA", "KATAKANA", "KANJI"
    val timesTraced: Int = 0,
    val isLearned: Boolean = false,
    val lastPracticedMillis: Long = 0L
)

@Entity(tableName = "quiz_history")
data class QuizHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // "HIRAGANA", "KATAKANA", "KANJI", "VOCABULARY"
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)
