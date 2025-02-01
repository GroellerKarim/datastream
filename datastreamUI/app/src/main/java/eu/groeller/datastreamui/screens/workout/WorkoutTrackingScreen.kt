package eu.groeller.datastreamui.screens.workout

import ExerciseRecordRequest
import ExerciseTrackingDialog
import WorkoutTrackingViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

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
            onAddWorkoutType = { workoutTypeString ->
                viewModel.addWorkoutType(workoutTypeString)
            },
            isLoading = uiState.isLoading
        )
        return // Don't show the rest of the UI until workout type is selected
    }

    // Effect to update duration periodically
    var workoutDuration by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        while (true) {
            workoutDuration = viewModel.getWorkoutDuration()
            kotlinx.coroutines.delay(1000) // Update every second
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 8.dp)
    ) {
        // Header with duration and controls

        // List of completed exercises
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Text(
                text = "Completed Exercises",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LazyColumn {
                items(uiState.exercises) { exerciseRecord ->
                    CompletedExerciseItem(exerciseRecord)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
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

        // Add Exercise Button
        Button(
            onClick = { showExerciseSelection = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Add Exercise")
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
            onAddExercise = { name, type -> viewModel.addExerciseDefinition(name, type) },
            allExercises = uiState.allExercises
        )
    }

    // Cancel Confirmation Dialog
    if (showCancelConfirmation) {
        CancelWorkoutDialog(
            onDismiss = { showCancelConfirmation = false },
            onConfirm = {
                if (showCancelConfirmation) {
                    showCancelConfirmation = false
                    viewModel.resetWorkout()
                    onNavigateBack()
                }
            }
        )
    }

    // Show exercise tracking dialog when there's a current exercise
    uiState.currentExercise?.let { exercise ->
        ExerciseTrackingDialog(
            exercise = exercise,
            previousSetTime = uiState.exercises.lastOrNull()?.endTime?.toInstant()?.toEpochMilli(),
            onDismiss = { viewModel.selectExercise(null) },
            onExerciseCompleted = { sets ->
                viewModel.recordExercise(sets)
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
                    text = exercise.name,
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(
                            text = "Set ${index + 1}:",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${set.weight}kg × ${set.reps}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (set.isFailure) {
                            Text(
                                text = "Failed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(
                                text = "-",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
    }
}
