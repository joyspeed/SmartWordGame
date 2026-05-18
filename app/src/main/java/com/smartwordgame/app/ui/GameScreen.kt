package com.smartwordgame.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartwordgame.app.data.Question
import com.smartwordgame.app.data.SettingsManager
import com.smartwordgame.app.game.GameState
import com.smartwordgame.app.game.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onRoundFinished: (correctCount: Int, totalQuestions: Int, totalTimeMs: Long, mistakes: List<Question>) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current
    var showPauseOverlay by remember { mutableStateOf(false) }
    var hasEnteredBackground by remember { mutableStateOf(false) }

    val soundManager = remember {
        val settings = SettingsManager(context)
        SoundManager(soundEnabled = settings.soundEnabled)
    }

    DisposableEffect(Unit) {
        onDispose { soundManager.release() }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    hasEnteredBackground = true
                    showPauseOverlay = true
                    viewModel.onPause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.onResume()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state) {
        if (state is GameState.Playing && hasEnteredBackground) {
            showPauseOverlay = true
            hasEnteredBackground = false
        }
        // Play sounds on answer
        val playingState = state as? GameState.Playing
        if (playingState?.answered == true) {
            if (playingState.isCorrect == true) {
                soundManager.playCorrect()
            } else {
                soundManager.playIncorrect()
            }
        }
    }

    val finishedState = state as? GameState.Finished
    LaunchedEffect(finishedState) {
        finishedState?.let {
            onRoundFinished(it.correctCount, it.totalQuestions, it.totalTimeMs, it.mistakes)
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val currentState = state) {
                GameState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is GameState.Finished -> Unit

                is GameState.Playing -> {
                    GamePlayingContent(
                        state = currentState,
                        onAnswerClick = viewModel::submitAnswer,
                        onNextClick = viewModel::nextQuestion,
                        onBack = onBack
                    )

                    if (showPauseOverlay) {
                        PauseOverlay(
                            onResume = {
                                showPauseOverlay = false
                                viewModel.onResume()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GamePlayingContent(
    state: GameState.Playing,
    onAnswerClick: (Int) -> Unit,
    onNextClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        GameHud(
            state = state,
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = state.question.prompt,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 28.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            state.question.options.forEachIndexed { index, option ->
                val (containerColor, contentColor) = answerButtonColors(state, index)
                Button(
                    onClick = { onAnswerClick(index) },
                    enabled = !state.answered,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                        disabledContainerColor = containerColor,
                        disabledContentColor = contentColor
                    )
                ) {
                    Text(
                        text = option,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        softWrap = true
                    )
                }
            }

            if (state.answered) {
                val feedbackText = if (state.isCorrect == true) "נכון! 🎉" else "לא נכון 😕"
                val feedbackColor = if (state.isCorrect == true) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }

                Text(
                    text = feedbackText,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = feedbackColor,
                    textAlign = TextAlign.Center
                )
            }

            if (state.answered && state.isCorrect == false) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "הבא",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun GameHud(
    state: GameState.Playing,
    onBack: () -> Unit
) {
    val timerColor = if (state.timeRemaining <= 5) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "חזרה",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "${state.timeRemaining}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = timerColor
                )
            }

            Text(
                text = "שאלה ${state.questionIndex + 1} מתוך ${state.totalQuestions}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "נכון: ${state.correctCount} מתוך ${state.totalQuestions}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PauseOverlay(
    onResume: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onResume,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "לחץ להמשיך",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun answerButtonColors(
    state: GameState.Playing,
    optionIndex: Int
): Pair<Color, Color> {
    val neutralContainer = MaterialTheme.colorScheme.secondaryContainer
    val neutralContent = MaterialTheme.colorScheme.onSecondaryContainer
    val correctContainer = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    val correctContent = MaterialTheme.colorScheme.primary
    val wrongContainer = MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
    val wrongContent = MaterialTheme.colorScheme.error

    if (!state.answered) {
        return neutralContainer to neutralContent
    }

    return when {
        optionIndex == state.question.correctOptionIndex -> correctContainer to correctContent
        state.selectedIndex == optionIndex && state.isCorrect == false -> wrongContainer to wrongContent
        else -> neutralContainer to neutralContent
    }
}
