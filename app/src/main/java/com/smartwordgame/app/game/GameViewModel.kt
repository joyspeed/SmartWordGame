package com.smartwordgame.app.game

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartwordgame.app.data.ActivityTracker
import com.smartwordgame.app.data.Question
import com.smartwordgame.app.data.QuestionGenerator
import com.smartwordgame.app.data.RoundConfig
import com.smartwordgame.app.data.WeakWordsManager
import com.smartwordgame.app.data.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {
    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var weakWordsManager: WeakWordsManager? = null
    private var activityTracker: ActivityTracker? = null
    private var questions: List<Question> = emptyList()
    private var roundConfig: RoundConfig? = null
    private var currentQuestionIndex = 0
    private var correctCount = 0
    private val mistakes = mutableListOf<Question>()
    private var roundStartTimeMs = 0L
    private var isPaused = false

    private var timerJob: Job? = null
    private var autoAdvanceJob: Job? = null
    private var roundJob: Job? = null

    fun startRound(context: Context, config: RoundConfig) {
        roundJob?.cancel()
        timerJob?.cancel()
        autoAdvanceJob?.cancel()
        _gameState.value = GameState.Loading
        isPaused = false
        roundConfig = config
        currentQuestionIndex = 0
        correctCount = 0
        mistakes.clear()
        roundStartTimeMs = System.currentTimeMillis()

        val appContext = context.applicationContext
        roundJob = viewModelScope.launch {
            val words = withContext(Dispatchers.IO) { WordRepository.loadWords(appContext) }
            weakWordsManager = WeakWordsManager(appContext)
            activityTracker = ActivityTracker(appContext)
            val weakScores = weakWordsManager?.getWeakWords().orEmpty()
            questions = QuestionGenerator.generateRound(
                words = words,
                count = config.questionCount,
                weakScores = weakScores,
                smartPractice = config.smartPractice
            )

            if (questions.isEmpty()) {
                finishRound()
            } else {
                showQuestion(0)
            }
        }
    }

    fun submitAnswer(optionIndex: Int) {
        val state = _gameState.value as? GameState.Playing ?: return
        if (state.answered) return

        timerJob?.cancel()
        autoAdvanceJob?.cancel()

        val isCorrect = optionIndex == state.question.correctOptionIndex
        if (isCorrect) {
            correctCount += 1
            weakWordsManager?.decrementScore(state.question.correctItem.id)
        } else {
            weakWordsManager?.incrementScore(state.question.correctItem.id)
            mistakes += state.question
        }
        activityTracker?.recordAnswer(isCorrect)

        _gameState.value = state.copy(
            correctCount = correctCount,
            answered = true,
            selectedIndex = optionIndex,
            isCorrect = isCorrect
        )

        if (isCorrect) {
            autoAdvanceJob = viewModelScope.launch {
                delay(AUTO_ADVANCE_DELAY_MS)
                val latestState = _gameState.value as? GameState.Playing ?: return@launch
                if (latestState.questionIndex == state.questionIndex && latestState.answered && latestState.isCorrect == true) {
                    nextQuestion()
                }
            }
        }
    }

    fun nextQuestion() {
        val state = _gameState.value as? GameState.Playing ?: return
        timerJob?.cancel()
        autoAdvanceJob?.cancel()

        val nextIndex = state.questionIndex + 1
        if (nextIndex >= questions.size) {
            finishRound()
        } else {
            showQuestion(nextIndex)
        }
    }

    fun onPause() {
        isPaused = true
        timerJob?.cancel()
    }

    fun onResume() {
        if (!isPaused) return
        isPaused = false

        val state = _gameState.value as? GameState.Playing ?: return
        if (!state.answered) {
            startTimer()
        }
    }

    override fun onCleared() {
        roundJob?.cancel()
        timerJob?.cancel()
        autoAdvanceJob?.cancel()
        super.onCleared()
    }

    private fun showQuestion(index: Int) {
        currentQuestionIndex = index
        val config = roundConfig ?: return
        val question = questions[index]
        _gameState.value = GameState.Playing(
            question = question,
            questionIndex = index,
            totalQuestions = questions.size,
            correctCount = correctCount,
            timeRemaining = config.difficulty.seconds,
            totalSeconds = config.difficulty.seconds,
            answered = false,
            selectedIndex = null,
            isCorrect = null
        )
        if (!isPaused) {
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        val state = _gameState.value as? GameState.Playing ?: return
        if (state.answered) return

        timerJob = viewModelScope.launch {
            var remaining = state.timeRemaining
            while (remaining > 0) {
                delay(ONE_SECOND_MS)
                val latestState = _gameState.value as? GameState.Playing ?: return@launch
                if (latestState.answered || isPaused || latestState.questionIndex != currentQuestionIndex) {
                    return@launch
                }

                remaining = latestState.timeRemaining - 1
                if (remaining <= 0) {
                    weakWordsManager?.incrementScore(latestState.question.correctItem.id)
                    activityTracker?.recordAnswer(false)
                    mistakes += latestState.question
                    _gameState.value = latestState.copy(
                        answered = true,
                        selectedIndex = null,
                        isCorrect = false,
                        timeRemaining = 0
                    )
                    return@launch
                }

                _gameState.value = latestState.copy(timeRemaining = remaining)
            }
        }
    }

    private fun finishRound() {
        timerJob?.cancel()
        autoAdvanceJob?.cancel()
        val elapsed = if (roundStartTimeMs == 0L) 0L else System.currentTimeMillis() - roundStartTimeMs
        _gameState.value = GameState.Finished(
            correctCount = correctCount,
            totalQuestions = questions.size,
            totalTimeMs = elapsed,
            mistakes = mistakes.toList()
        )
    }

    private companion object {
        const val ONE_SECOND_MS = 1_000L
        const val AUTO_ADVANCE_DELAY_MS = 2_000L
    }
}
