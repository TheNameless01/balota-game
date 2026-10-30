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
import com.example.ui.theme.BalotaForestGreenDark
import com.example.ui.theme.BalotaForestGreenLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class CategoryMeta(
    val name: String,
    val iconEmoji: String,
    val description: String,
    val bgLightColor: Color,
    val accentColor: Color
)

@Composable
fun CategoriesScreen(
    categoriesWithCounts: Map<String, Int>,
    onSelectCategory: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val categoryMetaList = listOf(
        CategoryMeta(
            name = "الأطباق والمأكولات",
            iconEmoji = "🍲",
            description = "كسكسي، رشتة، شخشوخة، مقروط وحلويات رمضان الشهية!",
            bgLightColor = Color(0xFFFFF3E0),
            accentColor = Color(0xFFE65100)
        ),
        CategoryMeta(
            name = "الجغرافيا والولايات",
            iconEmoji = "🗺️",
            description = "أنهار، جبال، واحات، وفقاقير الصحراء والسهول العريقة",
            bgLightColor = Color(0xFFE8F5E9),
            accentColor = Color(0xFF2E7D32)
        ),
        CategoryMeta(
            name = "الطبيعة والمعالم",
            iconEmoji = "🌲",
            description = "طاسيلي ناجر، جبال الهقار، شواطئ البحر والمغارات العجيبة",
            bgLightColor = Color(0xFFE1F5FE),
            accentColor = Color(0xFF0277BD)
        ),
        CategoryMeta(
            name = "التاريخ والمعالم",
            iconEmoji = "🏛️",
            description = "تيمقاد، قلعة بني حماد، قصبة العاصمة، وقوس كاراكالا",
            bgLightColor = Color(0xFFF3E5F5),
            accentColor = Color(0xFF7B1FA2)
        ),
        CategoryMeta(
            name = "التقاليد والألبسة",
            iconEmoji = "👘",
            description = "الشدة التلمسانية، الكركو العاصمي، الجبة القبائلية والملاية",
            bgLightColor = Color(0xFFFCE4EC),
            accentColor = Color(0xFFC2185B)
        ),
        CategoryMeta(
            name = "الحرف والتقاليد",
            iconEmoji = "🎨",
            description = "زربية غرداية، فضة بني يني، النحاس، وخيول تيارت الأصيلة",
            bgLightColor = Color(0xFFFFF8E1),
            accentColor = Color(0xFFF57F17)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("categories_screen_root"),
        color = BalotaCreamBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BalotaHeader(
                title = "التصنيفات التراثية 📚",
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(categoryMetaList.size) { index ->
                    val meta = categoryMetaList[index]
                    val count = categoriesWithCounts[meta.name] ?: 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(3.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectCategory(meta.name) }
                            .testTag("category_card_${index}"),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(meta.bgLightColor, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = meta.iconEmoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = meta.name,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(meta.bgLightColor, RoundedCornerShape(10.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "$count سؤال",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = meta.accentColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = meta.description,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(BalotaForestGreenLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "العب",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
