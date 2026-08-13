package week11.st991708650.smartfitnesstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _state = MutableStateFlow(AuthState())

    val state: StateFlow<AuthState> = _state

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun login(email: String, password: String) {
        if (email.isBlank()) {
            _state.value = AuthState(error = "Email is required")
            return
        }

        if (password.isBlank()) {
            _state.value = AuthState(error = "Password is required")
            return
        }

        _state.value = AuthState(loading = true)

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    _state.value = if (task.isSuccessful) {
                        AuthState(success = true)
                    } else {
                        AuthState(error = task.exception?.localizedMessage ?: "Login failed")
                    }
                }
        }
    }

    fun register(email: String, password: String, fullName: String = "") {
        if (fullName.isBlank()) {
            _state.value = AuthState(error = "Full name is required")
            return
        }

        if (email.isBlank()) {
            _state.value = AuthState(error = "Email is required")
            return
        }

        if (password.length < 8) {
            _state.value = AuthState(error = "Password must be at least 8 characters")
            return
        }

        _state.value = AuthState(loading = true)

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val profileUpdate = userProfileChangeRequest {
                            displayName = fullName.trim()
                        }

                        auth.currentUser?.updateProfile(profileUpdate)
                            ?.addOnCompleteListener {
                                _state.value = AuthState(success = true)
                            }
                            ?: run { _state.value = AuthState(success = true) }
                    } else {
                        _state.value = AuthState(
                            error = task.exception?.localizedMessage ?: "Registration failed"
                        )
                    }
                }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _state.value = AuthState(error = "Enter your email address")
            return
        }

        _state.value = AuthState(loading = true)

        viewModelScope.launch {
            auth.sendPasswordResetEmail(email.trim())
                .addOnCompleteListener { task ->
                    _state.value = if (task.isSuccessful) {
                        AuthState(success = true)
                    } else {
                        AuthState(
                            error = task.exception?.localizedMessage ?: "Unable to send reset email"
                        )
                    }
                }
        }
    }

    fun clearState() {
        _state.value = AuthState()
    }
}
