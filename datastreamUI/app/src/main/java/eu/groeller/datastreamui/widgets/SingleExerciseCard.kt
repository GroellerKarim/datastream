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
                        exercise.details.sets?.forEachIndexed { index, set ->
                            SetRow(setNumber = index + 1, set = set)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetRow(setNumber: Int, set: ExerciseSetResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Set $setNumber:",
            style = MaterialTheme.typography.bodyMedium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${set.repetitions} reps",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = createDurationString(set.startTime, set.endTime),
                style = MaterialTheme.typography.bodyMedium
            )
            if (set.failure) {
                Text(
                    text = "🔴",  // Red circle emoji to indicate failure
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


