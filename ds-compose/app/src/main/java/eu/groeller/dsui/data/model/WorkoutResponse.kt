package eu.groeller.datastreamui.data.model

import android.os.Parcelable
import eu.groeller.datastreamui.data.serializer.OffsetDateTimeParcelizer
import eu.groeller.datastreamui.data.serializer.OffsetDateTimeSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Parcelize
@Serializable
data class WorkoutResponse(
    val workoutId: Long,
    val durationMs: Long,
    @TypeParceler<OffsetDateTime, OffsetDateTimeParcelizer>()
    @Serializable(with = OffsetDateTimeSerializer::class)
    val date: OffsetDateTime,
    val exercises: Set<ExerciseRecordResponse>,
    val workoutType: String
) : Parcelable

@Parcelize
@Serializable
data class ExerciseRecordResponse(
    val exerciseRecordId: Long,
    val exerciseDefinitionId: Long,
    val exerciseName: String,
    val type: ExerciseType,
    @TypeParceler<OffsetDateTime, OffsetDateTimeParcelizer>()
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @TypeParceler<OffsetDateTime, OffsetDateTimeParcelizer>()
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime,
    val details: ExerciseRecordDetailsResponse,
    val orderIndex: Int
) : Parcelable

@Parcelize
@Serializable
data class ExerciseRecordDetailsResponse(
    val distance: Double? = null,
    val distanceUnit: DistanceUnit? = null,
    val sets: List<ExerciseSetResponse>? = null,
    val weightKg: Double? = null
) : Parcelable

@Parcelize
@Serializable
data class ExerciseSetResponse(
    @TypeParceler<OffsetDateTime, OffsetDateTimeParcelizer>()
    @Serializable(with = OffsetDateTimeSerializer::class)
    val startTime: OffsetDateTime,
    @TypeParceler<OffsetDateTime, OffsetDateTimeParcelizer>()
    @Serializable(with = OffsetDateTimeSerializer::class)
    val endTime: OffsetDateTime,
    val failure: Boolean,
    val weightKg: Double? = null,
    val repetitions: Int? = null
) : Parcelable

@Parcelize
@Serializable
enum class ExerciseType : Parcelable {
    SETS_REPS,
    DISTANCE,
    SETS_TIME
}

@Parcelize
@Serializable
enum class DistanceUnit : Parcelable {
    METERS,
    KILOMETERS,
    MILES
}