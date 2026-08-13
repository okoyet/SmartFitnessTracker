package week11.st991708650.smartfitnesstracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SensorStatusCard(isMoving: Boolean) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Motion Sensor")

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (isMoving) "Activity detected" else "No activity detected"
            )
        }
    }
}
