package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WorkoutTrackingScreen(
    onNavigateBack: () -> Unit,
    onWorkoutComplete: () -> Unit
) {
    var showExerciseSelection by remember { mutableStateOf(false) }
    
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
            // Back/Cancel button
            Button(
                onClick = { /* Show confirmation dialog */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel")
            }
            
            // Workout duration
            Text(
                text = "00:00:00",
                fontSize = 24.sp
            )
            
            // Complete workout button
            Button(onClick = onWorkoutComplete) {
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

        // Exercise tracking content will go here
        Text("Exercise tracking content will go here", modifier = Modifier.padding(vertical = 16.dp))
    }

    // Exercise Selection Dialog
    if (showExerciseSelection) {
        ExerciseSelectionDialog(
            onDismiss = { showExerciseSelection = false },
            onExerciseSelected = { exercise ->
                // TODO: Handle exercise selection
                showExerciseSelection = false
            },
            recentExercises = emptyList(), // TODO: Get from ViewModel
            allExercises = emptyList()     // TODO: Get from ViewModel
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