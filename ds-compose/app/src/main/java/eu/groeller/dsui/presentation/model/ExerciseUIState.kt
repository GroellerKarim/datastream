package eu.groeller.dsui.presentation.model

/**
 * UI state for exercise-related screens.
 * Represents different states the UI can be in when dealing with exercises.
 */
sealed class ExerciseUIState {
    /**
     * Initial loading state when fetching exercises or definitions.
     */
    object Loading : ExerciseUIState()
    
    /**
     * Success state with loaded exercise data.
     */
    data class Success(
        val exercises: List<ExerciseUI> = emptyList(),
        val exerciseDefinitions: List<ExerciseDefinitionUI> = emptyList(),
        val selectedExercise: ExerciseUI? = null,
        val selectedDefinition: ExerciseDefinitionUI? = null
    ) : ExerciseUIState()
    
    /**
     * Error state when exercise data cannot be loaded.
     */
    data class Error(val message: String) : ExerciseUIState()
    
    /**
     * Empty state when no exercises are available.
     */
    object Empty : ExerciseUIState()
} 