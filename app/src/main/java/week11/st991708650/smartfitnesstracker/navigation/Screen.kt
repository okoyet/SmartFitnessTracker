package week11.st991708650.smartfitnesstracker.navigation

sealed class Screen(val route: String) {

    data object Splash : Screen("splash")

    data object Login : Screen("login")

    data object Register : Screen("register")

    data object ForgotPassword : Screen("forgot_password")

    data object Home : Screen("home")

    data object Workout : Screen("workout")

    data object Progress : Screen("progress")

    data object Profile : Screen("profile")

    data object Settings : Screen("settings")

    data object WorkoutHistory : Screen("workout_history")
}