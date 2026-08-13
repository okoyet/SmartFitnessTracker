package week11.st991708650.smartfitnesstracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st991708650.smartfitnesstracker.data.model.DailyStats
import week11.st991708650.smartfitnesstracker.data.model.Workout
import week11.st991708650.smartfitnesstracker.data.repository.FirestoreRepository

data class FitnessUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val dailyStats: DailyStats? = null,
    val workouts: List<Workout> = emptyList()
)

class FitnessViewModel : ViewModel() {

    private val repository = FirestoreRepository()

    private val _uiState = MutableStateFlow(FitnessUiState())

    val uiState: StateFlow<FitnessUiState> = _uiState

    fun observeFitnessData(userId: String) {
        viewModelScope.launch {
            repository.getDailyStats(userId).collect { stats ->
                _uiState.value = _uiState.value.copy(dailyStats = stats)
            }
        }

        viewModelScope.launch {
            repository.getWorkouts(userId).collect { workouts ->
                _uiState.value = _uiState.value.copy(workouts = workouts)
            }
        }
    }

    fun saveDailyStats(stats: DailyStats) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.saveDailyStats(stats)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun addWorkout(workout: Workout) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.addWorkout(workout)

            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false)
            } else {
                _uiState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            val result = repository.updateWorkout(workout)

            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            val result = repository.deleteWorkout(workout)

            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }
}
