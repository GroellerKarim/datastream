package eu.groeller.dsui.data.mapper

import eu.groeller.datastreamui.data.model.CreateWorkoutRequest
import eu.groeller.datastreamui.data.model.WorkoutResponse
import eu.groeller.dsui.data.mapper.ExerciseMapper.toCreateRequestExercise
import eu.groeller.dsui.data.mapper.ExerciseMapper.toDomain
import eu.groeller.dsui.domain.model.Workout

/**
 * Mapper for converting between domain Workout models and data layer workout models.
 */
object WorkoutMapper {
    
    /**
     * Converts a data layer WorkoutResponse to a domain Workout model.
     */
    fun WorkoutResponse.toDomain(): Workout {
        return Workout(
            id = workoutId,
            durationMs = durationMs,
            exercises = exercises.map { it.toDomain() }.toSet(),
            workoutType = workoutType,
            startTime = startTime,
            endTime = endTime
        )
    }
    
    /**
     * Converts a domain Workout model to a data layer CreateWorkoutRequest.
     */
    fun Workout.toCreateRequest(): CreateWorkoutRequest {
        return CreateWorkoutRequest(
            exercises = exercises.map { it.toCreateRequestExercise() }.toList(),
            type = workoutType,
            startTime = startTime,
            endTime = endTime,
        )
    }
} 