package week11.st991708650.smartfitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import week11.st991708650.smartfitnesstracker.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val currentDisplayName = FirebaseAuth.getInstance().currentUser?.displayName.orEmpty()

    val profile by viewModel.userProfile.collectAsState()
    val editState by viewModel.editState.collectAsState()

    LaunchedEffect(userId) {
        userId?.let { viewModel.observeUserProfile(it) }
    }

    var fullName by remember { mutableStateOf(currentDisplayName) }
    var heightCm by remember { mutableStateOf("") }
    var weightKg by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var fieldsPrefilled by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        if (!fieldsPrefilled) {
            heightCm = if (profile.heightCm > 0) profile.heightCm.toString() else ""
            weightKg = if (profile.weightKg > 0) profile.weightKg.toString() else ""
            age = if (profile.age > 0) profile.age.toString() else ""
            goal = profile.goal
            fieldsPrefilled = true
        }
    }

    LaunchedEffect(editState.isSaved) {
        if (editState.isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
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
            TextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = heightCm,
                    onValueChange = { if (it.length <= 3) heightCm = it.filter(Char::isDigit) },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                TextField(
                    value = weightKg,
                    onValueChange = { if (it.length <= 3) weightKg = it.filter(Char::isDigit) },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            TextField(
                value = age,
                onValueChange = { if (it.length <= 3) age = it.filter(Char::isDigit) },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            TextField(
                value = goal,
                onValueChange = { goal = it },
                label = { Text("Goal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            if (editState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    onClick = {
                        if (userId != null) {
                            viewModel.saveProfile(
                                userId = userId,
                                fullName = fullName,
                                heightCm = heightCm.toIntOrNull() ?: 0,
                                weightKg = weightKg.toIntOrNull() ?: 0,
                                age = age.toIntOrNull() ?: 0,
                                goal = goal
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Save changes", style = MaterialTheme.typography.titleMedium)
                }
            }

            editState.error?.let { error ->
                Spacer(Modifier.height(12.dp))

                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
