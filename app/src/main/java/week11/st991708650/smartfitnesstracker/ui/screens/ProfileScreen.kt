package week11.st991708650.smartfitnesstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import week11.st991708650.smartfitnesstracker.ui.theme.accentAmberColor
import week11.st991708650.smartfitnesstracker.utils.displayNameFor
import week11.st991708650.smartfitnesstracker.utils.initialsFor
import week11.st991708650.smartfitnesstracker.viewmodel.FitnessViewModel

// Placeholder body-metric and membership fields - there's no profile document
// or editable-body-metrics feature in the data model yet, so these render as
// static sample content matching the prototype until that's built.
private const val SAMPLE_HEIGHT = "5'10\" · 178 cm"
private const val SAMPLE_WEIGHT = "165 lbs · 75 kg"
private const val SAMPLE_AGE = "28 years"
private const val SAMPLE_GOAL = "Build Muscle & Endurance"
private const val SAMPLE_STREAK_DAYS = 12

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: FitnessViewModel = viewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(user?.uid) {
        user?.uid?.let { viewModel.observeFitnessData(it) }
    }

    val displayName = displayNameFor(user)
    val initials = initialsFor(displayName)

    val totalWorkouts = state.workouts.size
    val totalHours = state.workouts.sumOf { it.duration } / 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = user?.email ?: "No email",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // "Pro Member" is decorative - there's no membership
                        // tier concept in the app yet.
                        Text(
                            text = "Pro Member",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        value = "$totalWorkouts",
                        label = "Workouts",
                        valueColor = MaterialTheme.colorScheme.primary
                    )

                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        value = "$totalHours",
                        label = "Hours",
                        valueColor = MaterialTheme.colorScheme.primary
                    )

                    // Streak isn't tracked yet - same placeholder as the
                    // Progress tab's streak tile.
                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        value = "${SAMPLE_STREAK_DAYS}d",
                        label = "Streak",
                        valueColor = accentAmberColor()
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                ProfileInfoRow(label = "Height", value = SAMPLE_HEIGHT)
                HorizontalDivider()
                ProfileInfoRow(label = "Weight", value = SAMPLE_WEIGHT)
                HorizontalDivider()
                ProfileInfoRow(label = "Age", value = SAMPLE_AGE)
                HorizontalDivider()
                ProfileInfoRow(label = "Goal", value = SAMPLE_GOAL)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Edit profile has no destination yet - there's no editable body-metrics
        // form or profile document to write to.
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit profile")
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenSettings),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Settings", style = MaterialTheme.typography.bodyLarge)
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Sign out")
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileStat(
    value: String,
    label: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = valueColor
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
