package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.viewmodel.WorkoutTrackingViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import eu.groeller.datastreamui.viewmodel.ExerciseRecordRequest

@Composable
fun WorkoutTrackingScreen(
    viewModel: WorkoutTrackingViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onWorkoutComplete: () -> Unit
) {
    var showExerciseSelection by remember { mutableStateOf(false) }
    var showCancelConfirmation by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Show workout type selection immediately if no type is selected
    if (uiState.selectedWorkoutType == null) {
        WorkoutTypeSelectionDialog(
            workoutTypes = uiState.workoutTypes,
            onDismiss = onNavigateBack, // Go back if user cancels type selection
            onWorkoutTypeSelected = { workoutType ->
                viewModel.selectWorkoutType(workoutType)
            },
            isLoading = uiState.isLoading
        )
        return // Don't show the rest of the UI until workout type is selected
    }

    // Effect to update duration periodically
    var workoutDuration by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while(true) {
            workoutDuration = viewModel.getWorkoutDuration()
            kotlinx.coroutines.delay(1000) // Update every second
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with duration and controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel button
            Button(
                onClick = { showCancelConfirmation = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel")
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Workout Type
                Text(
                    text = uiState.selectedWorkoutType?.name ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                // Workout duration
                Text(
                    text = createDurationString(workoutDuration),
                    fontSize = 24.sp
                )
            }
            
            // Complete workout button
            Button(
                onClick = onWorkoutComplete,
                enabled = uiState.exercises.isNotEmpty()
            ) {
                Text("Complete")
            }
        }

        // Current exercise display
        uiState.currentExercise?.let { exercise ->
            CurrentExerciseSection(
                exercise = exercise,
                onSetCompleted = { setData ->
                    viewModel.recordSet(setData)
                },
                onExerciseCompleted = {
                    viewModel.completeCurrentExercise()
                }
            )
        }

        // Add Exercise Button
        Button(
            onClick = { showExerciseSelection = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add Exercise")
        }

        // List of completed exercises
        if (uiState.exercises.isNotEmpty()) {
            Text(
                text = "Completed Exercises",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn {
                items(uiState.exercises) { exerciseRecord ->
                    CompletedExerciseItem(exerciseRecord)
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }

    // Exercise Selection Dialog
    if (showExerciseSelection) {
        ExerciseSelectionDialog(
            onDismiss = { showExerciseSelection = false },
            onExerciseSelected = { exercise ->
                viewModel.selectExercise(exercise)
                showExerciseSelection = false
            },
            recentExercises = uiState.recentExercises,
            allExercises = uiState.allExercises
        )
    }

    // Cancel Confirmation Dialog
    if (showCancelConfirmation) {
        CancelWorkoutDialog(
            onDismiss = { showCancelConfirmation = false },
            onConfirm = {
                if(showCancelConfirmation) {
                    showCancelConfirmation = false
                    viewModel.resetWorkout()
                    onNavigateBack()
                }
            }
        )
    }
}

@Composable
private fun CancelWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Workout?") },
        text = { Text("Are you sure you want to cancel this workout? All progress will be lost.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Workout")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Continue Workout")
            }
        }
    )
}

@Composable
fun CurrentExerciseSection(
    exercise: ExerciseDefinition,
    onSetCompleted: (SetData) -> Unit,
    onExerciseCompleted: () -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var isFailure by remember { mutableStateOf(false) }
    var completedSets by remember { mutableStateOf<List<SetData>>(emptyList()) }
    
    // Rest timer state
    val lastSetTime = remember { mutableStateOf<Long?>(null) }
    val restTime by derivedStateOf {
        lastSetTime.value?.let { lastTime ->
            (System.currentTimeMillis() - lastTime) / 1000
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Add Complete Exercise button at the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onExerciseCompleted,
                    enabled = completedSets.isNotEmpty()  // Use local state instead
                ) {
                    Text("Complete Exercise")
                }
            }

            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rest timer display
            restTime?.let { time ->
                Text(
                    text = "Rest time: ${createDurationString(time)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Set input section
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Failed Set")
                    Switch(
                        checked = isFailure,
                        onCheckedChange = { isFailure = it }
                    )
                }
                
                Button(
                    onClick = {
                        weight.toFloatOrNull()?.let { w ->
                            reps.toIntOrNull()?.let { r ->
                                val setData = SetData(w, r, isFailure)
                                completedSets = completedSets + setData  // Update local state
                                onSetCompleted(setData)  // Notify parent
                                lastSetTime.value = System.currentTimeMillis()
                                weight = ""
                                reps = ""
                                isFailure = false
                            }
                        }
                    },
                    enabled = weight.isNotBlank() && reps.isNotBlank()
                ) {
                    Text("Complete Set")
                }
            }

            // Add Complete Exercise button at the top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onExerciseCompleted,
                    enabled = completedSets.isNotEmpty()  // Only enable if there are sets
                ) {
                    Text("Complete Exercise")
                }
            }
        }
    }
}

// Data class for set information
data class SetData(
    val weight: Float,
    val reps: Int,
    val isFailure: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) 

@Composable
private fun CompletedExerciseItem(exercise: ExerciseRecordRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Exercise name and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    // We'll need to get the exercise name from somewhere
                    text = "Exercise ${exercise.order}",  
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = createDurationString(
                        (exercise.endTime.toInstant().toEpochMilli() - 
                         exercise.startTime.toInstant().toEpochMilli()) / 1000
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sets summary
            Text(
                text = "${exercise.details.sets.size} sets completed",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Set details
            exercise.details.sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Set ${index + 1}:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${set.weight}kg × ${set.reps}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    if (set.isFailure) {
                        Text(
                            text = "Failed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
