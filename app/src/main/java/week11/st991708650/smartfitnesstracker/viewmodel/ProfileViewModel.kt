package week11.st991708650.smartfitnesstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import week11.st991708650.smartfitnesstracker.data.model.UserProfile
import week11.st991708650.smartfitnesstracker.data.repository.FirestoreRepository

data class ProfileEditState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class ProfileViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState

    fun observeUserProfile(userId: String) {
        viewModelScope.launch {
            repository.getUserProfile(userId).collect { profile ->
                _userProfile.value = profile ?: UserProfile(userId = userId)
            }
        }
    }

    fun saveProfile(
        userId: String,
        fullName: String,
        heightCm: Int,
        weightKg: Int,
        age: Int,
        goal: String
    ) {
        _editState.value = ProfileEditState(isLoading = true)

        viewModelScope.launch {
            val nameUpdate = userProfileChangeRequest {
                displayName = fullName.trim()
            }

            val auth = FirebaseAuth.getInstance()

            try {
                auth.currentUser?.updateProfile(nameUpdate)?.await()
            } catch (e: Exception) {
                _editState.value = ProfileEditState(
                    error = e.localizedMessage ?: "Unable to update name"
                )
                return@launch
            }

            val result = repository.saveUserProfile(
                UserProfile(
                    userId = userId,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    age = age,
                    goal = goal
                )
            )

            _editState.value = if (result.isSuccess) {
                ProfileEditState(isSaved = true)
            } else {
                ProfileEditState(
                    error = result.exceptionOrNull()?.localizedMessage ?: "Unable to save profile"
                )
            }
        }
    }

    fun clearEditState() {
        _editState.value = ProfileEditState()
    }
}
