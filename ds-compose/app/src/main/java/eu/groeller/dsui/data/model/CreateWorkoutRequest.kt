package eu.groeller.datastreamui.data.model

import ExerciseRecordRequest
import eu.groeller.datastreamui.data.serializer.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class CreateWorkoutRequest(
    val type: String,
    val exercises: List<CreateExerciseRequest>,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime
)
