package com.example.data.models

data class Question(
    val id: Int,
    val category: String,
    val wilayaRef: String,
    val difficulty: String,
    val question: String,
    val options: List<String>,
    val answer: String,
    val funFact: String
) {
    /**
     * Checks if the user's selected option is the correct answer.
     * Preserves original wording while handling slight typographical variances.
     */
    fun isOptionCorrect(selectedOption: String): Boolean {
        if (selectedOption == answer) return true
        if (options.contains(selectedOption)) {
            // Handles typographical variance in Question 76 where answer has "المس المسرح" vs option "المسرح"
            if (answer.contains("المسرح الروماني") && selectedOption.contains("المسرح الروماني")) {
                return true
            }
        }
        return false
    }

    /**
     * Extracts pure wilaya name from wilayaRef (e.g., "16 - الجزائر العاصمة" -> "الجزائر العاصمة")
     */
    val wilayaName: String
        get() {
            val parts = wilayaRef.split("-")
            return if (parts.size > 1) parts[1].trim() else wilayaRef
        }

    /**
     * Extracts wilaya code number (e.g., "16 - الجزائر العاصمة" -> "16")
     */
    val wilayaCode: String
        get() {
            val parts = wilayaRef.split("-")
            return if (parts.isNotEmpty()) parts[0].trim() else ""
        }
}
