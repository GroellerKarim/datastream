package eu.groeller.dsui.domain.repository

import eu.groeller.dsui.domain.model.Workout
import eu.groeller.dsui.domain.model.WorkoutType
import eu.groeller.dsui.presentation.model.ExerciseDetailsUI
import java.time.OffsetDateTime

/**
 * Repository interface for workout-related operations.
 */
interface IWorkoutRepository {
    
    /**
     * Get workouts with pagination.
     * 
     * @param page The page number (0-based)
     * @param size The number of items per page
     * @return Result containing list of workouts if successful, error otherwise
     */
    suspend fun getWorkouts(page: Int, size: Int): Result<List<Workout>>
    
    /**
     * Get a specific workout by ID.
     * 
     * @param id The workout ID
     * @return Result containing the workout if found, error otherwise
     */
    suspend fun getWorkoutById(id: Long): Result<Workout>
    
    /**
     * Create a new workout.
     * 
     * @param workout The workout to create
     * @return Result containing the created workout with server-assigned ID if successful, error otherwise
     */
    suspend fun createWorkout(workout: Workout): Result<Workout>
    
    /**
     * Create a new workout with individual parameters.
     * 
     * @param workoutType The type/name of the workout.
     * @param startTime The start time of the workout.
     * @param endTime The end time of the workout.
     * @param exercises List of exercises in the workout, represented as pairs of exerciseDefinitionId and details.
     * @return Result containing the created workout with server-assigned ID if successful, error otherwise
     */
    suspend fun createWorkout(
        workoutType: String,
        startTime: OffsetDateTime,
        endTime: OffsetDateTime,
        exercises: List<Pair<Long, Pair<ExerciseDetailsUI, Pair<OffsetDateTime, OffsetDateTime>>>>
    ): Result<Workout>
    
    /**
     * Get all available workout types.
     * 
     * @return Result containing list of workout types if successful, error otherwise
     */
    suspend fun getWorkoutTypes(): Result<List<WorkoutType>>
    
    /**
     * Create a new workout type.
     * 
     * @param name The name of the workout type to create
     * @return Result containing the created workout type with server-assigned ID if successful, error otherwise
     */
    suspend fun createWorkoutType(name: String): Result<WorkoutType>
} 