package eu.groeller.dsui.presentation.model

/**
 * UI state for workout tracking screen.
 * Represents different states during an active workout session.
 */
sealed class WorkoutTrackingUIState {
    /**
     * Initial setup state before starting a workout.
     */
    data class Setup(
        val availableWorkoutTypes: List<WorkoutTypeUI> = emptyList(),
        val availableExerciseDefinitions: List<ExerciseDefinitionUI> = emptyList(),
        val selectedWorkoutType: WorkoutTypeUI? = null
    ) : WorkoutTrackingUIState()
    
    /**
     * Active workout state with current workout and exercises.
     */
    data class Active(
        val currentWorkout: WorkoutUI,
        val activeExercise: ExerciseUI? = null,
        val activeSet: ExerciseSetUI? = null,
        val elapsedTime: String,
        val isWorkoutPaused: Boolean = false
    ) : WorkoutTrackingUIState()
    
    /**
     * State during saving of workout data.
     */
    object Saving : WorkoutTrackingUIState()
    
    /**
     * State after successful save of workout.
     */
    data class Completed(val workout: WorkoutUI) : WorkoutTrackingUIState()
    
    /**
     * Error state during workout tracking or saving.
     */
    data class Error(val message: String) : WorkoutTrackingUIState()
} 