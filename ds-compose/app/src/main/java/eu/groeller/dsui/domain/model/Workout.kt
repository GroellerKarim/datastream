package eu.groeller.dsui.domain.model

import java.time.OffsetDateTime

/**
 * Domain model representing a complete workout session.
 * This corresponds to the backend's Workout entity.
 */
data class Workout(
    val id: Long,
    val durationMs: Long,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val exercises: Set<Exercise>,
    val workoutType: String
) 