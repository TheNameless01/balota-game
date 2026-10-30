package com.example.data.models

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String, // emoji or icon reference
    val currentProgress: Int,
    val maxProgress: Int,
    val isUnlocked: Boolean = false
) {
    val progressFraction: Float
        get() = if (maxProgress > 0) (currentProgress.coerceAtMost(maxProgress).toFloat() / maxProgress) else 0f
}
