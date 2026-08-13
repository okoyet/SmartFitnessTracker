package week11.st991708650.smartfitnesstracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onWorkoutClick: () -> Unit,
    onProgressClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "workout",
            onClick = onWorkoutClick,
            icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
            label = { Text("Workout") }
        )

        NavigationBarItem(
            selected = currentRoute == "progress",
            onClick = onProgressClick,
            icon = { Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null) },
            label = { Text("Progress") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = onProfileClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") }
        )
    }
}
