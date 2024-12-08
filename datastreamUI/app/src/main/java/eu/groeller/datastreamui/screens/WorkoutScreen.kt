package eu.groeller.datastreamui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.datastreamui.data.workout.WorkoutState
import eu.groeller.datastreamui.viewmodel.WorkoutViewModel

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val currentState = uiState) {
        is WorkoutState.Success -> WorkoutScreen(currentState.workouts)
        is WorkoutState.Error -> Text(currentState.error.message)
        is WorkoutState.Loading -> Text("Loading...")
    }
}

@Composable
fun WorkoutScreen(
    workouts: List<WorkoutResponse>
) {
    Column {
        workouts.forEach { workout ->
            Text(workout.workoutId.toString())
        }
    }
}