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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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

@Composable
fun ExerciseTrackingDialog(
    exercise: ExerciseDefinition,
    onDismiss: () -> Unit,
    onExerciseCompleted: (List<SetData>) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var isFailure by remember { mutableStateOf(false) }
    var completedSets by remember { mutableStateOf<List<SetData>>(emptyList()) }
    
    var lastSetTime by remember { mutableStateOf<Long?>(null) }
    var restTime by remember { mutableStateOf<Long?>(null) }
    
    // Effect to update rest time periodically
    LaunchedEffect(lastSetTime) {
        while(lastSetTime != null) {
            Log.d("ExerciseTrackingDialog", "Updating rest time")
            restTime = System.currentTimeMillis() - lastSetTime!!
            kotlinx.coroutines.delay(1000) // Update every second
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
                Text(
                    text = "Rest time: ${restTime?.let { createDurationString(it) } ?: "00:00"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column {
                // Input Section
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
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
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
                    
                    Button(
                        onClick = {
                            val newSet = SetData(
                                weight = weight.toFloatOrNull() ?: 0f,
                                reps = reps.toIntOrNull() ?: 0,
                                isFailure = isFailure,
                                timestamp = System.currentTimeMillis()
                            )
                            completedSets = completedSets + newSet
                            lastSetTime = System.currentTimeMillis()
                            weight = ""
                            reps = ""
                            isFailure = false
                        },
                        enabled = weight.isNotBlank() && reps.isNotBlank()
                    ) {
                        Text("Complete Set")
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