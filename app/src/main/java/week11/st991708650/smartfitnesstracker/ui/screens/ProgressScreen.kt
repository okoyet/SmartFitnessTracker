package week11.st991708650.smartfitnesstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import week11.st991708650.smartfitnesstracker.ui.theme.accentAmberColor
import week11.st991708650.smartfitnesstracker.utils.getCurrentMonthYear
import week11.st991708650.smartfitnesstracker.viewmodel.FitnessViewModel

private data class DayActivity(val label: String, val minutes: Int)
private data class WeekCalories(val label: String, val kcal: Int)
private data class PersonalRecord(val title: String, val value: String, val date: String)

// Sample data: the app doesn't track per-day activity history, monthly calorie
// aggregates, or personal records yet, so these three sections render
// placeholder values to match the prototype's layout until that data exists.
private val sampleWeeklyActivity = listOf(
    DayActivity("M", 30),
    DayActivity("T", 38),
    DayActivity("W", 3),
    DayActivity("T", 25),
    DayActivity("F", 42),
    DayActivity("S", 18),
    DayActivity("S", 2)
)

private val sampleMonthlyCalories = listOf(
    WeekCalories("W1", 2100),
    WeekCalories("W2", 3400),
    WeekCalories("W3", 2800),
    WeekCalories("W4", 3100)
)

private val samplePersonalRecords = listOf(
    PersonalRecord("Longest run", "12.4 km", "Jul 15"),
    PersonalRecord("Heaviest lift", "95 kg", "Jul 22")
)

private const val SAMPLE_STREAK_DAYS = 12

@Composable
fun ProgressScreen(viewModel: FitnessViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.observeFitnessData(userId)
        }
    }

    val stats = state.dailyStats
    val goalPercent = (((stats?.steps ?: 0) / 10000f).coerceIn(0f, 1f) * 100).toInt()
    val totalSessions = state.workouts.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Progress",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = getCurrentMonthYear(),
            style = MaterialTheme.typography.bodyMedium,
            color = accentAmberColor()
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                modifier = Modifier.weight(1f),
                label = "GOAL",
                value = "$goalPercent%",
                valueColor = MaterialTheme.colorScheme.primary,
                caption = "Monthly"
            )

            StatTile(
                modifier = Modifier.weight(1f),
                label = "STREAK",
                value = "$SAMPLE_STREAK_DAYS",
                valueColor = accentAmberColor(),
                caption = "days"
            )

            StatTile(
                modifier = Modifier.weight(1f),
                label = "SESSIONS",
                value = "$totalSessions",
                valueColor = MaterialTheme.colorScheme.primary,
                caption = "total"
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weekly Activity (min)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(16.dp))

                val maxMinutes = sampleWeeklyActivity.maxOf { it.minutes }.coerceAtLeast(1)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    sampleWeeklyActivity.forEach { day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .height(96.dp)
                                    .width(28.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((96 * day.minutes / maxMinutes).dp.coerceAtLeast(4.dp))
                                        .background(
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = day.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monthly Calories (kcal)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(16.dp))

                val maxKcal = sampleMonthlyCalories.maxOf { it.kcal }.coerceAtLeast(1)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    sampleMonthlyCalories.forEach { week ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%,d".format(week.kcal),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.height(6.dp))

                            Box(
                                modifier = Modifier
                                    .height(72.dp)
                                    .width(48.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((72 * week.kcal / maxKcal).dp.coerceAtLeast(4.dp))
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = week.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Records",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(12.dp))

                samplePersonalRecords.forEachIndexed { index, record ->
                    if (index > 0) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = record.title,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = record.value,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = record.date,
                                style = MaterialTheme.typography.bodySmall,
                                color = accentAmberColor()
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    valueColor: Color,
    caption: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = valueColor
            )

            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
