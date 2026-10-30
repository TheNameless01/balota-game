package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.Question
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.BufferedReader
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BalotaDatabaseUnitTest {

    @Test
    fun testDatabaseContainsAll100Questions() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val inputStream = context.assets.open("questions.json")
        val jsonString = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { it.readText() }
        val jsonArray = JSONArray(jsonString)

        assertEquals("Database must contain exactly 100 questions", 100, jsonArray.length())

        val categories = mutableSetOf<String>()
        val difficulties = mutableSetOf<String>()
        val wilayas = mutableSetOf<String>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val id = obj.getInt("id")
            val cat = obj.getString("category")
            val wilaya = obj.getString("wilaya_ref")
            val diff = obj.getString("difficulty")
            val questionText = obj.getString("question")
            val ans = obj.getString("answer")
            val funFact = obj.getString("fun_fact")
            val optionsArray = obj.getJSONArray("options")

            assertEquals("Question $id must have 4 options", 4, optionsArray.length())
            assertTrue("Question $id text must not be empty", questionText.isNotBlank())
            assertTrue("Question $id answer must not be empty", ans.isNotBlank())
            assertTrue("Question $id fun_fact must not be empty", funFact.isNotBlank())

            val options = mutableListOf<String>()
            for (j in 0 until 4) {
                options.add(optionsArray.getString(j))
            }

            val question = Question(
                id = id,
                category = cat,
                wilayaRef = wilaya,
                difficulty = diff,
                question = questionText,
                options = options,
                answer = ans,
                funFact = funFact
            )

            // Test option matching
            var matched = false
            for (opt in options) {
                if (question.isOptionCorrect(opt)) {
                    matched = true
                    break
                }
            }
            assertTrue("Question $id option must be verifiable by isOptionCorrect", matched)

            categories.add(cat)
            difficulties.add(diff)
            wilayas.add(question.wilayaCode)
        }

        assertEquals("Must have 6 authentic categories", 6, categories.size)
        assertEquals("Must have 3 difficulties: سهل، متوسط، صعب", 3, difficulties.size)
        assertEquals("Must have all 58 Algerian wilayas", 58, wilayas.size)
    }
}
