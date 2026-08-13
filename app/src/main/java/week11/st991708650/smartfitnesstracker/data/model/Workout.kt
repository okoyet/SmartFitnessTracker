package week11.st991708650.smartfitnesstracker.data.model

data class Workout(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val duration: Int = 0,
    val steps: Int = 0,
    val calories: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
