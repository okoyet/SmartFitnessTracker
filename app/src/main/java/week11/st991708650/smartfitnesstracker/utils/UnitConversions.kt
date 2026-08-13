package week11.st991708650.smartfitnesstracker.utils

import kotlin.math.roundToInt


fun formatHeight(heightCm: Int): String {
    if (heightCm <= 0) return "Not set"

    val totalInches = (heightCm / 2.54).roundToInt()
    val feet = totalInches / 12
    val inches = totalInches % 12

    return "$feet'$inches\" · $heightCm cm"
}


fun formatWeight(weightKg: Int): String {
    if (weightKg <= 0) return "Not set"

    val pounds = (weightKg * 2.20462).roundToInt()

    return "$pounds lbs · $weightKg kg"
}

fun formatAge(age: Int): String {
    return if (age <= 0) "Not set" else "$age years"
}
