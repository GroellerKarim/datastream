package eu.groeller.datastreamui.data.model

import eu.groeller.datastreamui.data.serializer.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

/**
 * Request model for creating a new exercise record.
 */
@Serializable
data class CreateExerciseRequest(
    val exerciseDefinitionId: Long,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime,
    val details: CreateExerciseDetailsRequest,
    val orderIndex: Int
)

/**
 * Request model for the details of an exercise being created.
 */
@Serializable
data class CreateExerciseDetailsRequest(
    val distance: Double? = null,
    val distanceUnit: DistanceUnit? = null,
    val sets: List<CreateExerciseSetRequest>? = null,
    val weightKg: Double? = null
)

/**
 * Request model for a set within an exercise being created.
 */
@Serializable
data class CreateExerciseSetRequest(
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime,
    val failure: Boolean,
    val weightKg: Double? = null,
    val repetitions: Int? = null
)

/**
 * Request model for creating a new exercise definition.
 */
@Serializable
data class CreateExerciseDefinitionRequest(
    val name: String,
    val type: ExerciseType
) 