package week11.st991708650.smartfitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import week11.st991708650.smartfitnesstracker.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password") },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
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
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            if (state.loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        viewModel.resetPassword(email)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Reset Email")
                }
            }

            state.error?.let { error ->
                Spacer(Modifier.height(10.dp))

                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (state.success) {
                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Password reset email sent successfully.",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
