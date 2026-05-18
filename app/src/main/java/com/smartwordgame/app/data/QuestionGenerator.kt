package com.smartwordgame.app.data

import kotlin.random.Random

enum class QuestionType { WORD_TO_MEANING, MEANING_TO_WORD }

data class Question(
    val type: QuestionType,
    val correctItem: WordItem,
    val options: List<String>,
    val correctOptionIndex: Int,
    val prompt: String
)

object QuestionGenerator {
    fun generateRound(
        words: List<WordItem>,
        count: Int,
        weakScores: Map<Int, Int>,
        smartPractice: Boolean
    ): List<Question> {
        if (words.isEmpty() || count <= 0) return emptyList()

        val random = Random.Default
        val actualCount = count.coerceAtMost(words.size)
        val selectedWords = if (smartPractice) {
            selectWeightedWords(words, actualCount, weakScores, random)
        } else {
            words.shuffled(random).take(actualCount)
        }

        val questionTypes = buildQuestionTypes(actualCount, random)
        return selectedWords.mapIndexed { index, wordItem ->
            createQuestion(questionTypes[index], wordItem, words, random)
        }
    }

    private fun selectWeightedWords(
        words: List<WordItem>,
        count: Int,
        weakScores: Map<Int, Int>,
        random: Random
    ): List<WordItem> {
        val remainingWords = words.toMutableList()
        val selectedWords = mutableListOf<WordItem>()

        repeat(count.coerceAtMost(remainingWords.size)) {
            val nextWord = weightedPick(remainingWords, weakScores, random)
            selectedWords += nextWord
            remainingWords.remove(nextWord)
        }

        return selectedWords
    }

    private fun weightedPick(
        candidates: List<WordItem>,
        weakScores: Map<Int, Int>,
        random: Random
    ): WordItem {
        val weights = candidates.map { item -> scoreToWeight(weakScores[item.id] ?: 0) }
        val totalWeight = weights.sum()
        var pick = random.nextInt(totalWeight)

        candidates.forEachIndexed { index, item ->
            pick -= weights[index]
            if (pick < 0) return item
        }

        return candidates.last()
    }

    private fun buildQuestionTypes(count: Int, random: Random): List<QuestionType> {
        val wordToMeaningCount = count / 2
        val meaningToWordCount = count / 2
        val types = MutableList(wordToMeaningCount) { QuestionType.WORD_TO_MEANING } +
            MutableList(meaningToWordCount) { QuestionType.MEANING_TO_WORD }

        if (count % 2 == 1) {
            val extraType = if (random.nextBoolean()) {
                QuestionType.WORD_TO_MEANING
            } else {
                QuestionType.MEANING_TO_WORD
            }
            return (types + extraType).shuffled(random)
        }

        return types.shuffled(random)
    }

    private fun createQuestion(
        type: QuestionType,
        correctItem: WordItem,
        allWords: List<WordItem>,
        random: Random
    ): Question {
        val correctOption = when (type) {
            QuestionType.WORD_TO_MEANING -> correctItem.explanation
            QuestionType.MEANING_TO_WORD -> correctItem.word
        }
        val distractors = allWords
            .asSequence()
            .filter { it.id != correctItem.id }
            .map {
                when (type) {
                    QuestionType.WORD_TO_MEANING -> it.explanation
                    QuestionType.MEANING_TO_WORD -> it.word
                }
            }
            .filter { it != correctOption }
            .distinct()
            .shuffled(random)
            .take(3)
            .toMutableList()

        val options = (distractors + correctOption).shuffled(random)
        return Question(
            type = type,
            correctItem = correctItem,
            options = options,
            correctOptionIndex = options.indexOf(correctOption),
            prompt = when (type) {
                QuestionType.WORD_TO_MEANING -> correctItem.word
                QuestionType.MEANING_TO_WORD -> correctItem.explanation
            }
        )
    }

    private fun scoreToWeight(score: Int): Int = when (score.coerceIn(0, 3)) {
        3 -> 6
        2 -> 3
        1 -> 2
        else -> 1
    }
}
