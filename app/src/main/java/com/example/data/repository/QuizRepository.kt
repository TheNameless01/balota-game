package com.example.data.repository

import android.content.Context
import com.example.data.models.Question
import com.example.data.models.WilayaInfo
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar

class QuizRepository(private val context: Context) {

    private val cachedQuestions = mutableListOf<Question>()

    init {
        loadQuestionsFromAssets()
    }

    private fun loadQuestionsFromAssets() {
        if (cachedQuestions.isNotEmpty()) return

        try {
            val inputStream = context.assets.open("questions.json")
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            val jsonString = reader.use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getInt("id")
                val category = obj.getString("category")
                val wilayaRef = obj.getString("wilaya_ref")
                val difficulty = obj.getString("difficulty")
                val questionText = obj.getString("question")
                val answer = obj.getString("answer")
                val funFact = obj.getString("fun_fact")

                val optionsArray = obj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }

                cachedQuestions.add(
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAllQuestions(): List<Question> = cachedQuestions.toList()

    fun getQuestionCount(): Int = cachedQuestions.size

    fun getRandomQuiz(count: Int = 10): List<Question> {
        val shuffled = cachedQuestions.shuffled()
        return shuffled.take(count.coerceAtMost(cachedQuestions.size))
    }

    fun getQuestionsByWilaya(wilayaRef: String): List<Question> {
        return cachedQuestions.filter {
            it.wilayaRef.equals(wilayaRef, ignoreCase = true) ||
            it.wilayaCode == wilayaRef.trim() ||
            it.wilayaName.equals(wilayaRef.trim(), ignoreCase = true)
        }
    }

    fun getQuestionsByCategory(category: String): List<Question> {
        return cachedQuestions.filter { it.category == category }
    }

    fun getQuestionsByDifficulty(difficulty: String): List<Question> {
        return cachedQuestions.filter { it.difficulty == difficulty }
    }

    fun getCategoriesWithCounts(): Map<String, Int> {
        val map = linkedMapOf<String, Int>()
        // Authentic categories from the 100 questions database
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

        // Deterministic shuffle based on day
        val random = java.util.Random(seed.toLong())
        val copy = cachedQuestions.toMutableList()
        copy.shuffle(random)
        return copy.take(count.coerceAtMost(copy.size))
    }
}
