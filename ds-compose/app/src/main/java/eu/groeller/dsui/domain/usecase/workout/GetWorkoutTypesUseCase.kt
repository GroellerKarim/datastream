package eu.groeller.dsui.domain.usecase.workout

import eu.groeller.dsui.domain.model.WorkoutType
import eu.groeller.dsui.domain.repository.IWorkoutRepository

/**
 * Use case for retrieving all workout types.
 */
class GetWorkoutTypesUseCase(private val workoutRepository: IWorkoutRepository) {
    
    /**
     * Retrieves all workout types.
     * 
     * @return Result containing a list of workout types or an error.
     */
    suspend operator fun invoke(): Result<List<WorkoutType>> {
        return workoutRepository.getWorkoutTypes()
    }
} 