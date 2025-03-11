package eu.groeller.dsui.domain.model

/**
 * Domain model representing a type of workout (e.g., "Push Day", "Cardio").
 * This corresponds to the backend's WorkoutType entity.
 */
data class WorkoutType(
    val id: Long,
    val name: String
) 