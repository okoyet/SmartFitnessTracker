package week11.st991708650.smartfitnesstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st991708650.smartfitnesstracker.data.repository.FirestoreRepository

data class AuthState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

data class DeleteAccountState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val needsReauth: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestoreRepository = FirestoreRepository()

    private val _state = MutableStateFlow(AuthState())

    val state: StateFlow<AuthState> = _state

    private val _deleteAccountState = MutableStateFlow(DeleteAccountState())

    val deleteAccountState: StateFlow<DeleteAccountState> = _deleteAccountState

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


    fun deleteAccount() {
        val user = auth.currentUser
        if (user == null) {
            _deleteAccountState.value = DeleteAccountState(error = "Not signed in")
            return
        }

        _deleteAccountState.value = DeleteAccountState(loading = true)

        viewModelScope.launch {
            val dataResult = firestoreRepository.deleteAllUserData(user.uid)

            if (dataResult.isFailure) {
                _deleteAccountState.value = DeleteAccountState(
                    error = dataResult.exceptionOrNull()?.localizedMessage
                        ?: "Unable to delete your data"
                )
                return@launch
            }

            user.delete()
                .addOnCompleteListener { task ->
                    _deleteAccountState.value = when {
                        task.isSuccessful -> DeleteAccountState(success = true)
                        task.exception is FirebaseAuthRecentLoginRequiredException ->
                            DeleteAccountState(needsReauth = true)
                        else -> DeleteAccountState(
                            error = task.exception?.localizedMessage ?: "Unable to delete account"
                        )
                    }
                }
        }
    }

    fun reauthenticateAndDelete(password: String) {
        val user = auth.currentUser
        val email = user?.email

        if (user == null || email == null) {
            _deleteAccountState.value = DeleteAccountState(error = "Not signed in")
            return
        }

        _deleteAccountState.value = DeleteAccountState(loading = true)

        val credential = EmailAuthProvider.getCredential(email, password)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteAccount()
                } else {
                    _deleteAccountState.value = DeleteAccountState(
                        error = task.exception?.localizedMessage ?: "Re-authentication failed"
                    )
                }
            }
    }

    fun clearDeleteAccountState() {
        _deleteAccountState.value = DeleteAccountState()
    }
}
