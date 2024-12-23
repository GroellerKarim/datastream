package eu.groeller.datastreamui.screens.workout

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eu.groeller.datastreamui.data.model.ExerciseRecordResponse
import eu.groeller.datastreamui.data.model.ExerciseSetResponse
import eu.groeller.datastreamui.data.model.ExerciseType
import java.time.Duration
import java.time.OffsetDateTime

@SuppressLint("DefaultLocale")
private fun createRestTimeString(start: OffsetDateTime, end: OffsetDateTime): String {
    val duration = Duration.between(start, end)
    val minutes = duration.toMinutes()
    val seconds = duration.toSecondsPart()
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun SingleExerciseCard(exercise: ExerciseRecordResponse) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        Column {
            // Main row (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = exercise.exerciseName)
                    Text(
                        text = when (exercise.type) {
                            ExerciseType.DISTANCE -> {
                                val distance = exercise.details.distance ?: 0.0
                                String.format("• %.1f km", distance)
                            }
                            ExerciseType.SETS_REPS, ExerciseType.SETS_TIME -> {
                                val setCount = exercise.details.sets?.size ?: 0
                                "• $setCount sets"
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(text = createDurationString(exercise.startTime, exercise.endTime))
            }
            
            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (exercise.type == ExerciseType.DISTANCE) {
                        DistanceExerciseDetails(exercise)
                    } else {
                        val showWeight = exercise.details.sets?.any { it.weightKg != null } ?: false
                        SetTableHeader(showWeight)
                        exercise.details.sets?.forEachIndexed { index, set ->
                            SetRow(setNumber = index + 1, set = set, showWeight = showWeight)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DistanceExerciseDetails(exercise: ExerciseRecordResponse) {
    val distance = exercise.details.distance ?: 0.0
    val duration = Duration.between(exercise.startTime, exercise.endTime)
    val durationHours = duration.toMillis() / (1000.0 * 60 * 60)
    val timePerKm = duration.toMinutes() / distance // minutes per km

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Distance",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.33f)
            )
            
            Text(
                text = "Pace",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.33f)
            )
            
            Text(
                text = "Speed",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.33f)
            )
        }
        
        // Data Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format("%.1f km", distance),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.33f)
            )
            
            Text(
                text = String.format("%d:%02d min/km", 
                    timePerKm.toInt(),
                    ((timePerKm % 1) * 60).toInt()
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.33f)
            )
            
            Text(
                text = String.format("%.1f km/h", distance / durationHours),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.33f)
            )
        }
    }
}

@Composable
private fun SetRow(setNumber: Int, set: ExerciseSetResponse, showWeight: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number Column (12%)
        Text(
            text = "Set $setNumber",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.12f)
        )
        
        // Weight Column (20%, if shown)
        if (showWeight) {
            Text(
                text = set.weightKg?.let { "$it kg" } ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.20f)
            )
        }
        
        // Reps/Time Column (33% or 38% if no weight)
        Text(
            text = if (set.repetitions != null) 
                   "${set.repetitions} reps" 
                   else createDurationString(set.startTime, set.endTime),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(if (showWeight) 0.33f else 0.38f)
        )
        
        // Rest Column (20% or 30% if no weight)
        Text(
            text = if (setNumber > 1) createRestTimeString(set.startTime, set.endTime) else "-",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(if (showWeight) 0.20f else 0.30f)
        )
        
        // Failure Column (15% or 20% if no weight)
        Text(
            text = if (set.failure) "🔴" else "-",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(if (showWeight) 0.15f else 0.20f)
        )
    }
}

@Composable
private fun SetTableHeader(showWeight: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Set",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(0.12f)
        )
        
        if (showWeight) {
            Text(
                text = "Weight",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.20f)
            )
        }
        
        Text(
            text = "Reps/Time",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(if (showWeight) 0.33f else 0.38f)
        )
        
        Text(
            text = "Rest",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(if (showWeight) 0.20f else 0.30f)
        )
        
        Text(
            text = "Failure",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(if (showWeight) 0.15f else 0.20f)
        )
    }
}


