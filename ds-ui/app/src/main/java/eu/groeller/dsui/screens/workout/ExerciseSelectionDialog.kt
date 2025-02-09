package eu.groeller.datastreamui.screens.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.data.model.ExerciseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectionDialog(
    onDismiss: () -> Unit,
    onExerciseSelected: (ExerciseDefinition) -> Unit,
    recentExercises: List<ExerciseDefinition>,
    allExercises: List<ExerciseDefinition>,
    onAddExercise: (name: String, type: ExerciseType) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<ExerciseType?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Exercise") },
        text = {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .height(600.dp)
            ) {
                // Exercise Type Filter using Segmented Buttons
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp, end = 0.dp, bottom = 12.dp)
                ) {
                    ExerciseType.entries.forEachIndexed { index, type ->
                        SegmentedButton(
                            selected = selectedType == type,
                            onClick = { 
                                selectedType = if (selectedType == type) null else type 
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ExerciseType.entries.size
                            ),
                            icon = { },
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            )
                        ) {
                            Text(
                                when(type) {
                                    ExerciseType.SETS_REPS -> "Sets & Reps"
                                    ExerciseType.SETS_TIME -> "Sets & Time"
                                    ExerciseType.DISTANCE -> "Distance"
                                },
                                fontSize = 12.sp,
                                maxLines = 1,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Button(onClick = { showAddDialog = true }) {
                    Text("Add Exercise")
                }
            }
        }
    )

    if (showAddDialog) {
        CreateExerciseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, type -> 
                onAddExercise(name, type)
                showAddDialog = false
            }
        )
    }
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
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: ExerciseType) -> Unit
) {
    var exerciseName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ExerciseType.SETS_REPS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exercise") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text("Exercise Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ExerciseType.entries.forEachIndexed { index, type ->
                        SegmentedButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = ExerciseType.entries.size
                            ),
                            icon = { },
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            )
                        ) {
                            Text(
                                when(type) {
                                    ExerciseType.SETS_REPS -> "Sets & Reps"
                                    ExerciseType.SETS_TIME -> "Sets & Time"
                                    ExerciseType.DISTANCE -> "Distance"
                                },
                                fontSize = 12.sp,
                                maxLines = 1,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (exerciseName.isNotBlank()) {
                        onConfirm(exerciseName, selectedType)
                    }
                },
                enabled = exerciseName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} 