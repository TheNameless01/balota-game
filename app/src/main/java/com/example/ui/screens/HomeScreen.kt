package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.PlayerProgress
import com.example.ui.components.BalotaGameButton
import com.example.ui.components.BalotaMascotGreeting
import com.example.ui.components.BalotaStatBadge
import com.example.ui.theme.BalotaAcornBrown
import com.example.ui.theme.BalotaCreamBackground
import com.example.ui.theme.BalotaForestGreen
import com.example.ui.theme.BalotaForestGreenDark
import com.example.ui.theme.BalotaForestGreenLight
import com.example.ui.theme.BalotaGold
import com.example.ui.theme.BalotaSkyBlue
import com.example.ui.theme.BalotaWarmYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    progress: PlayerProgress,
    onPlayNow: () -> Unit,
    onDailyChallenge: () -> Unit,
    onWilayasClick: () -> Unit,
    onCategoriesClick: () -> Unit,
    onDifficultyClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_root"),
        color = BalotaCreamBackground
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top App Bar with Mascot Title and Settings button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.balota_logo),
                            contentDescription = "شعار بلوطة",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "بلوطة — Balota",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BalotaForestGreenDark
                            )
                            Text(
                                text = "مستكشف الجزائر الصغير 🇩🇿",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .testTag("home_settings_button")
                            .size(44.dp)
                            .background(Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            tint = BalotaForestGreen
                        )
                    }
                }
            }

            // Balota Mascot Greeting
            item {
                BalotaMascotGreeting(
                    greeting = "مرحباً بك في بلوطة! أنا صديقك بلوطة، هل أنت جاهز لاكتشاف أسرار الجزائر الحبيبة معي؟ 🇩🇿✨",
                    subMessage = "أجب على الأسئلة واجمع النقاط والنجوم وافتح شارات الولايات!"
                )
            }

            // Player Stats Bar (Score, Level, Accuracy)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = progress.levelTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BalotaForestGreenDark
                                )
                                Text(
                                    text = "المستوى ${progress.playerLevel}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${progress.totalScore}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BalotaWarmYellow
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "نقطة ⭐", fontSize = 13.sp, color = BalotaForestGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Level progress bar
                        val scoreInCurrentLevel = progress.totalScore % 100
                        LinearProgressIndicator(
                            progress = { (scoreInCurrentLevel / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = BalotaWarmYellow,
                            trackColor = Color(0xFFEEEEEE),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BalotaStatBadge(
                                title = "إجابة صحيحة",
                                value = "${progress.correctAnswers}",
                                icon = "✅",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BalotaStatBadge(
                                title = "اختبارات",
                                value = "${progress.completedQuizzes}",
                                icon = "📜",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BalotaStatBadge(
                                title = "ولايات مكتشفة",
                                value = "${progress.completedWilayas.size} / 58",
                                icon = "🗺️",
                                modifier = Modifier.weight(1.2f)
                            )
                        }
                    }
                }
            }

            // PRIMARY BUTTON: PLAY NOW (العب الآن)
            item {
                BalotaGameButton(
                    text = "🎮 العب الآن",
                    subtitle = "10 أسئلة ممتعة ومنوعة من كل ربوع الوطن",
                    onClick = onPlayNow,
                    gradientColors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)),
                    testTag = "play_now_button",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // DAILY CHALLENGE (تحدي اليوم)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onDailyChallenge)
                        .testTag("daily_challenge_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "🌟", fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "تحدي اليوم الخارق ⚡",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF5D4037)
                                    )
                                    Text(
                                        text = "5 أسئلة يومية خاصة لاختبار ذكائك وربح نقاط إضافية!",
                                        fontSize = 12.sp,
                                        color = Color(0xFF6D4C41),
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = BalotaWarmYellow,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            // 4 EXPLORATION TILES (حسب الولاية، التصنيفات، مستويات الصعوبة، إنجازاتي)
            item {
                Text(
                    text = "أقسام اللعبة والاستكشاف 🧭",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = BalotaForestGreenDark,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // حسب الولاية
                    HomeTile(
                        title = "حسب الولاية",
                        subtitle = "58 ولاية جزائرية",
                        emoji = "🗺️",
                        backgroundColor = Color(0xFFE8F5E9),
                        textColor = BalotaForestGreenDark,
                        onClick = onWilayasClick,
                        modifier = Modifier.weight(1f),
                        testTag = "wilayas_button"
                    )

                    // التصنيفات
                    HomeTile(
                        title = "التصنيفات",
                        subtitle = "أطباق، تاريخ، معالم...",
                        emoji = "📚",
                        backgroundColor = Color(0xFFFFF3E0),
                        textColor = Color(0xFFE65100),
                        onClick = onCategoriesClick,
                        modifier = Modifier.weight(1f),
                        testTag = "categories_button"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // مستويات الصعوبة
                    HomeTile(
                        title = "مستويات الصعوبة",
                        subtitle = "سهل، متوسط، صعب",
                        emoji = "⭐",
                        backgroundColor = Color(0xFFE1F5FE),
                        textColor = Color(0xFF0277BD),
                        onClick = onDifficultyClick,
                        modifier = Modifier.weight(1f),
                        testTag = "difficulty_button"
                    )

                    // إنجازاتي
                    HomeTile(
                        title = "إنجازاتي",
                        subtitle = "الأوسمة والشارات",
                        emoji = "🏆",
                        backgroundColor = Color(0xFFF3E5F5),
                        textColor = Color(0xFF6A1B9A),
                        onClick = onAchievementsClick,
                        modifier = Modifier.weight(1f),
                        testTag = "achievements_button"
                    )
                }
            }

            // Friendly Balota Motivational tip at bottom
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "هل تعلم؟ الجزائر هي أكبر بلد في إفريقيا والعالم العربي من حيث المساحة! 🇩🇿",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HomeTile(
    title: String,
    subtitle: String,
    emoji: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Card(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = textColor.copy(alpha = 0.75f)
            )
        }
    }
}
