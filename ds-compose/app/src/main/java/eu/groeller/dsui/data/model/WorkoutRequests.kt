package eu.groeller.datastreamui.data.model

import eu.groeller.datastreamui.data.serializer.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

/**
 * Request model for creating a new workout.
 */
@Serializable
data class CreateWorkoutRequest(
    val exercises: List<CreateExerciseRequest>,
    val type: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime
) 