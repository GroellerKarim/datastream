package eu.groeller.dsui.presentation.model

/**
 * UI state for workout-related screens.
 * Represents different states the UI can be in when dealing with workouts.
 */
sealed class WorkoutUIState {
    /**
     * Initial loading state when fetching workouts.
     */
    object Loading : WorkoutUIState()
    
    /**
     * Success state with loaded workout data.
     */
    data class Success(
        val workouts: List<WorkoutUI>,
        val workoutTypes: List<WorkoutTypeUI> = emptyList(),
        val selectedWorkout: WorkoutUI? = null
    ) : WorkoutUIState()
    
    /**
     * Error state when workout data cannot be loaded.
     */
    data class Error(val message: String) : WorkoutUIState()
    
    /**
     * Empty state when no workouts are available.
     */
    object Empty : WorkoutUIState()
} 