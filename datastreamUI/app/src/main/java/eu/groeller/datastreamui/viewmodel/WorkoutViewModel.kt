package eu.groeller.datastreamui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.datastreamui.data.workout.WorkoutRepository
import eu.groeller.datastreamui.data.workout.WorkoutState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WorkoutViewModel(private val workoutRepository: WorkoutRepository): ViewModel() {

    private val workoutState = workoutRepository.fetchWorkoutState

    val uiState: StateFlow<WorkoutState> = workoutState.stateIn(
        scope = viewModelScope,
        initialValue = WorkoutState.Loading,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000)
    )
}