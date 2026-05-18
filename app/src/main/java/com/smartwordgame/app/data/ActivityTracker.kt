package com.smartwordgame.app.data

import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DayStats(
    val date: LocalDate,
    val questionsAnswered: Int,
    val correctAnswers: Int
)

class ActivityTracker(context: Context) {
    private val prefs = context.getSharedPreferences("activity_tracker", Context.MODE_PRIVATE)
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun recordAnswer(correct: Boolean) {
        val today = LocalDate.now().format(dateFormatter)
        val answeredKey = "answered_$today"
        val correctKey = "correct_$today"

        val currentAnswered = prefs.getInt(answeredKey, 0)
        prefs.edit().putInt(answeredKey, currentAnswered + 1).apply()

        if (correct) {
            val currentCorrect = prefs.getInt(correctKey, 0)
            prefs.edit().putInt(correctKey, currentCorrect + 1).apply()
        }
    }

    fun getDailyStats(days: Int = 30): List<DayStats> {
        val today = LocalDate.now()
        return (0 until days).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val dateStr = date.format(dateFormatter)
            DayStats(
                date = date,
                questionsAnswered = prefs.getInt("answered_$dateStr", 0),
                correctAnswers = prefs.getInt("correct_$dateStr", 0)
            )
        }.reversed()
    }
}
