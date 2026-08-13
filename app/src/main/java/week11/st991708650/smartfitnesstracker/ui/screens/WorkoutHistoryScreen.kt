package week11.st991708650.smartfitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import week11.st991708650.smartfitnesstracker.data.model.Workout
import week11.st991708650.smartfitnesstracker.viewmodel.FitnessViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryScreen(
    onBack: () -> Unit,
    viewModel: FitnessViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.observeFitnessData(userId)
        }
    }

    var query by remember { mutableStateOf("") }

    val filteredWorkouts = remember(state.workouts, query) {
        if (query.isBlank()) {
            state.workouts
        } else {
            state.workouts.filter { it.type.contains(query, ignoreCase = true) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search workouts") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge
                )

                Spacer(Modifier.width(8.dp))


                FilledTonalIconButton(onClick = {}) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "${filteredWorkouts.size} sessions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredWorkouts) { workout ->
                    WorkoutHistoryRow(workout)
                }
            }
        }
    }
}

@Composable
private fun WorkoutHistoryRow(workout: Workout) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(workout.type, style = MaterialTheme.typography.titleMedium)

                Text(
                    text = formatWorkoutDate(workout.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("${workout.duration} min", style = MaterialTheme.typography.bodyLarge)

                Text(
                    text = "${workout.calories} kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatWorkoutDate(timestampMillis: Long): String {
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestampMillis))
}
