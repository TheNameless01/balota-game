package com.example.data.models

data class PlayerProgress(
    val totalScore: Int = 0,
    val completedQuizzes: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val playerLevel: Int = 1,
    val completedWilayas: Set<String> = emptySet(),
    val unlockedAchievementIds: Set<String> = emptySet(),
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val timerEnabled: Boolean = false,
    val timerDurationSeconds: Int = 20
) {
    val totalQuestionsAnswered: Int
        get() = correctAnswers + incorrectAnswers

    val accuracyPercentage: Int
        get() = if (totalQuestionsAnswered > 0) ((correctAnswers * 100) / totalQuestionsAnswered) else 0

    val levelTitle: String
        get() = when (playerLevel) {
            1 -> "مستكشف مبتدئ 🌱"
            2 -> "صديق بلوطة 🐿️"
            3 -> "باحث نشيط 🧭"
            4 -> "عاشق الجزائر 🇩🇿"
            5 -> "خبير التراث 🏛️"
            6 -> "بطل الولايات 🗺️"
            7 -> "عبقري المعالم ⭐"
            else -> "أسطورة الجزائر 👑"
        }
}
