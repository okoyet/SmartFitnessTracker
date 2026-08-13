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
import week11.st991708650.smartfitnesstracker.ui.components.PasswordTextField
import week11.st991708650.smartfitnesstracker.viewmodel.AuthViewModel

private const val APP_VERSION = "Version 1.0.0"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAccountDeleted: () -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var resetEmailSent by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reauthPassword by remember { mutableStateOf("") }

    val deleteState by authViewModel.deleteAccountState.collectAsState()

    LaunchedEffect(deleteState.success) {
        if (deleteState.success) {
            onAccountDeleted()
        }
    }

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

            Spacer(Modifier.height(24.dp))

            if (deleteState.loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
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
            }

            deleteState.error?.let { error ->
                Spacer(Modifier.height(8.dp))

                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
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
                    "This permanently deletes your account and all your workout data. " +
                        "This can't be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        authViewModel.deleteAccount()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (deleteState.needsReauth) {
        AlertDialog(
            onDismissRequest = { authViewModel.clearDeleteAccountState() },
            title = { Text("Confirm your password") },
            text = {
                Column {
                    Text("For your security, please re-enter your password to delete your account.")

                    Spacer(Modifier.height(12.dp))

                    PasswordTextField(
                        password = reauthPassword,
                        onPasswordChange = { reauthPassword = it }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.reauthenticateAndDelete(reauthPassword)
                        reauthPassword = ""
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        reauthPassword = ""
                        authViewModel.clearDeleteAccountState()
                    }
                ) {
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


@Suppress("SameParameterValue")
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

// Same story as SettingsSwitchRow above - generic, currently single-use.
@Suppress("SameParameterValue")
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
