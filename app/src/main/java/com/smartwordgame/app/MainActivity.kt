package com.smartwordgame.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartwordgame.app.data.Difficulty
import com.smartwordgame.app.data.Question
import com.smartwordgame.app.data.RoundConfig
import com.smartwordgame.app.game.GameViewModel
import com.smartwordgame.app.ui.GameScreen
import com.smartwordgame.app.ui.HomeScreen
import com.smartwordgame.app.ui.PracticeScreen
import com.smartwordgame.app.ui.SettingsScreen
import com.smartwordgame.app.ui.SummaryScreen
import com.smartwordgame.app.ui.theme.SmartWordGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartWordGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartWordGameNavHost()
                }
            }
        }
    }
}

@Composable
private fun SmartWordGameNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    var pendingRoundConfig by remember { mutableStateOf<RoundConfig?>(null) }
    var lastRoundConfig by remember { mutableStateOf<RoundConfig?>(null) }
    var summaryData by remember { mutableStateOf<SummaryRouteData?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartGame = { config ->
                    pendingRoundConfig = config
                    lastRoundConfig = config
                    navController.navigate(Screen.Game.route)
                },
                onNavigateToPractice = {
                    navController.navigate(Screen.Practice.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Game.route) {
            val config = pendingRoundConfig ?: lastRoundConfig ?: DEFAULT_ROUND_CONFIG
            val gameViewModel: GameViewModel = viewModel()

            LaunchedEffect(gameViewModel, config) {
                pendingRoundConfig = null
                lastRoundConfig = config
                gameViewModel.startRound(context, config)
            }

            GameScreen(
                viewModel = gameViewModel,
                onRoundFinished = { correctCount, totalQuestions, totalTimeMs, mistakes ->
                    summaryData = SummaryRouteData(
                        correctCount = correctCount,
                        totalQuestions = totalQuestions,
                        totalTimeMs = totalTimeMs,
                        mistakes = mistakes.map(Question::toSummaryMistake)
                    )
                    navController.navigate(Screen.Summary.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Summary.route) {
            val data = summaryData
            if (data == null) {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            } else {
                SummaryScreen(
                    correctCount = data.correctCount,
                    totalQuestions = data.totalQuestions,
                    totalTimeMs = data.totalTimeMs,
                    mistakes = data.mistakes,
                    onPlayAgain = {
                        pendingRoundConfig = lastRoundConfig ?: DEFAULT_ROUND_CONFIG
                        navController.navigate(Screen.Game.route) {
                            popUpTo(Screen.Game.route) { inclusive = true }
                        }
                    },
                    onNavigateToPractice = {
                        navController.navigate(Screen.Practice.route)
                    },
                    onNavigateHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.Practice.route) {
            PracticeScreen(
                onStartPracticeRound = {
                    pendingRoundConfig = (lastRoundConfig ?: DEFAULT_ROUND_CONFIG).copy(smartPractice = true)
                    navController.navigate(Screen.Game.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

private data class SummaryRouteData(
    val correctCount: Int,
    val totalQuestions: Int,
    val totalTimeMs: Long,
    val mistakes: List<Pair<String, String>>
)

private sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Game : Screen("game")
    data object Summary : Screen("summary")
    data object Practice : Screen("practice")
    data object Settings : Screen("settings")
}

private val DEFAULT_ROUND_CONFIG = RoundConfig(
    questionCount = 10,
    difficulty = Difficulty.MEDIUM,
    smartPractice = true
)

private fun Question.toSummaryMistake(): Pair<String, String> = correctItem.word to correctItem.explanation
