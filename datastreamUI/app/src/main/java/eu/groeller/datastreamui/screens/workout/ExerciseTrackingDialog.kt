import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.screens.workout.SetData
import eu.groeller.datastreamui.screens.workout.createDurationString
import kotlinx.coroutines.delay
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseTrackingDialog(
    exercise: ExerciseDefinition,
    previousSetTime: OffsetDateTime?,
    onDismiss: () -> Unit,
    onExerciseCompleted: (List<SetData>) -> Unit
) {
    // Set Data
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var isFailure by remember { mutableStateOf(false) }
    var completedSets by remember { mutableStateOf<List<SetData>>(emptyList()) }
    var currentSetStartTime by remember { mutableStateOf<OffsetDateTime?>(null) }
    var currentEndTime by remember { mutableStateOf<OffsetDateTime?>(null) }

    var lastSetTime by remember { mutableStateOf<OffsetDateTime?>(previousSetTime) }
    var restTime by remember { mutableStateOf<Long?>(null) }    // For Timer
    var setDuration by remember { mutableStateOf<Long?>(null) } // For Timer

    var isSetInProgress by remember { mutableStateOf(false) }

    // Add partialReps state
    var partialReps by remember { mutableStateOf("") }

    // Effect to update rest time periodically
    LaunchedEffect(lastSetTime) {
        while (lastSetTime != null) {
            Log.d("ExerciseTrackingDialog", "Updating rest time")
            restTime = (System.currentTimeMillis() - lastSetTime!!.toInstant().toEpochMilli())
            delay(1000)
        }
    }

    // Effect to update set duration while set is in progress
    LaunchedEffect(currentSetStartTime) {
        while (currentSetStartTime != null) {
            setDuration =
                (System.currentTimeMillis() - currentSetStartTime!!.toInstant().toEpochMilli())
            delay(1000)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge
                )
                if (isSetInProgress) {
                    Text(
                        text = "Set duration: ${setDuration?.let { createDurationString(it) } ?: "00:00:00"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Rest time: ${restTime?.let { createDurationString(it) } ?: "00:00:00"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column {
                Column {
                    // Start/Stop Set Button
                    Button(
                        onClick = {
                            if (!isSetInProgress) {
                                // Start set
                                currentSetStartTime = OffsetDateTime.now()
                                isSetInProgress = true
                                // Clear any previous input
                                weight = ""
                                reps = ""
                                isFailure = false
                            } else {
                                // Stop set
                                val endTime = OffsetDateTime.now()  // Capture end time once
                                isSetInProgress = false
                                lastSetTime = endTime  // Use captured end time for rest timer
                                currentEndTime = endTime  // Store for SetData creation
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(if (isSetInProgress) "Stop Set" else "Start Set")
                    }

                    // Input Section - only visible after set is stopped
                    if (!isSetInProgress && currentSetStartTime != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = weight,
                                onValueChange = { weight = it },
                                label = { Text("Weight") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            
                            OutlinedTextField(
                                value = reps,
                                onValueChange = { reps = it },
                                label = { Text("Reps") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            OutlinedTextField(
                                value = partialReps,
                                onValueChange = { partialReps = it },
                                label = { Text("Partial") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                enabled = isFailure  // Only enable input when set is marked as failed
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Failed Set")
                                Switch(
                                    checked = isFailure,
                                    onCheckedChange = { isFailure = it }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    val newSet = SetData(
                                        weight = weight.toFloatOrNull() ?: 0f,
                                        reps = reps.toIntOrNull() ?: 0,
                                        isFailure = isFailure,
                                        partialReps = if (isFailure) partialReps.toIntOrNull() else null,
                                        startTime = currentSetStartTime!!,
                                        endTime = currentEndTime!!
                                    )
                                    completedSets = completedSets + newSet
                                    currentSetStartTime = null
                                    currentEndTime = null
                                    weight = ""
                                    reps = ""
                                    partialReps = ""
                                    isFailure = false
                                },
                                enabled = weight.isNotBlank() && reps.isNotBlank() && (!isFailure || partialReps.isNotBlank())
                            ) {
                                Text("Complete Set")
                            }
                        }
                    }
                }

                // Completed Sets Section
                if (completedSets.isNotEmpty()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = "Completed Sets",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f, false),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(completedSets) { set ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Set ${completedSets.indexOf(set) + 1}:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "${set.weight}kg × ${set.reps}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                if (set.isFailure) {
                                    Text(
                                        text = "Failed",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onExerciseCompleted(completedSets) },
                enabled = completedSets.isNotEmpty()
            ) {
                Text("Complete Exercise")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 