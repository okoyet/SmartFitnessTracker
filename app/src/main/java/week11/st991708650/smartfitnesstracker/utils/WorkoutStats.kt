package week11.st991708650.smartfitnesstracker.utils

import week11.st991708650.smartfitnesstracker.data.model.Workout
import java.util.Calendar



private fun startOfDay(millis: Long): Calendar {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

private fun dayKey(millis: Long): Long = startOfDay(millis).timeInMillis


fun calculateStreak(workouts: List<Workout>): Int {
    if (workouts.isEmpty()) return 0

    val workoutDays = workouts.map { dayKey(it.timestamp) }.toSet()

    val cursor = startOfDay(System.currentTimeMillis())

    // If nothing logged today, the streak can still be "alive" through
    // yesterday - only start counting from there.
    if (cursor.timeInMillis !in workoutDays) {
        cursor.add(Calendar.DAY_OF_YEAR, -1)
    }

    var streak = 0
    while (cursor.timeInMillis in workoutDays) {
        streak++
        cursor.add(Calendar.DAY_OF_YEAR, -1)
    }

    return streak
}


fun weeklyActivityMinutes(workouts: List<Workout>): List<Pair<String, Int>> {
    val labels = listOf("M", "T", "W", "T", "F", "S", "S")

    val monday = startOfDay(System.currentTimeMillis()).apply {
        // DAY_OF_WEEK is Sunday=1..Saturday=7; steps back to this week's Monday.
        val daysSinceMonday = (get(Calendar.DAY_OF_WEEK) + 5) % 7
        add(Calendar.DAY_OF_YEAR, -daysSinceMonday)
    }

    return labels.mapIndexed { index, label ->
        val dayStart = (monday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, index) }
        val dayEnd = (dayStart.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }

        val minutes = workouts
            .filter { it.timestamp >= dayStart.timeInMillis && it.timestamp < dayEnd.timeInMillis }
            .sumOf { it.duration }

        label to minutes
    }
}


fun monthlyCalories(workouts: List<Workout>): List<Pair<String, Int>> {
    val now = System.currentTimeMillis()
    val msPerDay = 24 * 60 * 60 * 1000L
    val buckets = IntArray(4)

    for (workout in workouts) {
        val ageDays = ((now - workout.timestamp) / msPerDay).toInt()
        if (ageDays < 0 || ageDays >= 28) continue

        val weeksAgo = ageDays / 7
        val bucketIndex = 3 - weeksAgo

        buckets[bucketIndex] += workout.calories
    }

    return listOf("W1", "W2", "W3", "W4").mapIndexed { index, label -> label to buckets[index] }
}


fun longestRun(workouts: List<Workout>): Workout? {
    return workouts
        .filter { it.type == "Running" && it.distanceKm > 0 }
        .maxByOrNull { it.distanceKm }
}


fun heaviestLift(workouts: List<Workout>): Workout? {
    return workouts
        .filter { it.type == "Strength" && it.weightLiftedKg > 0 }
        .maxByOrNull { it.weightLiftedKg }
}
