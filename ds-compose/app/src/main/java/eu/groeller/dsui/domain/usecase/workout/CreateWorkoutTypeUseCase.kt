package eu.groeller.dsui.domain.usecase.workout

import eu.groeller.dsui.domain.model.WorkoutType
import eu.groeller.dsui.domain.repository.IWorkoutRepository

/**
 * Use case for creating a new workout type.
 */
class CreateWorkoutTypeUseCase(private val workoutRepository: IWorkoutRepository) {
    
    /**
     * Creates a new workout type with the given name.
     * 
     * @param name The name of the workout type.
     * @return Result containing the created workout type or an error.
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