package eu.groeller.dsui.presentation.model

/**
 * UI model for representing a workout type in the user interface.
 */
data class WorkoutTypeUI(
    val id: Long,
    val name: String,
    
    // Additional UI-specific properties
    val isSelected: Boolean = false,
    val count: Int? = null  // Optional count of workouts of this type
) 