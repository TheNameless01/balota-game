package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.QuizRepository
import com.example.services.audio.SoundManager
import com.example.ui.components.BalotaGameButton
import com.example.ui.components.BalotaHeader
import com.example.ui.theme.BalotaCreamBackground
import com.example.ui.theme.BalotaForestGreen
import com.example.ui.theme.BalotaForestGreenDark
import com.example.ui.theme.BalotaForestGreenLight
import com.example.ui.theme.BalotaIncorrectRed
import com.example.ui.theme.BalotaWarmYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageQuestionsScreen(
    quizRepository: QuizRepository,
    soundManager: SoundManager,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var currentQuestionCount by remember { mutableIntStateOf(quizRepository.getQuestionCount()) }
    var isCustomPack by remember { mutableStateOf(quizRepository.isUsingCustomQuestions()) }

    var syncUrl by remember { mutableStateOf("https://raw.githubusercontent.com/omarmoussaoui763/balota-questions/main/questions.json") }
    var isSyncing by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isStatusSuccess by remember { mutableStateOf(true) }

    // Manual Question Fields
    var newQuestionText by remember { mutableStateOf("") }
    var newCorrectAnswer by remember { mutableStateOf("") }
    var newWrong1 by remember { mutableStateOf("") }
    var newWrong2 by remember { mutableStateOf("") }
    var newWrong3 by remember { mutableStateOf("") }
    var newFunFact by remember { mutableStateOf("") }

    val categories = listOf(
        "الأطباق والمأكولات",
        "الجغرافيا والولايات",
        "الطبيعة والمعالم",
        "التاريخ والمعالم",
        "التقاليد والألبسة",
        "الحرف والتقاليد"
    )
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val difficulties = listOf("سهل", "متوسط", "صعب")
    var selectedDifficulty by remember { mutableStateOf(difficulties[0]) }

    var selectedWilayaRef by remember { mutableStateOf("16 - الجزائر العاصمة") }

    // JSON Raw Paste Field
    var rawJsonInput by remember { mutableStateOf("") }

    var showResetDialog by remember { mutableStateOf(false) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "استعادة الأسئلة الأصلية؟",
                    fontWeight = FontWeight.Bold,
                    color = BalotaIncorrectRed
                )
            },
            text = {
                Text("سيتم حذف أي أسئلة مضافة أو محدثة واستعادة حزمة الـ 100 سؤال الأصلية المرفقة مع اللعبة.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        quizRepository.resetToDefaultQuestions()
                        currentQuestionCount = quizRepository.getQuestionCount()
                        isCustomPack = false
                        statusMessage = "تمت استعادة حزمة الأسئلة الأصلية (100 سؤال) بنجاح!"
                        isStatusSuccess = true
                        soundManager.playClick(soundEnabled, hapticsEnabled)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BalotaIncorrectRed)
                ) {
                    Text("نعم، استعادة")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("manage_questions_screen_root"),
        color = BalotaCreamBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            BalotaHeader(
                title = "تحديث وإدارة الأسئلة 📥",
                onBackClick = onBack
            )

            // Current Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "بنك الأسئلة المتاح حالياً",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "$currentQuestionCount سؤال متاح 📚",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BalotaForestGreenDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (isCustomPack) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isCustomPack) "حزمة محدثة 🔄" else "الحزمة الأصلية 📦",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomPack) BalotaForestGreenDark else Color(0xFFE65100)
                        )
                    }
                }
            }

            // Tabs: 0: Online OTA, 1: Add Question, 2: Import JSON
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = BalotaForestGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = BalotaForestGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("🌐 تحديث عبر الرابط", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("➕ إضافة سؤال", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("📄 استيراد JSON", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            // Status feedback banner
            if (statusMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isStatusSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = if (isStatusSuccess) "✅" else "⚠️", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusMessage ?: "",
                            fontSize = 13.sp,
                            color = if (isStatusSuccess) BalotaForestGreenDark else BalotaIncorrectRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // TAB 0: ONLINE OTA SYNC
                if (selectedTabIndex == 0) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "تحديث الأسئلة عبر الإنترنت (Over-the-Air) 🚀",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BalotaForestGreenDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "يمكنك تحديث وإضافة أسئلة جديدة تلقائياً دون إعادة تثبيت التطبيق! فقط ارفع ملف JSON إلى GitHub أو خادمك وضع الرابط أدناه:",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = syncUrl,
                                    onValueChange = { syncUrl = it },
                                    label = { Text("رابط ملف الأسئلة المباشر (URL)") },
                                    placeholder = { Text("https://example.com/questions.json") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BalotaForestGreen
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isSyncing) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            color = BalotaForestGreen,
                                            modifier = Modifier.size(28.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = "جاري الاتصال والتحميل من السحابة...",
                                            fontSize = 13.sp,
                                            color = BalotaForestGreenDark
                                        )
                                    }
                                } else {
                                    BalotaGameButton(
                                        text = "تحديث بنك الأسئلة الآن 🔄",
                                        onClick = {
                                            isSyncing = true
                                            statusMessage = null
                                            coroutineScope.launch {
                                                val (success, msg) = quizRepository.syncQuestionsFromUrl(syncUrl)
                                                isSyncing = false
                                                statusMessage = msg
                                                isStatusSuccess = success
                                                if (success) {
                                                    currentQuestionCount = quizRepository.getQuestionCount()
                                                    isCustomPack = true
                                                    soundManager.playCorrect(soundEnabled, hapticsEnabled)
                                                } else {
                                                    soundManager.playIncorrect(soundEnabled, hapticsEnabled)
                                                }
                                            }
                                        },
                                        gradientColors = listOf(BalotaForestGreenLight, BalotaForestGreen),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                // TAB 1: ADD SINGLE QUESTION
                if (selectedTabIndex == 1) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "إنشاء وإضافة سؤال جديد للعبة ✨",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BalotaForestGreenDark
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = newQuestionText,
                                    onValueChange = { newQuestionText = it },
                                    label = { Text("نص السؤال *") },
                                    placeholder = { Text("مثال: ما اسم المعلم الشهير بمدينة...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BalotaForestGreen)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = newCorrectAnswer,
                                    onValueChange = { newCorrectAnswer = it },
                                    label = { Text("الإجابة الصحيحة ✅ *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BalotaForestGreen)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newWrong1,
                                    onValueChange = { newWrong1 = it },
                                    label = { Text("الخيار الخاطئ 1 ❌ *") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newWrong2,
                                    onValueChange = { newWrong2 = it },
                                    label = { Text("الخيار الخاطئ 2 ❌ *") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = newWrong3,
                                    onValueChange = { newWrong3 = it },
                                    label = { Text("الخيار الخاطئ 3 ❌ *") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = newFunFact,
                                    onValueChange = { newFunFact = it },
                                    label = { Text("معلومة بلوطة الذهبية 💡 (اختياري)") },
                                    placeholder = { Text("معلومة تثقيفية يتعلمها الطفل فور الإجابة") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Category Dropdown
                                ExposedDropdownMenuBox(
                                    expanded = categoryExpanded,
                                    onExpandedChange = { categoryExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = selectedCategory,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("الفئة") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                        modifier = Modifier
                                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                            .fillMaxWidth()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = categoryExpanded,
                                        onDismissRequest = { categoryExpanded = false }
                                    ) {
                                        categories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat) },
                                                onClick = {
                                                    selectedCategory = cat
                                                    categoryExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = selectedWilayaRef,
                                    onValueChange = { selectedWilayaRef = it },
                                    label = { Text("الولاية (مثال: 16 - الجزائر العاصمة)") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                BalotaGameButton(
                                    text = "حفظ السؤال في اللعبة فوراً 💾",
                                    onClick = {
                                        if (newQuestionText.isBlank() || newCorrectAnswer.isBlank() ||
                                            newWrong1.isBlank() || newWrong2.isBlank() || newWrong3.isBlank()
                                        ) {
                                            statusMessage = "يرجى ملء نص السؤال والإجابة الصحيحة والخيارات الثلاثة!"
                                            isStatusSuccess = false
                                            return@BalotaGameButton
                                        }

                                        val ok = quizRepository.addSingleQuestion(
                                            category = selectedCategory,
                                            wilayaRef = selectedWilayaRef,
                                            difficulty = selectedDifficulty,
                                            questionText = newQuestionText,
                                            correctAnswer = newCorrectAnswer,
                                            wrongOption1 = newWrong1,
                                            wrongOption2 = newWrong2,
                                            wrongOption3 = newWrong3,
                                            funFact = newFunFact.ifBlank { "معلومة قيمة ومميزة عن تراث الجزائر!" }
                                        )

                                        if (ok) {
                                            currentQuestionCount = quizRepository.getQuestionCount()
                                            isCustomPack = true
                                            statusMessage = "تمت إضافة السؤال بنجاح! أصبح بنك الأسئلة يحتوي على $currentQuestionCount سؤالاً."
                                            isStatusSuccess = true
                                            soundManager.playCorrect(soundEnabled, hapticsEnabled)

                                            // Clear form
                                            newQuestionText = ""
                                            newCorrectAnswer = ""
                                            newWrong1 = ""
                                            newWrong2 = ""
                                            newWrong3 = ""
                                            newFunFact = ""
                                        } else {
                                            statusMessage = "تعذر حفظ السؤال. تحقق من المساحة المتاحة."
                                            isStatusSuccess = false
                                        }
                                    },
                                    gradientColors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // TAB 2: IMPORT / EXPORT JSON
                if (selectedTabIndex == 2) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "لصق أو استيراد ملف JSON مباشرة 📄",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BalotaForestGreenDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "يمكنك لصق مصفوفة JSON تحتوي على أسئلتك المخصصة وتطبيقها مباشرة دون الحاجة لأي اتصال بالإنترنت:",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = rawJsonInput,
                                    onValueChange = { rawJsonInput = it },
                                    label = { Text("الصق كود JSON للأسئلة هنا") },
                                    placeholder = { Text("""[{"id": 1, "question": "...", "options": [...], "answer": "...", "fun_fact": "..."}]""") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BalotaForestGreen)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val (success, msg) = quizRepository.importQuestionsFromJson(rawJsonInput)
                                            statusMessage = msg
                                            isStatusSuccess = success
                                            if (success) {
                                                currentQuestionCount = quizRepository.getQuestionCount()
                                                isCustomPack = true
                                                soundManager.playCorrect(soundEnabled, hapticsEnabled)
                                                rawJsonInput = ""
                                            } else {
                                                soundManager.playIncorrect(soundEnabled, hapticsEnabled)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = BalotaForestGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("استيراد وتطبيق 📥")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val exported = quizRepository.exportQuestionsJson()
                                            clipboardManager.setText(AnnotatedString(exported))
                                            statusMessage = "تم نسخ كود JSON لكافة الأسئلة إلى الحافظة بنجاح!"
                                            isStatusSuccess = true
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("تصدير ونسخ 📋")
                                    }
                                }
                            }
                        }
                    }
                }

                // Reset to default button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "استعادة الحزمة الأصلية",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "إلغاء التحديثات المخصصة والرجوع لـ 100 سؤال الأساسية.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            Button(
                                onClick = { showResetDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
                            ) {
                                Text("استعادة", color = BalotaIncorrectRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}
