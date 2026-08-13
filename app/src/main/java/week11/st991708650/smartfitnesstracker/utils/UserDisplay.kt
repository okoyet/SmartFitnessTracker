package week11.st991708650.smartfitnesstracker.utils

import com.google.firebase.auth.FirebaseUser

/**
 * Best-effort display name: Firebase displayName if set, otherwise the email's
 * local part capitalized, otherwise "Guest". Email/password sign-in doesn't
 * collect a real name anywhere in this app, so this is usually the email fallback.
 */
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
