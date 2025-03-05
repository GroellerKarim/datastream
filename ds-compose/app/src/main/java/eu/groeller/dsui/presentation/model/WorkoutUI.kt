package eu.groeller.dsui.presentation.model

import java.time.OffsetDateTime

/**
 * UI model for representing a workout in the user interface.
 * Includes all data necessary for display and creating workout requests to the backend.
 */
data class WorkoutUI(
    val id: Long? = null,
    val workoutType: String,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val durationDisplay: String,  // Formatted string for UI display (e.g., "45 min")
    val exercises: List<ExerciseUI> = emptyList(),
    
    // Additional UI-specific properties
    val isExpanded: Boolean = false,
    val isSelected: Boolean = false
) 