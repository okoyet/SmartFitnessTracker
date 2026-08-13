package week11.st991708650.smartfitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import week11.st991708650.smartfitnesstracker.viewmodel.AuthViewModel

private const val APP_VERSION = "Version 2.4.1"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    // Local-only, in-memory toggles - not wired to real Android notification
    // channels/permissions and not persisted. Dark mode is the one real,
    // working preference here (it actually drives the app theme).
    var pushNotifications by remember { mutableStateOf(true) }
    var workoutReminders by remember { mutableStateOf(true) }

    var resetEmailSent by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionLabel("PREFERENCES")

            SettingsSwitchRow(
                label = "Push notifications",
                checked = pushNotifications,
                onCheckedChange = { pushNotifications = it }
            )

            Spacer(Modifier.height(8.dp))

            SettingsSwitchRow(
                label = "Workout reminders",
                checked = workoutReminders,
                onCheckedChange = { workoutReminders = it }
            )

            Spacer(Modifier.height(8.dp))

            SettingsSwitchRow(
                label = "Dark mode",
                checked = darkModeEnabled,
                onCheckedChange = onDarkModeChange
            )

            Spacer(Modifier.height(24.dp))

            SectionLabel("ACCOUNT")

            SettingsNavRow(
                label = "Change password",
                onClick = {
                    FirebaseAuth.getInstance().currentUser?.email?.let { email ->
                        authViewModel.resetPassword(email)
                        resetEmailSent = true
                    }
                }
            )

            if (resetEmailSent) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Password reset email sent.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // No privacy settings screen exists yet.
            SettingsNavRow(label = "Privacy settings", onClick = {})

            Spacer(Modifier.height(24.dp))

            SectionLabel("SUPPORT")

            // No about/rating destinations wired up yet.
            SettingsNavRow(label = "About Smart Fitness Tracker", onClick = {})

            Spacer(Modifier.height(8.dp))

            SettingsNavRow(label = "Rate the app", onClick = {})

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete account")
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = APP_VERSION,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete account?") },
            text = {
                Text(
                    "This will sign you out. Full account deletion isn't available yet - " +
                        "contact support to permanently remove your data."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        // Real account + Firestore data deletion isn't implemented -
                        // that needs re-authentication and a server-side cleanup step
                        // (e.g. a Cloud Function) to remove the user's subcollections
                        // safely. Signing out is the closest safe action for now.
                        FirebaseAuth.getInstance().signOut()
                    }
                ) {
                    Text("Sign out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingsNavRow(label: String, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
