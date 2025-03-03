package eu.groeller.dsui.domain.usecase.workout

import eu.groeller.dsui.domain.model.Workout
import eu.groeller.dsui.domain.repository.IWorkoutRepository

/**
 * Use case for retrieving workouts with pagination.
 */
class GetWorkoutsUseCase(private val workoutRepository: IWorkoutRepository) {
    
    /**
     * Execute the use case to retrieve workouts.
     *
     * @param page The page number (0-based)
     * @param size The number of items per page
     * @return Result containing list of workouts if successful, error otherwise
     */
    suspend operator fun invoke(page: Int = 0, size: Int = 10): Result<List<Workout>> {
        // Basic validation
        if (page < 0) {
            return Result.failure(IllegalArgumentException("Page cannot be negative"))
        }
        
        if (size <= 0) {
            return Result.failure(IllegalArgumentException("Size must be positive"))
        }
        
        return workoutRepository.getWorkouts(page, size)
    }
} 