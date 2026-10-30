package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.Achievement
import com.example.data.models.PlayerProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("balota_progress_prefs", Context.MODE_PRIVATE)

    private val _progressFlow = MutableStateFlow(loadProgress())
    val progressFlow: StateFlow<PlayerProgress> = _progressFlow.asStateFlow()

    private fun loadProgress(): PlayerProgress {
        val totalScore = prefs.getInt("total_score", 0)
        val completedQuizzes = prefs.getInt("completed_quizzes", 0)
        val correctAnswers = prefs.getInt("correct_answers", 0)
        val incorrectAnswers = prefs.getInt("incorrect_answers", 0)
        val playerLevel = prefs.getInt("player_level", 1)
        val completedWilayas = prefs.getStringSet("completed_wilayas", emptySet()) ?: emptySet()
        val unlockedAchievements = prefs.getStringSet("unlocked_achievements", emptySet()) ?: emptySet()
        val soundEnabled = prefs.getBoolean("sound_enabled", true)
        val hapticsEnabled = prefs.getBoolean("haptics_enabled", true)
        val timerEnabled = prefs.getBoolean("timer_enabled", false)
        val timerDuration = prefs.getInt("timer_duration", 20)

        return PlayerProgress(
            totalScore = totalScore,
            completedQuizzes = completedQuizzes,
            correctAnswers = correctAnswers,
            incorrectAnswers = incorrectAnswers,
            playerLevel = playerLevel,
            completedWilayas = completedWilayas,
            unlockedAchievementIds = unlockedAchievements,
            soundEnabled = soundEnabled,
            hapticsEnabled = hapticsEnabled,
            timerEnabled = timerEnabled,
            timerDurationSeconds = timerDuration
        )
    }

    fun recordQuizResult(
        correctCount: Int,
        incorrectCount: Int,
        scoreEarned: Int,
        wilayaRef: String? = null
    ): List<Achievement> {
        val current = _progressFlow.value
        val newCorrect = current.correctAnswers + correctCount
        val newIncorrect = current.incorrectAnswers + incorrectCount
        val newScore = current.totalScore + scoreEarned
        val newQuizzes = current.completedQuizzes + 1

        // Calculate Level: 100 points per level up
        val newLevel = (newScore / 100) + 1

        val newWilayas = current.completedWilayas.toMutableSet()
        if (wilayaRef != null && correctCount > 0) {
            newWilayas.add(wilayaRef)
        }

        // Check new achievements
        val newlyUnlocked = mutableListOf<Achievement>()
        val unlockedIds = current.unlockedAchievementIds.toMutableSet()

        val allAchievements = getAchievementsList(newCorrect, newQuizzes, newWilayas.size, newLevel, correctCount, incorrectCount)
        for (ach in allAchievements) {
            if (ach.isUnlocked && !unlockedIds.contains(ach.id)) {
                unlockedIds.add(ach.id)
                newlyUnlocked.add(ach)
            }
        }

        prefs.edit()
            .putInt("total_score", newScore)
            .putInt("completed_quizzes", newQuizzes)
            .putInt("correct_answers", newCorrect)
            .putInt("incorrect_answers", newIncorrect)
            .putInt("player_level", newLevel)
            .putStringSet("completed_wilayas", newWilayas)
            .putStringSet("unlocked_achievements", unlockedIds)
            .apply()

        _progressFlow.value = current.copy(
            totalScore = newScore,
            completedQuizzes = newQuizzes,
            correctAnswers = newCorrect,
            incorrectAnswers = newIncorrect,
            playerLevel = newLevel,
            completedWilayas = newWilayas,
            unlockedAchievementIds = unlockedIds
        )

        return newlyUnlocked
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
        _progressFlow.value = _progressFlow.value.copy(soundEnabled = enabled)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        _progressFlow.value = _progressFlow.value.copy(hapticsEnabled = enabled)
    }

    fun setTimerEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("timer_enabled", enabled).apply()
        _progressFlow.value = _progressFlow.value.copy(timerEnabled = enabled)
    }

    fun setTimerDuration(seconds: Int) {
        prefs.edit().putInt("timer_duration", seconds).apply()
        _progressFlow.value = _progressFlow.value.copy(timerDurationSeconds = seconds)
    }

    fun resetProgress() {
        prefs.edit()
            .putInt("total_score", 0)
            .putInt("completed_quizzes", 0)
            .putInt("correct_answers", 0)
            .putInt("incorrect_answers", 0)
            .putInt("player_level", 1)
            .putStringSet("completed_wilayas", emptySet())
            .putStringSet("unlocked_achievements", emptySet())
            .apply()

        _progressFlow.value = PlayerProgress(
            soundEnabled = _progressFlow.value.soundEnabled,
            hapticsEnabled = _progressFlow.value.hapticsEnabled,
            timerEnabled = _progressFlow.value.timerEnabled,
            timerDurationSeconds = _progressFlow.value.timerDurationSeconds
        )
    }

    fun getAchievements(): List<Achievement> {
        val p = _progressFlow.value
        return getAchievementsList(p.correctAnswers, p.completedQuizzes, p.completedWilayas.size, p.playerLevel, 0, 0)
    }

    private fun getAchievementsList(
        correct: Int,
        quizzes: Int,
        wilayasCount: Int,
        level: Int,
        lastQuizCorrect: Int,
        lastQuizIncorrect: Int
    ): List<Achievement> {
        val unlockedIds = _progressFlow.value.unlockedAchievementIds

        return listOf(
            Achievement(
                id = "first_correct",
                title = "أول إجابة صحيحة ⭐",
                description = "أجبت على أول سؤال بشكل صحيح!",
                icon = "🎯",
                currentProgress = correct.coerceAtMost(1),
                maxProgress = 1,
                isUnlocked = correct >= 1 || unlockedIds.contains("first_correct")
            ),
            Achievement(
                id = "first_quiz",
                title = "أول اختبار مكتمل 📜",
                description = "أكملت أول جولة أسئلة مع بلوطة!",
                icon = "🎉",
                currentProgress = quizzes.coerceAtMost(1),
                maxProgress = 1,
                isUnlocked = quizzes >= 1 || unlockedIds.contains("first_quiz")
            ),
            Achievement(
                id = "10_correct",
                title = "10 إجابات صحيحة 💡",
                description = "أجبت على 10 أسئلة بشكل صحيح!",
                icon = "🌟",
                currentProgress = correct.coerceAtMost(10),
                maxProgress = 10,
                isUnlocked = correct >= 10 || unlockedIds.contains("10_correct")
            ),
            Achievement(
                id = "50_correct",
                title = "50 إجابة صحيحة 🔥",
                description = "أنت في طريقك لتصبح عبقري المعالم!",
                icon = "🏆",
                currentProgress = correct.coerceAtMost(50),
                maxProgress = 50,
                isUnlocked = correct >= 50 || unlockedIds.contains("50_correct")
            ),
            Achievement(
                id = "5_quizzes",
                title = "5 اختبارات مكتملة 🏅",
                description = "أكملت 5 جولات كاملة بنجاح!",
                icon = "🎖️",
                currentProgress = quizzes.coerceAtMost(5),
                maxProgress = 5,
                isUnlocked = quizzes >= 5 || unlockedIds.contains("5_quizzes")
            ),
            Achievement(
                id = "explorer_wilayas",
                title = "مستكشف الولايات 🗺️",
                description = "أجبت على أسئلة في 5 ولايات جزائرية مختلفة!",
                icon = "🧭",
                currentProgress = wilayasCount.coerceAtMost(5),
                maxProgress = 5,
                isUnlocked = wilayasCount >= 5 || unlockedIds.contains("explorer_wilayas")
            ),
            Achievement(
                id = "perfect_score",
                title = "العلامة الكاملة 💯",
                description = "حققت 100% في جولة أسئلة!",
                icon = "👑",
                currentProgress = if (unlockedIds.contains("perfect_score") || (lastQuizCorrect >= 5 && lastQuizIncorrect == 0)) 1 else 0,
                maxProgress = 1,
                isUnlocked = unlockedIds.contains("perfect_score") || (lastQuizCorrect >= 5 && lastQuizIncorrect == 0)
            ),
            Achievement(
                id = "champion",
                title = "بطل التحديات 🇩🇿",
                description = "وصلت إلى المستوى 5 وتجاوزت 400 نقطة!",
                icon = "🛡️",
                currentProgress = level.coerceAtMost(5),
                maxProgress = 5,
                isUnlocked = level >= 5 || unlockedIds.contains("champion")
            )
        )
    }
}
