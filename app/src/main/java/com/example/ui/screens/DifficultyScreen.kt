package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BalotaHeader
import com.example.ui.theme.BalotaCreamBackground
import com.example.ui.theme.BalotaForestGreen
import com.example.ui.theme.BalotaForestGreenLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class DifficultyOption(
    val levelName: String,
    val stars: String,
    val description: String,
    val questionCount: Int,
    val accentColor: Color,
    val bgLightColor: Color
)

@Composable
fun DifficultyScreen(
    easyCount: Int,
    mediumCount: Int,
    hardCount: Int,
    onSelectDifficulty: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val levels = listOf(
        DifficultyOption(
            levelName = "سهل",
            stars = "⭐☆☆",
            description = "أسئلة ممتعة وبسيطة تناسب المبتدئين والأطفال الصغار",
            questionCount = easyCount,
            accentColor = Color(0xFF2E7D32),
            bgLightColor = Color(0xFFE8F5E9)
        ),
        DifficultyOption(
            levelName = "متوسط",
            stars = "⭐⭐☆",
            description = "تحديات ثقافية متوازنة لاختبار معلوماتك الجزائرية",
            questionCount = mediumCount,
            accentColor = Color(0xFFF57C00),
            bgLightColor = Color(0xFFFFF3E0)
        ),
        DifficultyOption(
            levelName = "صعب",
            stars = "⭐⭐⭐",
            description = "ألغاز تاريخية وتراثية عميقة للخبراء والعباقرة الصغار!",
            questionCount = hardCount,
            accentColor = Color(0xFFD32F2F),
            bgLightColor = Color(0xFFFFEBEE)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("difficulty_screen_root"),
        color = BalotaCreamBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BalotaHeader(
                title = "مستويات الصعوبة ⭐",
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(levels.size) { index ->
                    val item = levels[index]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { onSelectDifficulty(item.levelName) }
                            .testTag("difficulty_card_${item.levelName}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .background(item.bgLightColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = item.stars, fontSize = 16.sp)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "مستوى ${item.levelName}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = item.accentColor
                                        )
                                        Text(
                                            text = "${item.questionCount} سؤال متاح",
                                            fontSize = 12.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(BalotaForestGreenLight, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "ابدأ",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = item.description,
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
}
