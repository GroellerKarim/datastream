package eu.groeller.dsui.domain.model

/**
 * Domain model representing detailed information about an exercise record.
 * This corresponds to the backend's ExerciseRecordDetails entity.
 */
data class ExerciseDetails(
    val distance: Double? = null,
    val distanceUnit: DistanceUnitEnum? = null,
    val sets: List<ExerciseSet>? = null,
    val weightKg: Double? = null
) 