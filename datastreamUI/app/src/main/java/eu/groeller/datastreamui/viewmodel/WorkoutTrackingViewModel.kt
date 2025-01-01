import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.datastreamui.data.exercise.ExerciseRepository
import eu.groeller.datastreamui.data.model.ExerciseDefinition
import eu.groeller.datastreamui.data.model.WorkoutType
import eu.groeller.datastreamui.screens.workout.SetData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

data class WorkoutTrackingState(
    val startTime: OffsetDateTime = OffsetDateTime.now(),
    val exercises: List<ExerciseRecordRequest> = emptyList(),
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
    fun selectExercise(exercise: ExerciseDefinition?) {
        _uiState.update { currentState ->
            currentState.copy(currentExercise = exercise)
        }
    }

    fun deselectExcercise() {
        _uiState.update { currentState ->
            currentState.copy(currentExercise = null)
        }
    }

    // Add completed exercise to the workout
    fun addExerciseRecord(exercise: ExerciseRecordRequest) {
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

    fun recordExercise(sets: List<SetData>) {
        val currentExercise = _uiState.value.currentExercise

        if (currentExercise != null && sets.isNotEmpty()) {
            val exerciseRecord = ExerciseRecordRequest(
                name = currentExercise.name,
                exerciseDefinitionId = currentExercise.id,
                startTime = _uiState.value.startTime,
                endTime = OffsetDateTime.now(),
                details = ExerciseRecordDetailsRequest(sets),
                order = _uiState.value.exercises.size + 1
            )

            _uiState.update { currentState ->
                currentState.copy(
                    exercises = currentState.exercises.plus(exerciseRecord),
                    currentExercise = null,
                )
            }
        }
    }
}

data class ExerciseRecordRequest(
    val name: String,
    val exerciseDefinitionId: Long,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val details: ExerciseRecordDetailsRequest,
    val order: Int
)

data class ExerciseRecordDetailsRequest(
    val sets: List<SetData>
)