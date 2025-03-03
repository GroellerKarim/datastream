package eu.groeller.dsui.domain.usecase.workout

import eu.groeller.dsui.domain.model.WorkoutType
import eu.groeller.dsui.domain.repository.IWorkoutRepository

/**
 * Use case for creating a new workout type.
 */
class CreateWorkoutTypeUseCase(private val workoutRepository: IWorkoutRepository) {
    
    /**
     * Execute the use case to create a new workout type.
     *
     * @param name The name of the workout type to create
     * @return Result containing the created workout type if successful, error otherwise
     */
    suspend operator fun invoke(name: String): Result<WorkoutType> {
        // Validation logic
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Workout type name cannot be blank"))
        }
        
        if (name.length < 3) {
            return Result.failure(IllegalArgumentException("Workout type name must be at least 3 characters"))
        }
        
        return workoutRepository.createWorkoutType(name)
    }
} 