package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import eu.groeller.datastreamui.viewmodel.WorkoutTrackingViewModel

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
            Text(
                text = "Current Exercise: ${exercise.name}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // TODO: Add exercise tracking interface
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
            // TODO: Add completed exercises list
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