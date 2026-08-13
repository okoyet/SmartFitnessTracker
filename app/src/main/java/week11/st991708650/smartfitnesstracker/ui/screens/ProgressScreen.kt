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
import week11.st991708650.smartfitnesstracker.data.model.Workout
import week11.st991708650.smartfitnesstracker.ui.components.WeeklyActivityChart
import week11.st991708650.smartfitnesstracker.ui.theme.accentAmberColor
import week11.st991708650.smartfitnesstracker.utils.DAILY_STEP_GOAL
import week11.st991708650.smartfitnesstracker.utils.calculateStreak
import week11.st991708650.smartfitnesstracker.utils.getCurrentMonthYear
import week11.st991708650.smartfitnesstracker.utils.heaviestLift
import week11.st991708650.smartfitnesstracker.utils.longestRun
import week11.st991708650.smartfitnesstracker.utils.monthlyCalories
import week11.st991708650.smartfitnesstracker.viewmodel.FitnessViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val workouts = state.workouts
    val goalPercent = (((stats?.steps ?: 0) / DAILY_STEP_GOAL.toFloat()).coerceIn(0f, 1f) * 100).toInt()
    val streak = calculateStreak(workouts)

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
                caption = "Today"
            )

            StatTile(
                modifier = Modifier.weight(1f),
                label = "STREAK",
                value = "${streak}d",
                valueColor = accentAmberColor(),
                caption = "days"
            )

            StatTile(
                modifier = Modifier.weight(1f),
                label = "SESSIONS",
                value = "${workouts.size}",
                valueColor = MaterialTheme.colorScheme.primary,
                caption = "total"
            )
        }

        Spacer(Modifier.height(16.dp))

        WeeklyActivityChart(
            workouts = workouts,
            title = "Weekly Activity (min)",
            highlightToday = false
        )

        Spacer(Modifier.height(16.dp))

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Monthly Calories (kcal)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(16.dp))

                val monthly = monthlyCalories(workouts)
                val maxKcal = monthly.maxOf { it.second }.coerceAtLeast(1)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    monthly.forEach { (label, kcal) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%,d".format(kcal),
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
                                if (kcal > 0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height((72 * kcal / maxKcal).dp.coerceAtLeast(4.dp))
                                            .background(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = MaterialTheme.shapes.extraSmall
                                            )
                                    )
                                }
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = label,
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

                PersonalRecordRow(
                    title = "Longest run",
                    workout = longestRun(workouts),
                    formatValue = { "%.1f km".format(it.distanceKm) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                PersonalRecordRow(
                    title = "Heaviest lift",
                    workout = heaviestLift(workouts),
                    formatValue = { "%.0f kg".format(it.weightLiftedKg) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PersonalRecordRow(
    title: String,
    workout: Workout?,
    formatValue: (Workout) -> String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)

        if (workout == null) {
            Text(
                text = "No data yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatValue(workout),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = formatRecordDate(workout.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = accentAmberColor()
                )
            }
        }
    }
}

private fun formatRecordDate(timestampMillis: Long): String {
    return SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestampMillis))
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
