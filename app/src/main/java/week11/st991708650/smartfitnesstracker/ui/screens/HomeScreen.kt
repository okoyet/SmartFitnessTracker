package week11.st991708650.smartfitnesstracker.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import week11.st991708650.smartfitnesstracker.ui.components.*
import week11.st991708650.smartfitnesstracker.ui.theme.accentAmberColor
import week11.st991708650.smartfitnesstracker.utils.displayNameFor
import week11.st991708650.smartfitnesstracker.viewmodel.SensorViewModel

@Composable
fun HomeScreen(
    sensorViewModel: SensorViewModel = viewModel(),
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToProgress: () -> Unit = {},
    onNavigateToWorkoutHistory: () -> Unit = {}
) {
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            sensorViewModel.startSensors()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            sensorViewModel.startSensors()
        }
    }

    val sensorData by sensorViewModel.sensorData.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            sensorViewModel.stopSensors()
        }
    }

    val displayName = displayNameFor(FirebaseAuth.getInstance().currentUser)
    val goalPercent = ((sensorData.steps / 10000f).coerceIn(0f, 1f) * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader(userName = displayName)

        Spacer(Modifier.height(24.dp))

        GoalCard(goalPercent = goalPercent)

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                label = "STEPS",
                value = "%,d".format(sensorData.steps),
                caption = "of 10,000",
                valueColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                label = "CALORIES",
                value = "${sensorData.calories}",
                caption = "kcal",
                valueColor = accentAmberColor(),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                label = "ACTIVE",
                value = "${sensorData.activeMinutes}",
                caption = "min",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        WeeklyChart()

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Quick actions",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        QuickActionCard(
            title = "Start Workout",
            subtitle = "Begin a new session",
            onClick = onNavigateToWorkout
        )

        Spacer(Modifier.height(8.dp))

        QuickActionCard(
            title = "View Progress",
            subtitle = "Charts and streaks",
            onClick = onNavigateToProgress
        )

        Spacer(Modifier.height(8.dp))

        QuickActionCard(
            title = "Workout History",
            subtitle = "Past sessions",
            onClick = onNavigateToWorkoutHistory
        )

        Spacer(Modifier.height(16.dp))
    }
}
