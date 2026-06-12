package com.example.data.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class ProgressRepository(private val progressDao: ProgressDao) {

    val userStatsFlow: Flow<UserStats> = progressDao.getUserStatsFlow().map { it ?: UserStats() }
    val allCharacterProgressFlow: Flow<List<CharacterProgress>> = progressDao.getAllCharacterProgressFlow()
    val quizHistoryFlow: Flow<List<QuizHistory>> = progressDao.getQuizHistoryFlow()

    suspend fun getOrCreateUserStats(): UserStats {
        val stats = progressDao.getUserStats()
        if (stats == null) {
            val fresh = UserStats()
            progressDao.insertOrUpdateUserStats(fresh)
            return fresh
        }
        return stats
    }

    suspend fun recordTracingSuccess(charId: String, type: String, starReward: Int = 10) {
        // 1. Update Character Progress
        val oldProgress = progressDao.getCharacterProgress(charId)
        val newProgress = CharacterProgress(
            charId = charId,
            type = type,
            timesTraced = (oldProgress?.timesTraced ?: 0) + 1,
            isLearned = true,
            lastPracticedMillis = System.currentTimeMillis()
        )
        progressDao.insertOrUpdateCharacterProgress(newProgress)

        // 2. Award Stars and calculate Level / Badges
        val oldStats = getOrCreateUserStats()
        val newStars = oldStats.stars + starReward
        val calculatedLevel = 1 + (newStars / 100) // 100 stars per level

        // Process badges
        val currentBadges = oldStats.getBadgesList().toMutableSet()
        
        // Award "First Step"
        if (currentBadges.add("First Step")) {
            // First character ever traced
        }

        // Star-based unlocks
        if (newStars >= 50) currentBadges.add("Beginner Explorer")
        if (newStars >= 150) currentBadges.add("Determined Learner")
        if (newStars >= 300) currentBadges.add("Hiragana Master")
        if (newStars >= 500) currentBadges.add("Kanji Sage")

        val badgeString = currentBadges.joinToString(",")

        val updatedStats = oldStats.copy(
            stars = newStars,
            level = calculatedLevel,
            badgesRaw = badgeString
        )
        progressDao.insertOrUpdateUserStats(updatedStats)
    }

    suspend fun recordQuizScore(category: String, score: Int, total: Int) {
        // 1. Insert History
        val history = QuizHistory(
            category = category,
            score = score,
            totalQuestions = total
        )
        progressDao.insertQuizHistory(history)

        // 2. Award Stars (15 per correct answer, +20 bonus for perfect score)
        val percent = if (total > 0) (score.toFloat() / total * 100).toInt() else 0
        var reward = score * 15
        if (percent == 100) reward += 25

        val oldStats = getOrCreateUserStats()
        val newStars = oldStats.stars + reward
        val calculatedLevel = 1 + (newStars / 100)

        // Process badges
        val currentBadges = oldStats.getBadgesList().toMutableSet()
        if (percent == 100) {
            currentBadges.add("Perfect Score")
        }
        if (newStars >= 50) currentBadges.add("Beginner Explorer")
        if (newStars >= 150) currentBadges.add("Determined Learner")
        if (newStars >= 300) currentBadges.add("Hiragana Master")
        if (newStars >= 500) currentBadges.add("Kanji Sage")

        val badgeString = currentBadges.joinToString(",")

        val updatedStats = oldStats.copy(
            stars = newStars,
            level = calculatedLevel,
            badgesRaw = badgeString
        )
        progressDao.insertOrUpdateUserStats(updatedStats)
    }

    suspend fun updateStreak() {
        val oldStats = getOrCreateUserStats()
        val now = System.currentTimeMillis()
        val lastActive = oldStats.lastActiveMillis

        if (lastActive == 0L) {
            // First time active
            progressDao.insertOrUpdateUserStats(oldStats.copy(
                currentStreak = 1,
                lastActiveMillis = now
            ))
            return
        }

        val diffMillis = now - lastActive
        val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

        val newStreak = when {
            diffDays == 0L -> {
                // Same day, streak remains identical
                oldStats.currentStreak
            }
            diffDays == 1L -> {
                // Consecutive active day! Streak increments!
                val increased = oldStats.currentStreak + 1
                increased
            }
            else -> {
                // Broke streak
                1
            }
        }

        val currentBadges = oldStats.getBadgesList().toMutableSet()
        if (newStreak >= 3) {
            currentBadges.add("Three Day Flame")
        }

        progressDao.insertOrUpdateUserStats(oldStats.copy(
            currentStreak = newStreak,
            lastActiveMillis = now,
            badgesRaw = currentBadges.joinToString(",")
        ))
    }

    suspend fun clearAllData() {
        progressDao.clearUserStats()
        progressDao.clearCharacterProgress()
        progressDao.clearQuizHistory()
        getOrCreateUserStats()
    }
}
