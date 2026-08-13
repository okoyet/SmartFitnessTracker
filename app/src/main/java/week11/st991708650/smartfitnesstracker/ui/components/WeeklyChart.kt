package week11.st991708650.smartfitnesstracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar

private data class WeeklyChartDay(val label: String, val minutes: Int)

// Sample data: there's no per-day activity history query yet, so this shows
// placeholder minutes shaped like the prototype rather than real history.
private val sampleWeek = listOf(
    WeeklyChartDay("M", 25),
    WeeklyChartDay("T", 32),
    WeeklyChartDay("W", 2),
    WeeklyChartDay("T", 28),
    WeeklyChartDay("F", 45),
    WeeklyChartDay("S", 20),
    WeeklyChartDay("S", 3)
)

private fun todayIndex(): Int {
    // Calendar.DAY_OF_WEEK is Sunday=1..Saturday=7; convert to Monday=0..Sunday=6.
    val calendarDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return (calendarDay + 5) % 7
}

@Composable
fun WeeklyChart() {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            val maxMinutes = sampleWeek.maxOf { it.minutes }.coerceAtLeast(1)
            val today = todayIndex()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sampleWeek.forEachIndexed { index, day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .height(72.dp)
                                .width(24.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((72 * day.minutes / maxMinutes).dp.coerceAtLeast(4.dp))
                                    .background(
                                        color = if (index == today) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                        },
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = day.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
