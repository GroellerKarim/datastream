package eu.groeller.dsui.domain.model

import java.time.OffsetDateTime

/**
 * Domain model representing a single exercise performed during a workout.
 * This corresponds to the backend's ExerciseRecord entity.
 */
data class Exercise(
    val id: Long,
    val exerciseDefinitionId: Long,
    val exerciseName: String,
    val type: ExerciseTypeEnum,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val details: ExerciseDetails,
    val orderIndex: Int
) 