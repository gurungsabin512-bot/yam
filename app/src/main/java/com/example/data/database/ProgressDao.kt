package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: UserStats)

    @Query("SELECT * FROM character_progress")
    fun getAllCharacterProgressFlow(): Flow<List<CharacterProgress>>

    @Query("SELECT * FROM character_progress WHERE charId = :charId LIMIT 1")
    suspend fun getCharacterProgress(charId: String): CharacterProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCharacterProgress(progress: CharacterProgress)

    @Query("SELECT * FROM quiz_history ORDER BY timestamp DESC")
    fun getQuizHistoryFlow(): Flow<List<QuizHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizHistory(history: QuizHistory)

    @Query("DELETE FROM user_stats")
    suspend fun clearUserStats()

    @Query("DELETE FROM character_progress")
    suspend fun clearCharacterProgress()

    @Query("DELETE FROM quiz_history")
    suspend fun clearQuizHistory()
}
