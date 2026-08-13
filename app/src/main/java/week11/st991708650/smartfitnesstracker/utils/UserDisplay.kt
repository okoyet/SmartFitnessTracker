package week11.st991708650.smartfitnesstracker.utils

import com.google.firebase.auth.FirebaseUser


fun displayNameFor(user: FirebaseUser?): String {
    return user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        ?: "Guest"
}

fun initialsFor(displayName: String): String {
    return displayName
        .split(" ", ".", "_")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }
}
