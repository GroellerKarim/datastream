package eu.groeller.datastreamui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.datastreamui.data.exercise.ExerciseRepository
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.data.model.ExerciseRecordResponse
import eu.groeller.datastreamui.data.model.WorkoutType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

data class WorkoutTrackingState(
    val startTime: OffsetDateTime = OffsetDateTime.now(),
    val exercises: List<ExerciseRecordResponse> = emptyList(),
    val recentExercises: List<ExerciseDefinition> = emptyList(),
    val allExercises: List<ExerciseDefinition> = emptyList(),
    val workoutTypes: List<WorkoutType> = emptyList(),
    val selectedWorkoutType: WorkoutType? = null,
    val currentExercise: ExerciseDefinition? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WorkoutTrackingViewModel(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutTrackingState())
    val uiState: StateFlow<WorkoutTrackingState> = _uiState.asStateFlow()

    init {
        loadWorkoutTypes()
    }

    private fun loadWorkoutTypes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val types = exerciseRepository.getWorkoutTypes()
                _uiState.update { currentState ->
                    currentState.copy(
                        workoutTypes = types,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load workout types"
                    )
                }
            }
        }
    }

    fun selectWorkoutType(workoutType: WorkoutType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val recentExercises = exerciseRepository.getRecentExercisesForType(workoutType)
                val allExercises = exerciseRepository.getAllExercises()
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedWorkoutType = workoutType,
                        recentExercises = recentExercises,
                        allExercises = allExercises,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load exercises"
                    )
                }
            }
        }
    }

    // Exercise Selection
    fun selectExercise(exercise: ExerciseDefinition) {
        _uiState.update { currentState ->
            currentState.copy(currentExercise = exercise)
        }
    }

    // Add completed exercise to the workout
    fun addExerciseRecord(exercise: ExerciseRecordResponse) {
        _uiState.update { currentState ->
            currentState.copy(
                exercises = currentState.exercises + exercise,
                currentExercise = null // Reset current exercise after completion
            )
        }
    }

    // Get workout duration in milliseconds
    fun getWorkoutDuration(): Long {
        return OffsetDateTime.now()
            .toInstant()
            .toEpochMilli() - _uiState.value.startTime
            .toInstant()
            .toEpochMilli()
    }

    // Reset workout state
    fun resetWorkout() {
        _uiState.value = WorkoutTrackingState()
    }
} 