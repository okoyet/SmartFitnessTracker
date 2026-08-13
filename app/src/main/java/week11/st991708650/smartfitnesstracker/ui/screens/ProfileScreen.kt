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
import week11.st991708650.smartfitnesstracker.utils.calculateStreak
import week11.st991708650.smartfitnesstracker.utils.displayNameFor
import week11.st991708650.smartfitnesstracker.utils.formatAge
import week11.st991708650.smartfitnesstracker.utils.formatHeight
import week11.st991708650.smartfitnesstracker.utils.formatWeight
import week11.st991708650.smartfitnesstracker.utils.initialsFor
import week11.st991708650.smartfitnesstracker.viewmodel.FitnessViewModel
import week11.st991708650.smartfitnesstracker.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEditProfile: () -> Unit,
    fitnessViewModel: FitnessViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val state by fitnessViewModel.uiState.collectAsState()
    val userProfile by profileViewModel.userProfile.collectAsState()

    LaunchedEffect(user?.uid) {
        user?.uid?.let {
            fitnessViewModel.observeFitnessData(it)
            profileViewModel.observeUserProfile(it)
        }
    }

    val displayName = displayNameFor(user)
    val initials = initialsFor(displayName)

    val totalWorkouts = state.workouts.size
    val totalHours = state.workouts.sumOf { it.duration } / 60
    val streak = calculateStreak(state.workouts)

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

                    ProfileStat(
                        modifier = Modifier.weight(1f),
                        value = "${streak}d",
                        label = "Streak",
                        valueColor = accentAmberColor()
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                ProfileInfoRow(label = "Height", value = formatHeight(userProfile.heightCm))
                HorizontalDivider()
                ProfileInfoRow(label = "Weight", value = formatWeight(userProfile.weightKg))
                HorizontalDivider()
                ProfileInfoRow(label = "Age", value = formatAge(userProfile.age))
                HorizontalDivider()
                ProfileInfoRow(
                    label = "Goal",
                    value = userProfile.goal.ifBlank { "Not set" }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onOpenEditProfile,
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
