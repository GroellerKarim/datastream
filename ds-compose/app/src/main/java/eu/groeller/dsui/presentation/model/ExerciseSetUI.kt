package eu.groeller.dsui.presentation.model

import java.time.OffsetDateTime

/**
 * UI model for representing an exercise set in the user interface.
 * Contains all data necessary for display and mapping to backend requests.
 */
data class ExerciseSetUI(
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val isFailure: Boolean = false,
    val repetitions: Int? = null,
    val partialRepetitions: Int? = null,
    val weightKg: Double? = null,
    val orderIndex: Int,
    
    // Additional UI-specific properties
    val isSelected: Boolean = false,
    val isInProgress: Boolean = false,
    val formattedWeight: String? = null,  // e.g., "75 kg"
    val formattedDuration: String? = null  // e.g., "1:30" for timed sets
) 