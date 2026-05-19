package com.smartwordgame.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smartwordgame.app.data.ActivityTracker
import com.smartwordgame.app.data.DayStats
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ActivitySummaryScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dailyStats = remember(context) { ActivityTracker(context).getDailyStats(30) }

    ActivitySummaryContent(
        dailyStats = dailyStats,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivitySummaryContent(
    dailyStats: List<DayStats>,
    onBack: () -> Unit
) {
    val totalQuestions = dailyStats.sumOf { it.questionsAnswered }
    val totalCorrect = dailyStats.sumOf { it.correctAnswers }
    val accuracy = if (totalQuestions > 0) {
        ((totalCorrect.toFloat() / totalQuestions) * 100).roundToInt()
    } else {
        0
    }
    val activeDays = dailyStats.filter { it.questionsAnswered > 0 }
    val maxQuestions = activeDays.maxOfOrNull { it.questionsAnswered } ?: 0
    val noActivity = activeDays.isEmpty()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM", Locale("he")) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            text = "סיכום 30 ימים אחרונים",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "חזרה"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SummaryHeaderCard(
                        totalQuestions = totalQuestions,
                        totalCorrect = totalCorrect,
                        accuracy = accuracy
                    )
                }

                if (noActivity) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                            tonalElevation = 2.dp,
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                text = "🐻 אין פעילות עדיין. שחקו כדי לראות את ההתקדמות! 🎮",
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                items(
                    items = activeDays,
                    key = { it.date.toString() }
                ) { stats ->
                    DayStatsCard(
                        stats = stats,
                        maxQuestions = maxQuestions,
                        dateFormatter = dateFormatter
                    )
                }

                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }
            }
        }
    }
}

@Composable
private fun SummaryHeaderCard(
    totalQuestions: Int,
    totalCorrect: Int,
    accuracy: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SummaryLine(label = "📝 סה\"כ שאלות", value = totalQuestions.toString())
            SummaryLine(label = "✅ תשובות נכונות", value = totalCorrect.toString())
            SummaryLine(label = "🎯 דיוק כולל", value = "$accuracy%")
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun DayStatsCard(
    stats: DayStats,
    maxQuestions: Int,
    dateFormatter: DateTimeFormatter
) {
    val totalRatio = if (maxQuestions > 0) {
        stats.questionsAnswered.toFloat() / maxQuestions.toFloat()
    } else {
        0f
    }
    val accuracyRatio = if (stats.questionsAnswered > 0) {
        stats.correctAnswers.toFloat() / stats.questionsAnswered.toFloat()
    } else {
        0f
    }
    val accuracyPercent = (accuracyRatio * 100).roundToInt()
    val barColor = when {
        accuracyPercent >= 80 -> Color(0xFF66BB6A)
        accuracyPercent >= 50 -> Color(0xFFFF9800)
        else -> Color(0xFFEF5350)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.width(70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stats.date.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${stats.questionsAnswered} שאלות, ${stats.correctAnswers} נכונות",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(totalRatio)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(999.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}
