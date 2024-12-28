package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.groeller.datastreamui.data.model.ExerciseDefinition

@Composable
fun ExerciseSelectionDialog(
    onDismiss: () -> Unit,
    onExerciseSelected: (ExerciseDefinition) -> Unit,
    recentExercises: List<ExerciseDefinition>,
    allExercises: List<ExerciseDefinition>
) {
    var searchQuery by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Exercise") },
        text = {
            Column {
                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search exercises") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Exercise list
                LazyColumn {
                    // Recent exercises section
                    if (recentExercises.isNotEmpty() && searchQuery.isEmpty()) {
                        item { 
                            Text(
                                "Recently Used",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(recentExercises) { exercise ->
                            ExerciseItem(exercise, onExerciseSelected)
                        }
                        item { Divider(Modifier.padding(vertical = 8.dp)) }
                    }

                    // All exercises (filtered by search)
                    item { 
                        Text(
                            "All Exercises",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    val filteredExercises = allExercises.filter { 
                        it.name.contains(searchQuery, ignoreCase = true) 
                    }
                    items(filteredExercises) { exercise ->
                        ExerciseItem(exercise, onExerciseSelected)
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

@Composable
private fun ExerciseItem(
    exercise: ExerciseDefinition,
    onExerciseSelected: (ExerciseDefinition) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExerciseSelected(exercise) }
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = exercise.type.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 