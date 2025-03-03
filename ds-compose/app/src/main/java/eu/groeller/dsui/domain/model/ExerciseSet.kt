package eu.groeller.dsui.domain.model

import java.time.OffsetDateTime

/**
 * Domain model representing a single set of an exercise.
 * This corresponds to the backend's ExerciseSet entity.
 */
data class ExerciseSet(
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val failure: Boolean,
    val weightKg: Double?,
    val repetitions: Int?
) 