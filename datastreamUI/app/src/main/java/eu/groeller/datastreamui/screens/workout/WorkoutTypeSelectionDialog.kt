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
    onAddWorkoutType: (String) -> Unit,
    isLoading: Boolean = false
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var hasDismissed by remember { mutableStateOf(false) }

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
                Column {
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
                    
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Add New Type")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (!hasDismissed) {
                    hasDismissed = true
                    onDismiss.invoke()
                }
            }) {
                Text("Cancel")
            }
        }
    )

    if (showAddDialog) {
        AddWorkoutTypeDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newType -> 
                onAddWorkoutType(newType)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddWorkoutTypeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var typeName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Workout Type") },
        text = {
            OutlinedTextField(
                value = typeName,
                onValueChange = { typeName = it },
                label = { Text("Type Name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (typeName.isNotBlank()) {
                        onConfirm(typeName)
                        onDismiss()
                    }
                },
                enabled = typeName.isNotBlank()
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