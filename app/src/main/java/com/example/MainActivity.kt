package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.models.Question
import com.example.data.repository.ProgressRepository
import com.example.data.repository.QuizRepository
import com.example.services.audio.SoundManager
import com.example.ui.navigation.Screen
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DifficultyScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.screens.ReviewScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WilayasScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var quizRepository: QuizRepository
    private lateinit var progressRepository: ProgressRepository
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        quizRepository = QuizRepository(applicationContext)
        progressRepository = ProgressRepository(applicationContext)
        soundManager = SoundManager(applicationContext)

        setContent {
            MyApplicationTheme {
                // Arabic RTL Layout direction provider
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .safeDrawingPadding()
                    ) {
                        BalotaAppContent(
                            quizRepository = quizRepository,
                            progressRepository = progressRepository,
                            soundManager = soundManager
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundManager.resumeMusic(progressRepository.progressFlow.value.musicEnabled)
    }

    override fun onPause() {
        super.onPause()
        soundManager.pauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun BalotaAppContent(
    quizRepository: QuizRepository,
    progressRepository: ProgressRepository,
    soundManager: SoundManager
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    val progress by progressRepository.progressFlow.collectAsState()

    LaunchedEffect(progress.musicEnabled) {
        soundManager.setMusicEnabled(progress.musicEnabled)
    }

    when (val screen = currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                onStartGame = {
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Home -> {
            HomeScreen(
                progress = progress,
                onPlayNow = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    val questions = quizRepository.getRandomQuiz(10)
                    currentScreen = Screen.Quiz(
                        modeTitle = "العب الآن 🎮",
                        questions = questions
                    )
                },
                onDailyChallenge = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    val questions = quizRepository.getDailyChallengeQuestions(5)
                    currentScreen = Screen.Quiz(
                        modeTitle = "تحدي اليوم الخارق ⚡",
                        questions = questions
                    )
                },
                onWilayasClick = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Wilayas
                },
                onCategoriesClick = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Categories
                },
                onDifficultyClick = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Difficulty
                },
                onAchievementsClick = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Achievements
                },
                onSettingsClick = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Settings
                }
            )
        }

        is Screen.Quiz -> {
            QuizScreen(
                modeTitle = screen.modeTitle,
                questions = screen.questions,
                soundManager = soundManager,
                soundEnabled = progress.soundEnabled,
                hapticsEnabled = progress.hapticsEnabled,
                timerEnabled = progress.timerEnabled,
                timerDurationSeconds = progress.timerDurationSeconds,
                onQuizFinish = { correctCount, incorrectCount, scoreEarned, userAnswers ->
                    val newlyUnlocked = progressRepository.recordQuizResult(
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        scoreEarned = scoreEarned,
                        wilayaRef = screen.wilayaRef
                    )
                    currentScreen = Screen.Results(
                        totalQuestions = screen.questions.size,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        scoreEarned = scoreEarned,
                        answeredQuestions = screen.questions,
                        userAnswers = userAnswers,
                        newlyUnlockedAchievements = newlyUnlocked,
                        modeTitle = screen.modeTitle
                    )
                },
                onExitQuiz = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Results -> {
            ResultsScreen(
                totalQuestions = screen.totalQuestions,
                correctCount = screen.correctCount,
                incorrectCount = screen.incorrectCount,
                scoreEarned = screen.scoreEarned,
                newlyUnlockedAchievements = screen.newlyUnlockedAchievements,
                modeTitle = screen.modeTitle,
                onPlayAgain = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    // Start fresh quiz of same type or random
                    val freshQuestions = quizRepository.getRandomQuiz(screen.totalQuestions)
                    currentScreen = Screen.Quiz(
                        modeTitle = screen.modeTitle,
                        questions = freshQuestions
                    )
                },
                onReviewQuestions = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Review(
                        questions = screen.answeredQuestions,
                        userAnswers = screen.userAnswers
                    )
                },
                onGoHome = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Review -> {
            ReviewScreen(
                questions = screen.questions,
                userAnswers = screen.userAnswers,
                onBack = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Wilayas -> {
            WilayasScreen(
                wilayas = quizRepository.getAllWilayas(),
                completedWilayas = progress.completedWilayas,
                onSelectWilaya = { wilaya ->
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    val questions = quizRepository.getQuestionsByWilaya(wilaya.fullRef)
                    currentScreen = Screen.Quiz(
                        modeTitle = "ولاية ${wilaya.name} 🇩🇿",
                        questions = questions,
                        wilayaRef = wilaya.fullRef
                    )
                },
                onBack = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Categories -> {
            CategoriesScreen(
                categoriesWithCounts = quizRepository.getCategoriesWithCounts(),
                onSelectCategory = { category ->
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    val questions = quizRepository.getQuestionsByCategory(category).shuffled().take(10)
                    currentScreen = Screen.Quiz(
                        modeTitle = category,
                        questions = questions
                    )
                },
                onBack = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Difficulty -> {
            val allQuestions = quizRepository.getAllQuestions()
            val easyCount = allQuestions.count { it.difficulty == "سهل" }
            val mediumCount = allQuestions.count { it.difficulty == "متوسط" }
            val hardCount = allQuestions.count { it.difficulty == "صعب" }

            DifficultyScreen(
                easyCount = easyCount,
                mediumCount = mediumCount,
                hardCount = hardCount,
                onSelectDifficulty = { diff ->
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    val questions = quizRepository.getQuestionsByDifficulty(diff).shuffled().take(10)
                    currentScreen = Screen.Quiz(
                        modeTitle = "مستوى $diff ⭐",
                        questions = questions
                    )
                },
                onBack = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Achievements -> {
            AchievementsScreen(
                achievements = progressRepository.getAchievements(),
                onBack = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Settings -> {
            SettingsScreen(
                progress = progress,
                onSoundToggle = { enabled ->
                    progressRepository.setSoundEnabled(enabled)
                },
                onMusicToggle = { enabled ->
                    progressRepository.setMusicEnabled(enabled)
                    soundManager.setMusicEnabled(enabled)
                },
                onHapticsToggle = { enabled ->
                    progressRepository.setHapticsEnabled(enabled)
                },
                onTimerToggle = { enabled ->
                    progressRepository.setTimerEnabled(enabled)
                },
                onResetProgress = {
                    progressRepository.resetProgress()
                },
                onBack = {
                    soundManager.playClick(progress.soundEnabled, progress.hapticsEnabled)
                    currentScreen = Screen.Home
                }
            )
        }
    }
}
