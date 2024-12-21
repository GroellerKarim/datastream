package eu.groeller.datastreamui.screens.workout

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

@Composable
fun SingleExerciseCard(exercise: ExerciseRecordResponse) {
    var isExpanded by remember { mutableStateOf(false) }
    val isExpandable = exercise.type != ExerciseType.DISTANCE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .then(if (isExpandable) {
                Modifier.clickable { isExpanded = !isExpanded }
            } else {
                Modifier
            })
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
                Text(text = exercise.exerciseName)
                Text(text = createDurationString(exercise.startTime, exercise.endTime))
            }
            
            // Expandable content (only for non-DISTANCE exercises)
            if (isExpandable) {
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
private fun SetRow(setNumber: Int, set: ExerciseSetResponse, showWeight: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number Column (15%)
        Text(
            text = "Set $setNumber",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.15f)
        )
        
        // Weight Column (20%, if shown)
        if (showWeight) {
            Text(
                text = set.weightKg?.let { "$it kg" } ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(0.2f)
            )
        }
        
        // Reps/Time Column (30% or 35% if no weight)
        Text(
            text = if (set.repetitions != null) 
                   "${set.repetitions} reps" 
                   else createDurationString(set.startTime, set.endTime),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(if (showWeight) 0.3f else 0.35f)
        )
        
        // Rest Column (25% or 30% if no weight)
        Text(
            text = if (setNumber > 1) createDurationString(set.startTime, set.endTime) else "-",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(if (showWeight) 0.25f else 0.3f)
        )
        
        // Failure Column (10% or 20% if no weight)
        Text(
            text = if (set.failure) "🔴" else "-",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(if (showWeight) 0.1f else 0.2f)
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
            modifier = Modifier.weight(0.15f)
        )
        
        if (showWeight) {
            Text(
                text = "Weight",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(0.2f)
            )
        }
        
        Text(
            text = "Reps/Time",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(if (showWeight) 0.3f else 0.35f)
        )
        
        Text(
            text = "Rest",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(if (showWeight) 0.25f else 0.3f)
        )
        
        Text(
            text = "Failure",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(if (showWeight) 0.1f else 0.2f)
        )
    }
}


