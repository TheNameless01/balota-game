package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Question
import com.example.services.audio.SoundManager
import com.example.ui.components.BalotaGameButton
import com.example.ui.components.BalotaHeader
import com.example.ui.theme.BalotaAcornBrown
import com.example.ui.theme.BalotaCreamBackground
import com.example.ui.theme.BalotaForestGreen
import com.example.ui.theme.BalotaForestGreenDark
import com.example.ui.theme.BalotaForestGreenLight
import com.example.ui.theme.BalotaGold
import com.example.ui.theme.BalotaIncorrectRed
import com.example.ui.theme.BalotaWarmYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    modeTitle: String,
    questions: List<Question>,
    soundManager: SoundManager,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    timerEnabled: Boolean,
    timerDurationSeconds: Int,
    onQuizFinish: (
        correctCount: Int,
        incorrectCount: Int,
        scoreEarned: Int,
        userAnswers: Map<Int, String>
    ) -> Unit,
    onExitQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (questions.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "لا توجد أسئلة متاحة حالياً.")
        }
        return
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }
    var incorrectCount by remember { mutableIntStateOf(0) }
    var scoreEarned by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableMapOf<Int, String>() }

    var timeRemaining by remember { mutableIntStateOf(timerDurationSeconds) }
    var showExitDialog by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentIndex]

    BackHandler {
        showExitDialog = true
    }

    // Timer logic if enabled
    LaunchedEffect(currentIndex, isSubmitted, timerEnabled) {
        if (timerEnabled && !isSubmitted) {
            timeRemaining = timerDurationSeconds
            while (timeRemaining > 0 && !isSubmitted) {
                delay(1000)
                timeRemaining -= 1
            }
            if (timeRemaining == 0 && !isSubmitted) {
                // Time up! Auto submit as incorrect
                isSubmitted = true
                selectedOption = ""
                userAnswers[currentQuestion.id] = ""
                incorrectCount++
                soundManager.playIncorrect(soundEnabled, hapticsEnabled)
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = {
                Text(
                    text = "هل تريد الخروج من اللعبة؟",
                    fontWeight = FontWeight.Bold,
                    color = BalotaForestGreenDark
                )
            },
            text = {
                Text(text = "ستفقد تقدمك في هذا الاختبار إذا خرجت الآن.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        onExitQuiz()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BalotaIncorrectRed)
                ) {
                    Text("نعم، خروج")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExitDialog = false }) {
                    Text("إكمال اللعب")
                }
            }
        )
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("quiz_screen_root"),
        color = BalotaCreamBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            BalotaHeader(
                title = modeTitle,
                onBackClick = { showExitDialog = true },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${currentIndex + 1} / ${questions.size}",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            // Progress bar
            val progressFraction = (currentIndex + 1).toFloat() / questions.size
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = BalotaWarmYellow,
                trackColor = Color(0xFFC8E6C9)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Info Badges: Category, Wilaya, Difficulty, Timer
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category Tag
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentQuestion.category,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BalotaForestGreenDark
                                )
                            }

                            // Wilaya Tag
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFF3E0), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentQuestion.wilayaName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }

                            // Difficulty Tag
                            val diffColor = when (currentQuestion.difficulty) {
                                "سهل" -> Color(0xFF43A047)
                                "متوسط" -> Color(0xFFFB8C00)
                                else -> Color(0xFFE53935)
                            }
                            Box(
                                modifier = Modifier
                                    .background(diffColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = currentQuestion.difficulty,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = diffColor
                                )
                            }
                        }

                        if (timerEnabled) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        if (timeRemaining <= 5) Color(0xFFFFEBEE) else Color(0xFFE1F5FE),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (timeRemaining <= 5) BalotaIncorrectRed else BalotaForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${timeRemaining}ث",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (timeRemaining <= 5) BalotaIncorrectRed else BalotaForestGreen
                                )
                            }
                        }
                    }
                }

                // Main Question Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "السؤال ${currentIndex + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BalotaWarmYellow
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentQuestion.question,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )
                        }
                    }
                }

                // Four Options (Randomized order so correct answer is randomly distributed)
                item {
                    val shuffledOptions = remember(currentQuestion.id) {
                        currentQuestion.options.shuffled()
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        shuffledOptions.forEachIndexed { optIndex, optionText ->
                            val isSelected = selectedOption == optionText
                            val isCorrectAnswer = currentQuestion.isOptionCorrect(optionText)

                            val backgroundColor by animateColorAsState(
                                targetValue = when {
                                    !isSubmitted -> if (isSelected) Color(0xFFE8F5E9) else Color.White
                                    isCorrectAnswer -> Color(0xFFD4EDDA) // soft light green
                                    isSelected && !isCorrectAnswer -> Color(0xFFF8D7DA) // soft light red
                                    else -> Color.White.copy(alpha = 0.7f)
                                },
                                animationSpec = tween(250),
                                label = "option_bg"
                            )

                            val borderColor = when {
                                !isSubmitted -> if (isSelected) BalotaForestGreen else Color(0xFFE0E0E0)
                                isCorrectAnswer -> Color(0xFF28A745)
                                isSelected && !isCorrectAnswer -> Color(0xFFDC3545)
                                else -> Color(0xFFE0E0E0)
                            }

                            val letterPrefix = when (optIndex) {
                                0 -> "أ"
                                1 -> "ب"
                                2 -> "ج"
                                else -> "د"
                            }

                            val optionScale by animateFloatAsState(
                                targetValue = if (isSelected && isSubmitted) 1.035f else 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "option_scale"
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .scale(optionScale)
                                    .shadow(if (isSelected || (isSubmitted && isCorrectAnswer)) 6.dp else 1.dp, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(16.dp))
                                    .clickable(enabled = !isSubmitted) {
                                        selectedOption = optionText
                                        isSubmitted = true
                                        val isRight = currentQuestion.isOptionCorrect(optionText)
                                        userAnswers[currentQuestion.id] = optionText

                                        if (isRight) {
                                            correctCount++
                                            scoreEarned += 10
                                            soundManager.playCorrect(soundEnabled, hapticsEnabled)
                                        } else {
                                            incorrectCount++
                                            soundManager.playIncorrect(soundEnabled, hapticsEnabled)
                                        }
                                    }
                                    .testTag("quiz_option_${optIndex}"),
                                colors = CardDefaults.cardColors(containerColor = backgroundColor)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Option Letter badge
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                when {
                                                    isSubmitted && isCorrectAnswer -> Color(0xFF28A745)
                                                    isSubmitted && isSelected -> Color(0xFFDC3545)
                                                    else -> Color(0xFFF0F0F0)
                                                },
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSubmitted && isCorrectAnswer) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else if (isSubmitted && isSelected && !isCorrectAnswer) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            Text(
                                                text = letterPrefix,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = BalotaForestGreenDark
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Text(
                                        text = optionText,
                                        fontSize = 16.sp,
                                        fontWeight = if (isSelected || (isSubmitted && isCorrectAnswer)) FontWeight.Bold else FontWeight.Medium,
                                        color = TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Immediate Motivational & Fun Fact Box (Only visible after answering)
                if (isSubmitted) {
                    item {
                        val isUserCorrect = selectedOption?.let { currentQuestion.isOptionCorrect(it) } == true
                        val encouragement = if (isUserCorrect) {
                            "أحسنت يا بطل! إجابة صحيحة وذكية 🌟🇩🇿"
                        } else {
                            "لا بأس يا بطل، تعلمنا معلومة جديدة وقيمة! 💡"
                        }

                        AnimatedVisibility(
                            visible = isSubmitted,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(18.dp)),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUserCorrect) Color(0xFFE8F5E9) else Color(0xFFFFF8E1)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = encouragement,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUserCorrect) BalotaForestGreenDark else Color(0xFF8D6E63)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text(text = "💡", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "معلومة بلوطة الذهبية:",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = BalotaForestGreenDark
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = currentQuestion.funFact,
                                                fontSize = 13.sp,
                                                color = TextPrimary,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Next / Finish Button
                    item {
                        val isLastQuestion = currentIndex == questions.size - 1
                        val nextButtonText = if (isLastQuestion) "عرض النتيجة النهائية 🏆" else "السؤال التالي ➡️"

                        BalotaGameButton(
                            text = nextButtonText,
                            onClick = {
                                soundManager.playClick(soundEnabled, hapticsEnabled)
                                if (isLastQuestion) {
                                    soundManager.playQuizComplete(soundEnabled, hapticsEnabled)
                                    onQuizFinish(correctCount, incorrectCount, scoreEarned, userAnswers)
                                } else {
                                    currentIndex++
                                    selectedOption = null
                                    isSubmitted = false
                                }
                            },
                            gradientColors = listOf(BalotaForestGreenLight, BalotaForestGreen),
                            testTag = "quiz_next_button",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
