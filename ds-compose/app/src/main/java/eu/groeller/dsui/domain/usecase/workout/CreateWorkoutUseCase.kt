package eu.groeller.dsui.domain.usecase.workout

import eu.groeller.dsui.domain.model.Workout
import eu.groeller.dsui.domain.repository.IWorkoutRepository
import eu.groeller.dsui.presentation.model.ExerciseDetailsUI
import java.time.OffsetDateTime

/**
 * Use case for creating a new workout with exercises.
 */
class CreateWorkoutUseCase(private val workoutRepository: IWorkoutRepository) {
    
    /**
     * Creates a new workout with the given details and exercises.
     * 
     * @param workoutType The type/name of the workout.
     * @param startTime The start time of the workout.
     * @param endTime The end time of the workout.
     * @param exercises List of exercises in the workout, represented as pairs of exerciseDefinitionId and details.
     * @return Result containing the created workout or an error.
     */
    suspend operator fun invoke(
        workoutType: String,
        startTime: OffsetDateTime,
        endTime: OffsetDateTime,
        exercises: List<Pair<Long, Pair<ExerciseDetailsUI, Pair<OffsetDateTime, OffsetDateTime>>>>
    ): Result<Workout> {
        return workoutRepository.createWorkout(
            workoutType = workoutType,
            startTime = startTime,
            endTime = endTime,
            exercises = exercises
        )
    }
} 