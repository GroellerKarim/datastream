package eu.groeller.datastreamui.data.model

import eu.groeller.datastreamui.data.serializer.OffsetDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class WorkoutResponse(
    val workoutId: Long,
    val durationMs: Long,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val date: OffsetDateTime,
    val exercises: Set<ExerciseRecordResponse>
)

@Serializable
data class ExerciseRecordResponse(
    val exerciseRecordId: Long,
    val exerciseDefinitionId: Long,
    val exerciseName: String,
    val type: ExerciseType,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime,
    val details: ExerciseRecordDetailsResponse,
    val orderIndex: Int
)

@Serializable
data class ExerciseRecordDetailsResponse(
    val distance: Double? = null,
    val distanceUnit: DistanceUnit? = null,
    val sets: List<ExerciseSetResponse>? = null,
    val weightKg: Double? = null
)

@Serializable
data class ExerciseSetResponse(
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime,
    val failure: Boolean,
    val repetitions: Int
)

@Serializable
enum class ExerciseType {
    SETS_REPS,
    DISTANCE,
    SETS_TIME
}

@Serializable
enum class DistanceUnit {
    METERS,
    KILOMETERS,
    MILES
}