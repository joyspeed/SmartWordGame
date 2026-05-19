package com.smartwordgame.app.data

import org.junit.Assert.*
import org.junit.Test

class QuestionGeneratorTest {

    private val sampleWords = listOf(
        WordItem(1, "כֶּלֶב", "dog"),
        WordItem(2, "חָתוּל", "cat"),
        WordItem(3, "בַּיִת", "house"),
        WordItem(4, "סֵפֶר", "book"),
        WordItem(5, "שֻׁלְחָן", "table"),
        WordItem(6, "כִּסֵּא", "chair"),
        WordItem(7, "יֶלֶד", "child"),
        WordItem(8, "מוֹרָה", "teacher")
    )

    @Test
    fun `generateRound returns correct number of questions`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 5, emptyMap(), false)
        assertEquals(5, questions.size)
    }

    @Test
    fun `generateRound caps at available words`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 100, emptyMap(), false)
        assertEquals(sampleWords.size, questions.size)
    }

    @Test
    fun `generateRound returns empty for empty word list`() {
        val questions = QuestionGenerator.generateRound(emptyList(), 10, emptyMap(), false)
        assertTrue(questions.isEmpty())
    }

    @Test
    fun `generateRound returns empty for zero count`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 0, emptyMap(), false)
        assertTrue(questions.isEmpty())
    }

    @Test
    fun `each question has 4 options`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 5, emptyMap(), false)
        questions.forEach { q ->
            assertEquals(4, q.options.size)
        }
    }

    @Test
    fun `correct option index points to right answer`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 8, emptyMap(), false)
        questions.forEach { q ->
            val expected = when (q.type) {
                QuestionType.WORD_TO_MEANING -> q.correctItem.explanation
                QuestionType.MEANING_TO_WORD -> q.correctItem.word
            }
            assertEquals(expected, q.options[q.correctOptionIndex])
        }
    }

    @Test
    fun `question prompt matches type`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 8, emptyMap(), false)
        questions.forEach { q ->
            when (q.type) {
                QuestionType.WORD_TO_MEANING -> assertEquals(q.correctItem.word, q.prompt)
                QuestionType.MEANING_TO_WORD -> assertEquals(q.correctItem.explanation, q.prompt)
            }
        }
    }

    @Test
    fun `question options contain no duplicates`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 8, emptyMap(), false)
        questions.forEach { q ->
            assertEquals(q.options.size, q.options.distinct().size)
        }
    }

    @Test
    fun `roughly 50-50 split between question types`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 8, emptyMap(), false)
        val wordToMeaning = questions.count { it.type == QuestionType.WORD_TO_MEANING }
        val meaningToWord = questions.count { it.type == QuestionType.MEANING_TO_WORD }
        assertEquals(4, wordToMeaning)
        assertEquals(4, meaningToWord)
    }

    @Test
    fun `smart practice with weak scores biases selection`() {
        // Run many rounds and check that word with score 3 appears more often
        val weakScores = mapOf(1 to 3) // word 1 = highest difficulty
        var word1Count = 0
        val rounds = 100
        repeat(rounds) {
            val questions = QuestionGenerator.generateRound(sampleWords, 1, weakScores, true)
            if (questions.first().correctItem.id == 1) word1Count++
        }
        // With weight 6 vs weight 1 for others (total = 6 + 7*1 = 13), expected ~46%
        // Should appear significantly more than uniform (1/8 = 12.5%)
        assertTrue("Word with score 3 should appear more often (appeared $word1Count/100)", word1Count > 20)
    }

    @Test
    fun `no duplicate correct words in a round`() {
        val questions = QuestionGenerator.generateRound(sampleWords, 8, emptyMap(), false)
        val correctIds = questions.map { it.correctItem.id }
        assertEquals(correctIds.size, correctIds.distinct().size)
    }

    @Test
    fun `minimum 4 words required for valid game`() {
        val tooFew = sampleWords.take(3)
        // Should still generate questions (with potential distractor reuse)
        val questions = QuestionGenerator.generateRound(tooFew, 3, emptyMap(), false)
        assertEquals(3, questions.size)
    }
}
