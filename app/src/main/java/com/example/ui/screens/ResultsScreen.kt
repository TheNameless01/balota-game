package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.Achievement
import com.example.ui.components.BalotaGameButton
import com.example.ui.components.BalotaHeader
import com.example.ui.components.BalotaStatBadge
import com.example.ui.theme.BalotaCreamBackground
import com.example.ui.theme.BalotaForestGreen
import com.example.ui.theme.BalotaForestGreenDark
import com.example.ui.theme.BalotaForestGreenLight
import com.example.ui.theme.BalotaGold
import com.example.ui.theme.BalotaWarmYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ResultsScreen(
    totalQuestions: Int,
    correctCount: Int,
    incorrectCount: Int,
    scoreEarned: Int,
    newlyUnlockedAchievements: List<Achievement>,
    modeTitle: String,
    onPlayAgain: () -> Unit,
    onReviewQuestions: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onGoHome()
    }

    val percentage = if (totalQuestions > 0) ((correctCount * 100) / totalQuestions) else 0

    val stars = when {
        percentage >= 90 -> 3
        percentage >= 60 -> 2
        percentage >= 30 -> 1
        else -> 0
    }

    val feedbackMessage = when {
        percentage == 100 -> "ما شاء الله! عبقرية جزائرية لا تضاهى! 🇩🇿👑"
        percentage >= 80 -> "أداء رائع جداً وممتاز يا بطل الجزائر! 🌟"
        percentage >= 50 -> "أحسنت صنعاً! معلوماتك تتطور باستمرار! 🚀"
        else -> "لا بأس يا بطل، كل محاولة تعلمنا أشياء جديدة وممتعة! 💡"
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("results_screen_root"),
        color = BalotaCreamBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BalotaHeader(
                title = "نتيجة الاختبار 🏆",
                onBackClick = onGoHome
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mascot & Congratulations Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.balota_logo),
                                contentDescription = "شعار بلوطة",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stars Display
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(3) { index ->
                                    val isEarned = index < stars
                                    Text(
                                        text = if (isEarned) "⭐" else "☆",
                                        fontSize = 32.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = feedbackMessage,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = BalotaForestGreenDark,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "اختبار: $modeTitle",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // Score Details Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "النقاط المحصلة:",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "+$scoreEarned نقطة ⭐",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BalotaWarmYellow
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                BalotaStatBadge(
                                    title = "نسبة النجاح",
                                    value = "$percentage%",
                                    icon = "🎯",
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BalotaStatBadge(
                                    title = "صحيحة",
                                    value = "$correctCount",
                                    icon = "✅",
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BalotaStatBadge(
                                    title = "خاطئة",
                                    value = "$incorrectCount",
                                    icon = "❌",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Newly Unlocked Achievements section (if any)
                if (newlyUnlockedAchievements.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "🎉 وسام جديد تم فتحه!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                newlyUnlockedAchievements.forEach { ach ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Text(text = ach.icon, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = ach.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF3E2723)
                                            )
                                            Text(
                                                text = ach.description,
                                                fontSize = 12.sp,
                                                color = Color(0xFF5D4037)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                item {
                    BalotaGameButton(
                        text = "العب مرة أخرى 🔄",
                        onClick = onPlayAgain,
                        gradientColors = listOf(BalotaForestGreenLight, BalotaForestGreen),
                        testTag = "play_again_button",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    BalotaGameButton(
                        text = "مراجعة الأسئلة والأجوبة 📖",
                        subtitle = "تعرف على الأجوبة الصحيحة ومعلومات بلوطة",
                        onClick = onReviewQuestions,
                        gradientColors = listOf(Color(0xFF0288D1), Color(0xFF01579B)),
                        testTag = "review_questions_button",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    BalotaGameButton(
                        text = "العودة للرئيسية 🏠",
                        onClick = onGoHome,
                        gradientColors = listOf(Color(0xFF8D6E63), Color(0xFF5D4037)),
                        testTag = "go_home_button",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
