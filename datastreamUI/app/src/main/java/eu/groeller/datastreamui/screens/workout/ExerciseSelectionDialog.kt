package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.data.model.ExerciseType

@Composable
fun ExerciseSelectionDialog(
    onDismiss: () -> Unit,
    onExerciseSelected: (ExerciseDefinition) -> Unit,
    recentExercises: List<ExerciseDefinition>,
    allExercises: List<ExerciseDefinition>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<ExerciseType?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Exercise") },
        text = {
            Column {
                // Exercise Type Filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ExerciseType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = if (selectedType == type) null else type
                            },
                            label = { 
                                Text(
                                    when(type) {
                                        ExerciseType.SETS_REPS -> "Sets & Reps"
                                        ExerciseType.SETS_TIME -> "Sets & Time"
                                        ExerciseType.DISTANCE -> "Distance"
                                    }
                                )
                            }
                        )
                    }
                }

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
                        items(recentExercises.filter { exercise ->
                            selectedType?.let { type -> exercise.type == type } ?: true
                        }) { exercise ->
                            ExerciseItem(exercise, onExerciseSelected)
                        }
                        item { Divider(Modifier.padding(vertical = 8.dp)) }
                    }

                    // All exercises (filtered by search and type)
                    item { 
                        Text(
                            "All Exercises",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    val filteredExercises = allExercises.filter { exercise -> 
                        exercise.name.contains(searchQuery, ignoreCase = true) &&
                        (selectedType?.let { type -> exercise.type == type } ?: true)
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