package com.smartwordgame.app.data

import android.content.Context
import androidx.core.content.edit

class WeakWordsManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getScore(wordId: Int): Int = sharedPreferences.getInt(scoreKey(wordId), 0).coerceIn(MIN_SCORE, MAX_SCORE)

    fun incrementScore(wordId: Int) {
        val updatedScore = (getScore(wordId) + 1).coerceAtMost(MAX_SCORE)
        sharedPreferences.edit { putInt(scoreKey(wordId), updatedScore) }
    }

    fun decrementScore(wordId: Int) {
        val updatedScore = (getScore(wordId) - 1).coerceAtLeast(MIN_SCORE)
        sharedPreferences.edit { putInt(scoreKey(wordId), updatedScore) }
    }

    fun getWeakWords(): Map<Int, Int> = sharedPreferences.all.mapNotNull { (key, value) ->
        if (!key.startsWith(KEY_PREFIX)) {
            null
        } else {
            val wordId = key.removePrefix(KEY_PREFIX).toIntOrNull()
            val score = (value as? Int)?.coerceIn(MIN_SCORE, MAX_SCORE) ?: return@mapNotNull null
            if (wordId != null && score > 0) wordId to score else null
        }
    }.toMap()

    fun resetAll() {
        sharedPreferences.edit { clear() }
    }

    private fun scoreKey(wordId: Int): String = "$KEY_PREFIX$wordId"

    private companion object {
        const val PREFS_NAME = "weak_words_scores"
        const val KEY_PREFIX = "word_"
        const val MIN_SCORE = 0
        const val MAX_SCORE = 3
    }
}
