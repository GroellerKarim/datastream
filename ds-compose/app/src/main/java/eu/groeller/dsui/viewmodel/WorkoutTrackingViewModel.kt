package eu.groeller.dsui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.groeller.dsui.domain.model.ExerciseTypeEnum
import eu.groeller.dsui.domain.usecase.exercise.CreateExerciseDefinitionUseCase
import eu.groeller.dsui.domain.usecase.exercise.GetExerciseDefinitionsUseCase
import eu.groeller.dsui.domain.usecase.workout.CreateWorkoutUseCase
import eu.groeller.dsui.domain.usecase.workout.GetWorkoutTypesUseCase
import eu.groeller.dsui.domain.usecase.workout.CreateWorkoutTypeUseCase
import eu.groeller.dsui.presentation.mapper.ExerciseDefinitionUIMapper.toUI
import eu.groeller.dsui.presentation.mapper.WorkoutTypeUIMapper.toUI
import eu.groeller.dsui.presentation.mapper.WorkoutUIMapper
import eu.groeller.dsui.presentation.mapper.WorkoutUIMapper.toUI
import eu.groeller.dsui.presentation.mapper.WorkoutUIMapper.withEndTime
import eu.groeller.dsui.presentation.model.ExerciseDetailsUI
import eu.groeller.dsui.presentation.model.ExerciseSetUI
import eu.groeller.dsui.presentation.model.ExerciseUI
import eu.groeller.dsui.presentation.model.WorkoutTrackingUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetDateTime

/**
 * ViewModel for the workout tracking screen.
 * Handles creating and tracking active workouts.
 */
class WorkoutTrackingViewModel(
    private val getExerciseDefinitionsUseCase: GetExerciseDefinitionsUseCase,
    private val getWorkoutTypesUseCase: GetWorkoutTypesUseCase,
    private val createExerciseDefinitionUseCase: CreateExerciseDefinitionUseCase,
    private val createWorkoutTypeUseCase: CreateWorkoutTypeUseCase,
    private val createWorkoutUseCase: CreateWorkoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkoutTrackingUIState>(
        WorkoutTrackingUIState.Setup()
    )
    val uiState: StateFlow<WorkoutTrackingUIState> = _uiState.asStateFlow()

    // Timer values for the active workout
    private var workoutStartTime: OffsetDateTime = OffsetDateTime.now()
    private var elapsedTimeSeconds: Long = 0

    init {
        loadInitialData()
    }

    /**
     * Loads initial workout types and exercise definitions.
     */
    private fun loadInitialData() {
        viewModelScope.launch {
            // Load workout types
            getWorkoutTypesUseCase().fold(
                onSuccess = { workoutTypes ->
                    val workoutTypesUI = workoutTypes.toUI()
                    
                    // Load exercise definitions
                    getExerciseDefinitionsUseCase().fold(
                        onSuccess = { exerciseDefinitions ->
                            val currentState = _uiState.value
                            if (currentState is WorkoutTrackingUIState.Setup) {
                                _uiState.value = WorkoutTrackingUIState.Setup(
                                    availableWorkoutTypes = workoutTypesUI,
                                    availableExerciseDefinitions = exerciseDefinitions.toUI()
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.value = WorkoutTrackingUIState.Error(
                                "Failed to load exercise definitions: ${error.message}"
                            )
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.value = WorkoutTrackingUIState.Error(
                        "Failed to load workout types: ${error.message}"
                    )
                }
            )
        }
    }

    /**
     * Selects a workout type and starts a new workout.
     */
    fun selectWorkoutType(workoutTypeId: Long) {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Setup) {
            val selectedType = currentState.availableWorkoutTypes.find { it.id == workoutTypeId }
            if (selectedType != null) {
                _uiState.value = currentState.copy(
                    selectedWorkoutType = selectedType
                )
            }
        }
    }

    /**
     * Starts a new workout with the selected workout type.
     */
    fun startWorkout() {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Setup && currentState.selectedWorkoutType != null) {
            workoutStartTime = OffsetDateTime.now()
            
            val newWorkout = WorkoutUIMapper.createWorkoutUI(
                workoutType = currentState.selectedWorkoutType.name,
                startTime = workoutStartTime
            )
            
            _uiState.value = WorkoutTrackingUIState.Active(
                currentWorkout = newWorkout,
                elapsedTime = "00:00"
            )
        }
    }

    /**
     * Adds a new exercise to the current workout.
     */
    fun addExercise(exerciseDefinitionId: Long) {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Active) {
            val workout = currentState.currentWorkout
            val state = _uiState.value as WorkoutTrackingUIState.Setup
            
            val exerciseDefinition = state.availableExerciseDefinitions
                .find { it.id == exerciseDefinitionId }
            
            if (exerciseDefinition != null) {
                val now = OffsetDateTime.now()
                
                val newExercise = ExerciseUI(
                    exerciseDefinitionId = exerciseDefinition.id,
                    name = exerciseDefinition.name,
                    type = exerciseDefinition.type,
                    startTime = now,
                    endTime = now,  // Will be updated when exercise is completed
                    details = ExerciseDetailsUI(),
                    orderIndex = workout.exercises.size,
                    isInProgress = true
                )
                
                val updatedWorkout = workout.copy(
                    exercises = workout.exercises + newExercise
                )
                
                _uiState.value = currentState.copy(
                    currentWorkout = updatedWorkout,
                    activeExercise = newExercise
                )
            }
        }
    }

    /**
     * Adds a set to the current active exercise.
     */
    fun addSet(reps: Int, weight: Double, isFailure: Boolean) {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Active && currentState.activeExercise != null) {
            val workout = currentState.currentWorkout
            val activeExercise = currentState.activeExercise
            
            val now = OffsetDateTime.now()
            
            val newSet = ExerciseSetUI(
                startTime = now,
                endTime = now.plusSeconds(1),  // Default 1 second duration, will be updated
                repetitions = reps,
                weightKg = weight,
                isFailure = isFailure,
                orderIndex = (activeExercise.details.sets?.size ?: 0),
                isInProgress = true
            )
            
            // Update exercise with the new set
            val updatedExercise = activeExercise.copy(
                details = activeExercise.details.copy(
                    sets = (activeExercise.details.sets ?: emptyList()) + newSet
                )
            )
            
            // Update workout with the updated exercise
            val updatedExercises = workout.exercises.map { 
                if (it.orderIndex == activeExercise.orderIndex) updatedExercise else it 
            }
            
            val updatedWorkout = workout.copy(exercises = updatedExercises)
            
            _uiState.value = currentState.copy(
                currentWorkout = updatedWorkout,
                activeExercise = updatedExercise,
                activeSet = newSet
            )
        }
    }

    /**
     * Completes the current set by setting its end time.
     */
    fun completeSet() {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Active && 
            currentState.activeExercise != null && 
            currentState.activeSet != null) {
            
            val workout = currentState.currentWorkout
            val activeExercise = currentState.activeExercise
            val activeSet = currentState.activeSet
            
            val now = OffsetDateTime.now()
            
            // Update the set with the end time
            val completedSet = activeSet.copy(
                endTime = now,
                isInProgress = false
            )
            
            // Update the sets list
            val updatedSets = (activeExercise.details.sets ?: emptyList()).map { 
                if (it.orderIndex == activeSet.orderIndex) completedSet else it 
            }
            
            // Update the exercise
            val updatedExercise = activeExercise.copy(
                details = activeExercise.details.copy(sets = updatedSets)
            )
            
            // Update the workout
            val updatedExercises = workout.exercises.map { 
                if (it.orderIndex == activeExercise.orderIndex) updatedExercise else it 
            }
            
            val updatedWorkout = workout.copy(exercises = updatedExercises)
            
            _uiState.value = currentState.copy(
                currentWorkout = updatedWorkout,
                activeExercise = updatedExercise,
                activeSet = null  // Clear active set
            )
        }
    }

    /**
     * Completes the current exercise by setting its end time.
     */
    fun completeExercise() {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Active && currentState.activeExercise != null) {
            val workout = currentState.currentWorkout
            val activeExercise = currentState.activeExercise
            
            val now = OffsetDateTime.now()
            
            // Update the exercise with the end time
            val completedExercise = activeExercise.copy(
                endTime = now,
                isInProgress = false
            )
            
            // Update the workout
            val updatedExercises = workout.exercises.map { 
                if (it.orderIndex == activeExercise.orderIndex) completedExercise else it 
            }
            
            val updatedWorkout = workout.copy(exercises = updatedExercises)
            
            _uiState.value = currentState.copy(
                currentWorkout = updatedWorkout,
                activeExercise = null  // Clear active exercise
            )
        }
    }

    /**
     * Creates a new exercise definition.
     */
    fun createExerciseDefinition(name: String, type: ExerciseTypeEnum) {
        viewModelScope.launch {
            createExerciseDefinitionUseCase(name, type).fold(
                onSuccess = { exerciseDefinition ->
                    val currentState = _uiState.value
                    if (currentState is WorkoutTrackingUIState.Setup) {
                        _uiState.value = currentState.copy(
                            availableExerciseDefinitions = currentState.availableExerciseDefinitions + 
                                    exerciseDefinition.toUI()
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.value = WorkoutTrackingUIState.Error(
                        "Failed to create exercise definition: ${error.message}"
                    )
                }
            )
        }
    }

    /**
     * Creates a new workout type.
     */
    fun createWorkoutType(name: String) {
        viewModelScope.launch {
            createWorkoutTypeUseCase(name).fold(
                onSuccess = { workoutType ->
                    val currentState = _uiState.value
                    if (currentState is WorkoutTrackingUIState.Setup) {
                        _uiState.value = currentState.copy(
                            availableWorkoutTypes = currentState.availableWorkoutTypes + 
                                    workoutType.toUI()
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.value = WorkoutTrackingUIState.Error(
                        "Failed to create workout type: ${error.message}"
                    )
                }
            )
        }
    }

    /**
     * Completes and saves the entire workout.
     */
    fun completeWorkout() {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Active) {
            val now = OffsetDateTime.now()
            val workout = currentState.currentWorkout
            
            _uiState.value = WorkoutTrackingUIState.Saving
            
            // Update the workout with the end time
            val completedWorkout = workout.withEndTime(now)
            
            viewModelScope.launch {
                // Convert UI model to domain model and save
                createWorkoutUseCase(
                    workoutType = completedWorkout.workoutType,
                    startTime = completedWorkout.startTime,
                    endTime = completedWorkout.endTime,
                    exercises = completedWorkout.exercises.map { exercise ->
                        Pair(
                            exercise.exerciseDefinitionId,
                            Pair(
                                exercise.details,
                                Pair(exercise.startTime, exercise.endTime)
                            )
                        )
                    }
                ).fold(
                    onSuccess = { savedWorkout ->
                        _uiState.value = WorkoutTrackingUIState.Completed(
                            workout = savedWorkout.toUI()
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = WorkoutTrackingUIState.Error(
                            "Failed to save workout: ${error.message}"
                        )
                    }
                )
            }
        }
    }

    /**
     * Updates the elapsed time for the current workout.
     */
    fun updateElapsedTime() {
        val currentState = _uiState.value
        if (currentState is WorkoutTrackingUIState.Active) {
            val now = OffsetDateTime.now()
            val duration = Duration.between(workoutStartTime, now)
            elapsedTimeSeconds = duration.seconds
            
            val minutes = elapsedTimeSeconds / 60
            val seconds = elapsedTimeSeconds % 60
            val formattedTime = String.format("%02d:%02d", minutes, seconds)
            
            _uiState.value = currentState.copy(
                elapsedTime = formattedTime
            )
        }
    }

    /**
     * Resets to the setup state for starting a new workout.
     */
    fun resetWorkout() {
        workoutStartTime = OffsetDateTime.now()
        elapsedTimeSeconds = 0
        
        // Reload initial data
        loadInitialData()
    }
}