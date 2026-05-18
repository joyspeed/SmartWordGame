package com.smartwordgame.app.game

import com.smartwordgame.app.data.Question

sealed interface GameState {
    data object Loading : GameState

    data class Playing(
        val question: Question,
        val questionIndex: Int,
        val totalQuestions: Int,
        val correctCount: Int,
        val timeRemaining: Int,
        val totalSeconds: Int,
        val answered: Boolean,
        val selectedIndex: Int?,
        val isCorrect: Boolean?
    ) : GameState

    data class Finished(
        val correctCount: Int,
        val totalQuestions: Int,
        val totalTimeMs: Long,
        val mistakes: List<Question>
    ) : GameState
}
