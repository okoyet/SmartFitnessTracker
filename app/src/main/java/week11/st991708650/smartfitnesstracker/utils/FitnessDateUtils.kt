package week11.st991708650.smartfitnesstracker.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getTodayDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

fun getCurrentMonthYear(): String {
    return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())
}
