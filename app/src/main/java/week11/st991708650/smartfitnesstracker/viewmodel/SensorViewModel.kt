package week11.st991708650.smartfitnesstracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow
import week11.st991708650.smartfitnesstracker.data.model.FitnessSensorData
import week11.st991708650.smartfitnesstracker.data.repository.SensorRepository

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SensorRepository(application.applicationContext)

    val sensorData: StateFlow<FitnessSensorData> = repository.sensorData

    fun startSensors() {
        repository.startSensors()
    }

    fun stopSensors() {
        repository.stopSensors()
    }

    override fun onCleared() {
        repository.stopSensors()
        super.onCleared()
    }
}
