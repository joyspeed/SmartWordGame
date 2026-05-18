package com.smartwordgame.app.data

enum class Difficulty(val seconds: Int) { EASY(60), MEDIUM(30), HARD(15) }

data class RoundConfig(
    val questionCount: Int,
    val difficulty: Difficulty,
    val smartPractice: Boolean = true
)
