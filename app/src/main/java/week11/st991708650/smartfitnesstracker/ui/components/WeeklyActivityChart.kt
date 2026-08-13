package week11.st991708650.smartfitnesstracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import week11.st991708650.smartfitnesstracker.data.model.Workout
import week11.st991708650.smartfitnesstracker.utils.weeklyActivityMinutes
import java.util.Calendar

private fun todayIndex(): Int {
    // Calendar.DAY_OF_WEEK is Sunday=1..Saturday=7; convert to Monday=0..Sunday=6.
    val calendarDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    return (calendarDay + 5) % 7
}

/**
 * Real per-weekday activity minutes for the current week, derived from the
 * user's actual saved workouts - all bars start at zero on a fresh account.
 */
@Composable
fun WeeklyActivityChart(
    workouts: List<Workout>,
    title: String = "This Week",
    highlightToday: Boolean = true
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(16.dp))

            val data = weeklyActivityMinutes(workouts)
            val maxMinutes = data.maxOf { it.second }.coerceAtLeast(1)
            val today = if (highlightToday) todayIndex() else -1

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                data.forEachIndexed { index, (label, minutes) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .height(72.dp)
                                .width(24.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            if (minutes > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((72 * minutes / maxMinutes).dp.coerceAtLeast(4.dp))
                                        .background(
                                            color = when {
                                                !highlightToday -> MaterialTheme.colorScheme.primary
                                                index == today -> MaterialTheme.colorScheme.primary
                                                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                            },
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                )
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
