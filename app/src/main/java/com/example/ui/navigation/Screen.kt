package com.example.ui.navigation

import com.example.data.models.Achievement
import com.example.data.models.Question

sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    data class Quiz(
        val modeTitle: String,
        val questions: List<Question>,
        val wilayaRef: String? = null
    ) : Screen()
    data class Results(
        val totalQuestions: Int,
        val correctCount: Int,
        val incorrectCount: Int,
        val scoreEarned: Int,
        val answeredQuestions: List<Question>,
        val userAnswers: Map<Int, String>,
        val newlyUnlockedAchievements: List<Achievement> = emptyList(),
        val modeTitle: String = "اختبار عام"
    ) : Screen()
    data class Review(
        val questions: List<Question>,
        val userAnswers: Map<Int, String>
    ) : Screen()
    object Wilayas : Screen()
    object Categories : Screen()
    object Difficulty : Screen()
    object Achievements : Screen()
    object Settings : Screen()
    object ManageQuestions : Screen()
}
