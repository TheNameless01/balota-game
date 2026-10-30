package com.example.data.models

data class WilayaInfo(
    val code: String,
    val name: String,
    val fullRef: String,
    val questionCount: Int = 0,
    val isCompleted: Boolean = false,
    val bestScore: Int = 0
)
