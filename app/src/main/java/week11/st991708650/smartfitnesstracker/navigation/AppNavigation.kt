package week11.st991708650.smartfitnesstracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import week11.st991708650.smartfitnesstracker.ui.screens.ForgotPasswordScreen
import week11.st991708650.smartfitnesstracker.ui.screens.LoginScreen
import week11.st991708650.smartfitnesstracker.ui.screens.MainAppScreen
import week11.st991708650.smartfitnesstracker.ui.screens.RegisterScreen
import week11.st991708650.smartfitnesstracker.ui.screens.SettingsScreen
import week11.st991708650.smartfitnesstracker.ui.screens.SplashScreen
import week11.st991708650.smartfitnesstracker.ui.screens.WorkoutHistoryScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    darkModeEnabled: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                goRegister = {
                    navController.navigate(Screen.Register.route)
                },
                goForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            MainAppScreen(
                onLogout = {
                    FirebaseAuth.getInstance().signOut()

                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOpenSettings = { navController.navigate(Screen.Settings.route) },
                onOpenWorkoutHistory = { navController.navigate(Screen.WorkoutHistory.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                darkModeEnabled = darkModeEnabled,
                onDarkModeChange = onDarkModeChange
            )
        }

        composable(Screen.WorkoutHistory.route) {
            WorkoutHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
