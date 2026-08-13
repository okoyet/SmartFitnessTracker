package week11.st991708650.smartfitnesstracker.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import week11.st991708650.smartfitnesstracker.data.model.FitnessSensorData
import kotlin.math.sqrt

class SensorRepository(context: Context) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _sensorData = MutableStateFlow(FitnessSensorData())

    val sensorData: StateFlow<FitnessSensorData> = _sensorData

    private var initialSteps = -1

    private var movementDetected = false

    private var movementStartTime = 0L

    fun hasStepCounter(): Boolean {
        return stepSensor != null
    }

    fun hasAccelerometer(): Boolean {
        return accelerometer != null
    }

    fun startSensors() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopSensors() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalSteps = event.values[0].toInt()

                if (initialSteps == -1) {
                    initialSteps = totalSteps
                }

                val steps = totalSteps - initialSteps

                updateFitnessData(steps = steps)
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val magnitude = sqrt(x * x + y * y + z * z)

                val moving = magnitude > 11f || magnitude < 8f

                if (moving) {
                    if (!movementDetected) {
                        movementDetected = true
                        movementStartTime = System.currentTimeMillis()
                    }
                } else {
                    movementDetected = false
                }

                updateFitnessData(isMoving = moving)
            }
        }
    }

    private fun updateFitnessData(steps: Int? = null, isMoving: Boolean? = null) {
        val current = _sensorData.value

        val updatedSteps = steps ?: current.steps

        val updatedMoving = isMoving ?: current.isMoving

        val calories = (updatedSteps * 0.04).toInt()

        val activeMinutes = if (updatedMoving) {
            ((System.currentTimeMillis() - movementStartTime) / 60000)
                .toInt()
                .coerceAtLeast(1)
        } else {
            current.activeMinutes
        }

        _sensorData.value = current.copy(
            steps = updatedSteps,
            calories = calories,
            activeMinutes = activeMinutes,
            isMoving = updatedMoving
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not required
    }
}
