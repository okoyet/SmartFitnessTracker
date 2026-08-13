package week11.st991708650.smartfitnesstracker.data.model

data class DailyStats(
    val userId: String = "",
    val date: String = "",
    val steps: Int = 0,
    val calories: Int = 0,
    val activeMinutes: Int = 0
)
