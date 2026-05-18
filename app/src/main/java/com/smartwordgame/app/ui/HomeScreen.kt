package com.smartwordgame.app.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.smartwordgame.app.data.Difficulty
import com.smartwordgame.app.data.RoundConfig

@Composable
fun HomeScreen(
    onStartGame: (RoundConfig) -> Unit,
    onNavigateToPractice: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToActivitySummary: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember(context) {
        context.getSharedPreferences("smart_word_game_prefs", Context.MODE_PRIVATE)
    }
    val smartPractice = remember(preferences) {
        preferences.getBoolean("smartPractice", true)
    }
    var selectedQuestionCount by rememberSaveable { mutableIntStateOf(10) }
    var selectedDifficulty by rememberSaveable { mutableStateOf(Difficulty.MEDIUM) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "משחק מילים",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                SelectorSection(title = "כמה שאלות בסיבוב?") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(10, 20, 30).forEach { count ->
                            FilterChip(
                                selected = selectedQuestionCount == count,
                                onClick = { selectedQuestionCount = count },
                                label = {
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                },
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .widthIn(min = 88.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                                )
                            )
                        }
                    }
                }

                SelectorSection(title = "רמת קושי") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DifficultyOptionCard(
                            title = "קל",
                            subtitle = "(דקה לשאלה)",
                            selected = selectedDifficulty == Difficulty.EASY,
                            onClick = { selectedDifficulty = Difficulty.EASY }
                        )
                        DifficultyOptionCard(
                            title = "בינוני",
                            subtitle = "(30 שניות לשאלה)",
                            selected = selectedDifficulty == Difficulty.MEDIUM,
                            onClick = { selectedDifficulty = Difficulty.MEDIUM }
                        )
                        DifficultyOptionCard(
                            title = "קשה",
                            subtitle = "(15 שניות לשאלה)",
                            selected = selectedDifficulty == Difficulty.HARD,
                            onClick = { selectedDifficulty = Difficulty.HARD }
                        )
                    }
                }

                Button(
                    onClick = {
                        onStartGame(
                            RoundConfig(
                                questionCount = selectedQuestionCount,
                                difficulty = selectedDifficulty,
                                smartPractice = smartPractice
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "התחל",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "עוד דברים שכיף לגלות",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        NavigationMenuButton(
                            text = "מילים לתרגול",
                            icon = Icons.Filled.List,
                            onClick = onNavigateToPractice
                        )

                        NavigationMenuButton(
                            text = "מילון מילים",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            onClick = onNavigateToDictionary
                        )

                        NavigationMenuButton(
                            text = "סיכום 30 ימים",
                            icon = Icons.Filled.DateRange,
                            onClick = onNavigateToActivitySummary
                        )

                        IconButton(
                            onClick = onNavigateToSettings,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(56.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "הגדרות",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
private fun SelectorSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun DifficultyOptionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (selected) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape,
                    modifier = Modifier.size(18.dp)
                ) {}
            }
        }
    }
}
