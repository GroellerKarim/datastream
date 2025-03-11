package eu.groeller.dsui.domain.model

/**
 * Domain model representing the definition of an exercise.
 * This corresponds to the backend's ExerciseDefinition entity.
 */
data class ExerciseDefinition(
    val id: Long,
    val name: String,
    val type: ExerciseTypeEnum
) 