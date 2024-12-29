package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.groeller.datastreamui.data.model.WorkoutType

@Composable
fun WorkoutTypeSelectionDialog(
    workoutTypes: List<WorkoutType>,
    onDismiss: () -> Unit,
    onWorkoutTypeSelected: (WorkoutType) -> Unit,
    isLoading: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Workout Type") },
        text = {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn {
                    items(workoutTypes) { workoutType ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onWorkoutTypeSelected(workoutType) }
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = workoutType.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        if (workoutType != workoutTypes.last()) {
                            Divider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 