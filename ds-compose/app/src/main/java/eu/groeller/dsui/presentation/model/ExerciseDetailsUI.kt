package eu.groeller.dsui.presentation.model

import eu.groeller.dsui.domain.model.DistanceUnitEnum

/**
 * UI model for representing exercise details in the user interface.
 * Contains details specific to different exercise types (distance-based or set-based).
 */
data class ExerciseDetailsUI(
    // For distance-based exercises
    val distance: Double? = null,
    val distanceUnit: DistanceUnitEnum? = null,
    val distancePerUnit: Double? = null,  // e.g., pace like min/km
    
    // For set-based exercises
    val sets: List<ExerciseSetUI>? = null,
    
    // Common for both types
    val weightKg: Double? = null,
    
    // UI-specific display properties
    val formattedDistance: String? = null,  // e.g., "5.2 km"
    val formattedPace: String? = null      // e.g., "4:30 min/km"
) 