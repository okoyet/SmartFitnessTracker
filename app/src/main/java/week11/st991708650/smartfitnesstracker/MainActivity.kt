package week11.st991708650.smartfitnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import week11.st991708650.smartfitnesstracker.navigation.AppNavigation
import week11.st991708650.smartfitnesstracker.ui.theme.SmartFitnessTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Manual dark-mode override from the Settings screen. Not persisted
            // across app restarts (no DataStore/SharedPreferences wired up yet).
            var darkModeEnabled by rememberSaveable { mutableStateOf(false) }

            SmartFitnessTrackerTheme(darkTheme = darkModeEnabled) {
                AppNavigation(
                    darkModeEnabled = darkModeEnabled,
                    onDarkModeChange = { darkModeEnabled = it }
                )
            }
        }
    }
}
