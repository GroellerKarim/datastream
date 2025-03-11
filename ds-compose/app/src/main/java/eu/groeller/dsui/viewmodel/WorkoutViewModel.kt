package eu.groeller.dsui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.dsui.domain.usecase.workout.GetWorkoutsUseCase
import eu.groeller.dsui.domain.usecase.workout.GetWorkoutTypesUseCase
import eu.groeller.dsui.presentation.mapper.WorkoutUIMapper.toUI
import eu.groeller.dsui.presentation.mapper.WorkoutTypeUIMapper.toUI
import eu.groeller.dsui.presentation.model.WorkoutUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the workout list screen.
 * Handles loading workouts and workout types from the domain layer.
 */
class WorkoutViewModel(
    private val getWorkoutsUseCase: GetWorkoutsUseCase,
    private val getWorkoutTypesUseCase: GetWorkoutTypesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutUIState>(WorkoutUIState.Loading)
    val uiState: StateFlow<WorkoutUIState> = _uiState.asStateFlow()

    private var selectedWorkoutId: Long? = null

    init {
        loadWorkouts()
    }

    /**
     * Loads workouts and workout types from the domain layer.
     */
    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = WorkoutUIState.Loading
            
            // First, try to load workouts
            getWorkoutsUseCase(page = 0, size = 10).fold(
                onSuccess = { workouts ->
                    if (workouts.isEmpty()) {
                        _uiState.value = WorkoutUIState.Empty
                        return@fold
                    }
                    
                    // If workouts loaded successfully, also load workout types
                    getWorkoutTypesUseCase().fold(
                        onSuccess = { workoutTypes ->
                            _uiState.value = WorkoutUIState.Success(
                                workouts = workouts.toUI(selectedWorkoutId),
                                workoutTypes = workoutTypes.toUI(),
                                selectedWorkout = selectedWorkoutId?.let { id ->
                                    workouts.find { it.id == id }?.toUI(isSelected = true)
                                }
                            )
                        },
                        onFailure = { error ->
                            // Even if workout types fail to load, we can still show workouts
                            _uiState.value = WorkoutUIState.Success(
                                workouts = workouts.toUI(selectedWorkoutId)
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = WorkoutUIState.Error(
                        error.message ?: "Failed to load workouts"
                    )
                }
            )
        }
    }

    /**
     * Selects a workout by ID.
     */
    fun selectWorkout(workoutId: Long) {
        val currentState = _uiState.value
        if (currentState is WorkoutUIState.Success) {
            selectedWorkoutId = workoutId
            
            val selectedWorkout = currentState.workouts.find { it.id == workoutId }
            
            _uiState.value = WorkoutUIState.Success(
                workouts = currentState.workouts.map { 
                    it.copy(isSelected = it.id == workoutId) 
                },
                workoutTypes = currentState.workoutTypes,
                selectedWorkout = selectedWorkout?.copy(isSelected = true)
            )
        }
    }

    /**
     * Clears the selected workout.
     */
    fun clearSelectedWorkout() {
        val currentState = _uiState.value
        if (currentState is WorkoutUIState.Success) {
            selectedWorkoutId = null
            
            _uiState.value = WorkoutUIState.Success(
                workouts = currentState.workouts.map { it.copy(isSelected = false) },
                workoutTypes = currentState.workoutTypes,
                selectedWorkout = null
            )
        }
    }

    /**
     * Refreshes the workout list.
     */
    fun refreshWorkouts() {
        loadWorkouts()
    }
}