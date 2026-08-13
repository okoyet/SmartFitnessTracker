package week11.st991708650.smartfitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import week11.st991708650.smartfitnesstracker.data.model.Workout
import week11.st991708650.smartfitnesstracker.viewmodel.FitnessViewModel

private data class WorkoutCategory(
    val label: String,
    val caloriesPerMinute: Double,
    val kmPerMinute: Double
)

private val workoutCategories = listOf(
    WorkoutCategory("Running", caloriesPerMinute = 10.0, kmPerMinute = 0.17),
    WorkoutCategory("Walking", caloriesPerMinute = 5.0, kmPerMinute = 0.08),
    WorkoutCategory("Cycling", caloriesPerMinute = 8.0, kmPerMinute = 0.30),
    WorkoutCategory("Strength", caloriesPerMinute = 6.0, kmPerMinute = 0.0),
    WorkoutCategory("Yoga", caloriesPerMinute = 3.0, kmPerMinute = 0.0)
)

@Composable
fun WorkoutScreen(
    onOpenHistory: () -> Unit = {},
    viewModel: FitnessViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var selectedCategory by remember { mutableStateOf(workoutCategories.first()) }
    var isRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var showSavedBanner by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000)
            elapsedSeconds++
        }
    }

    LaunchedEffect(showSavedBanner) {
        if (showSavedBanner) {
            delay(2500)
            showSavedBanner = false
        }
    }

    val calories = (elapsedSeconds / 60.0 * selectedCategory.caloriesPerMinute).toInt()
    val distanceKm = elapsedSeconds / 60.0 * selectedCategory.kmPerMinute

    val status = when {
        isRunning -> "In Progress"
        elapsedSeconds > 0 -> "Paused"
        else -> "Ready"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workout Tracker",
                style = MaterialTheme.typography.headlineMedium
            )

            IconButton(onClick = onOpenHistory) {
                Icon(Icons.Default.History, contentDescription = "Workout history")
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(workoutCategories) { category ->
                val selected = category == selectedCategory

                FilterChip(
                    selected = selected,
                    onClick = {
                        if (!isRunning) {
                            selectedCategory = category
                        }
                    },
                    label = { Text(category.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ELAPSED TIME",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = formatElapsed(elapsedSeconds),
                    style = MaterialTheme.typography.displayMedium
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CALORIES",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(calories.toString(), style = MaterialTheme.typography.headlineSmall)

                        Text(
                            text = "kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    VerticalDivider(modifier = Modifier.height(56.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "DISTANCE",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "%.2f".format(distanceKm),
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = "km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { isRunning = !isRunning },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = when {
                            isRunning -> "Pause"
                            elapsedSeconds > 0 -> "Resume"
                            else -> "Start"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (!isRunning && elapsedSeconds > 0) {
                    Spacer(Modifier.height(8.dp))

                    TextButton(
                        onClick = {
                            if (userId != null) {
                                viewModel.addWorkout(
                                    Workout(
                                        userId = userId,
                                        type = selectedCategory.label,
                                        duration = elapsedSeconds / 60,
                                        calories = calories
                                    )
                                )
                            }

                            isRunning = false
                            elapsedSeconds = 0
                            showSavedBanner = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finish Workout")
                    }
                }
            }
        }

        if (showSavedBanner) {
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Workout saved")
                }
            }
        }
    }
}

private fun formatElapsed(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
