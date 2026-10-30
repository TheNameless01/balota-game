package com.example.data.repository

import android.content.Context
import com.example.data.models.Question
import com.example.data.models.WilayaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Calendar
import java.util.concurrent.TimeUnit

class QuizRepository(private val context: Context) {

    private val cachedQuestions = mutableListOf<Question>()
    private val dynamicFile = File(context.filesDir, "dynamic_questions.json")

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        cachedQuestions.clear()

        // 1. If user previously updated/added questions locally, load from filesDir
        if (dynamicFile.exists()) {
            try {
                val jsonString = dynamicFile.readText(Charsets.UTF_8)
                val parsed = parseQuestionsFromJson(jsonString)
                if (parsed.isNotEmpty()) {
                    cachedQuestions.addAll(parsed)
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Default: Load the 100 bundled questions from assets
        try {
            val inputStream = context.assets.open("questions.json")
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            val jsonString = reader.use { it.readText() }
            val parsed = parseQuestionsFromJson(jsonString)
            cachedQuestions.addAll(parsed)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseQuestionsFromJson(jsonString: String): List<Question> {
        val list = mutableListOf<Question>()
        val clean = jsonString.trim().replace(Regex("""^\s*\\\["""), "[").replace(Regex("""\\\]\s*$"""), "]")
        val jsonArray = JSONArray(clean)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = if (obj.has("id")) obj.getInt("id") else (i + 1)
            val category = obj.optString("category", "الجغرافيا والولايات")
            val wilayaRef = obj.optString("wilaya_ref", "16 - الجزائر العاصمة")
            val difficulty = obj.optString("difficulty", "متوسط")
            val questionText = obj.getString("question")
            val answer = obj.getString("answer")
            val funFact = obj.optString("fun_fact", "")

            val optionsArray = obj.getJSONArray("options")
            val options = mutableListOf<String>()
            for (j in 0 until optionsArray.length()) {
                options.add(optionsArray.getString(j))
            }

            list.add(
                Question(
                    id = id,
                    category = category,
                    wilayaRef = wilayaRef,
                    difficulty = difficulty,
                    question = questionText,
                    options = options,
                    answer = answer,
                    funFact = funFact
                )
            )
        }
        return list
    }

    private fun saveCurrentQuestionsToStorage(): Boolean {
        return try {
            val jsonArray = JSONArray()
            for (q in cachedQuestions) {
                val obj = JSONObject()
                obj.put("id", q.id)
                obj.put("category", q.category)
                obj.put("wilaya_ref", q.wilayaRef)
                obj.put("difficulty", q.difficulty)
                obj.put("question", q.question)

                val optArr = JSONArray()
                q.options.forEach { optArr.put(it) }
                obj.put("options", optArr)

                obj.put("answer", q.answer)
                obj.put("fun_fact", q.funFact)
                jsonArray.put(obj)
            }
            dynamicFile.writeText(jsonArray.toString(2), Charsets.UTF_8)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Over-The-Air (OTA) Online Question Sync:
     * Downloads questions from any remote JSON URL (e.g. GitHub Raw, Firebase, Supabase, API),
     * validates, saves to internal storage, and refreshes the in-app quiz database immediately!
     */
    suspend fun syncQuestionsFromUrl(url: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        if (url.isBlank()) {
            return@withContext Pair(false, "يرجى إدخال رابط صالح لتحديث الأسئلة.")
        }

        try {
            val request = Request.Builder()
                .url(url.trim())
                .addHeader("Accept", "application/json")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Pair(false, "تعذر الاتصال بالخادم (رمز الخطأ: ${response.code})")
            }

            val body = response.body?.string() ?: return@withContext Pair(false, "استجابة الخادم فارغة.")
            val parsedQuestions = parseQuestionsFromJson(body)

            if (parsedQuestions.isEmpty()) {
                return@withContext Pair(false, "لم يتم العثور على أي أسئلة صالحة في الرابط.")
            }

            // Save to dynamic storage and update memory cache
            cachedQuestions.clear()
            cachedQuestions.addAll(parsedQuestions)
            saveCurrentQuestionsToStorage()

            return@withContext Pair(true, "تم التحديث بنجاح! تم تحميل ${parsedQuestions.size} سؤالاً جديداً.")
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Pair(false, "فشل التحديث: ${e.localizedMessage ?: "تأكد من الاتصال بالإنترنت"}")
        }
    }

    /**
     * Import questions directly from a JSON string or pasted content
     */
    fun importQuestionsFromJson(jsonString: String): Pair<Boolean, String> {
        return try {
            val parsed = parseQuestionsFromJson(jsonString)
            if (parsed.isEmpty()) {
                return Pair(false, "النص لا يحتوي على أسئلة بتنسيق JSON صحيح.")
            }
            cachedQuestions.clear()
            cachedQuestions.addAll(parsed)
            saveCurrentQuestionsToStorage()
            Pair(true, "تم استيراد ${parsed.size} سؤالاً بنجاح وحفظها في اللعبة!")
        } catch (e: Exception) {
            Pair(false, "خطأ في قراءة ملف الأسئلة: ${e.localizedMessage}")
        }
    }

    /**
     * Add a single custom question created inside the app
     */
    fun addSingleQuestion(
        category: String,
        wilayaRef: String,
        difficulty: String,
        questionText: String,
        correctAnswer: String,
        wrongOption1: String,
        wrongOption2: String,
        wrongOption3: String,
        funFact: String
    ): Boolean {
        val nextId = (cachedQuestions.maxOfOrNull { it.id } ?: 100) + 1
        val options = listOf(correctAnswer, wrongOption1, wrongOption2, wrongOption3).shuffled()

        val newQ = Question(
            id = nextId,
            category = category,
            wilayaRef = wilayaRef,
            difficulty = difficulty,
            question = questionText.trim(),
            options = options,
            answer = correctAnswer.trim(),
            funFact = funFact.trim()
        )

        cachedQuestions.add(newQ)
        return saveCurrentQuestionsToStorage()
    }

    /**
     * Reset to the original 100 bundled questions
     */
    fun resetToDefaultQuestions(): Boolean {
        try {
            if (dynamicFile.exists()) {
                dynamicFile.delete()
            }
            loadQuestions()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Export all questions as formatted JSON string
     */
    fun exportQuestionsJson(): String {
        val jsonArray = JSONArray()
        for (q in cachedQuestions) {
            val obj = JSONObject()
            obj.put("id", q.id)
            obj.put("category", q.category)
            obj.put("wilaya_ref", q.wilayaRef)
            obj.put("difficulty", q.difficulty)
            obj.put("question", q.question)
            val optArr = JSONArray()
            q.options.forEach { optArr.put(it) }
            obj.put("options", optArr)
            obj.put("answer", q.answer)
            obj.put("fun_fact", q.funFact)
            jsonArray.put(obj)
        }
        return jsonArray.toString(2)
    }

    // --- Query Functions ---

    fun getAllQuestions(): List<Question> = cachedQuestions.toList()

    fun getQuestionCount(): Int = cachedQuestions.size

    fun isUsingCustomQuestions(): Boolean = dynamicFile.exists()

    fun getRandomQuiz(count: Int = 10): List<Question> {
        val shuffled = cachedQuestions.shuffled()
        return shuffled.take(count.coerceAtMost(cachedQuestions.size)).map { q ->
            q.copy(options = q.options.shuffled())
        }
    }

    fun getQuestionsByWilaya(wilayaRef: String): List<Question> {
        return cachedQuestions.filter {
            it.wilayaRef.equals(wilayaRef, ignoreCase = true) ||
            it.wilayaCode == wilayaRef.trim() ||
            it.wilayaName.equals(wilayaRef.trim(), ignoreCase = true)
        }.map { it.copy(options = it.options.shuffled()) }
    }

    fun getQuestionsByCategory(category: String): List<Question> {
        return cachedQuestions.filter { it.category == category }
            .map { it.copy(options = it.options.shuffled()) }
    }

    fun getQuestionsByDifficulty(difficulty: String): List<Question> {
        return cachedQuestions.filter { it.difficulty == difficulty }
            .map { it.copy(options = it.options.shuffled()) }
    }

    fun getCategoriesWithCounts(): Map<String, Int> {
        val map = linkedMapOf<String, Int>()
        listOf(
            "الأطباق والمأكولات",
            "الجغرافيا والولايات",
            "الطبيعة والمعالم",
            "التاريخ والمعالم",
            "التقاليد والألبسة",
            "الحرف والتقاليد"
        ).forEach { cat ->
            map[cat] = cachedQuestions.count { it.category == cat }
        }
        return map
    }

    fun getAllWilayas(): List<WilayaInfo> {
        val wilayaMap = mutableMapOf<String, WilayaInfo>()
        for (q in cachedQuestions) {
            val ref = q.wilayaRef
            val code = q.wilayaCode
            val name = q.wilayaName
            val existing = wilayaMap[ref]
            if (existing == null) {
                wilayaMap[ref] = WilayaInfo(
                    code = code,
                    name = name,
                    fullRef = ref,
                    questionCount = 1
                )
            } else {
                wilayaMap[ref] = existing.copy(questionCount = existing.questionCount + 1)
            }
        }
        return wilayaMap.values.sortedBy { it.code }
    }

    fun getDailyChallengeQuestions(count: Int = 5): List<Question> {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val seed = dayOfYear + year * 1000

        val random = java.util.Random(seed.toLong())
        val copy = cachedQuestions.toMutableList()
        copy.shuffle(random)
        return copy.take(count.coerceAtMost(copy.size)).map { it.copy(options = it.options.shuffled()) }
    }
}
