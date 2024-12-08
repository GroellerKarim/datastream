package eu.groeller.datastreamui.data.workout

import eu.groeller.datastreamui.data.ErrorResponse
import eu.groeller.datastreamui.data.model.WorkoutResponse

sealed interface WorkoutState {
    data object Loading: WorkoutState
    data class Success(val workouts: List<WorkoutResponse>) : WorkoutState
    data class Error(val error: ErrorResponse): WorkoutState
}