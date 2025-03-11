package eu.groeller.dsui.data.repository

import eu.groeller.dsui.data.mapper.WorkoutMapper.toDomain
import eu.groeller.dsui.data.mapper.WorkoutMapper.toCreateRequest
import eu.groeller.dsui.data.mapper.WorkoutTypeMapper.toDomain
import eu.groeller.dsui.data.source.local.UserLocalDataSource
import eu.groeller.dsui.data.source.local.LocalUserState
import eu.groeller.dsui.data.source.remote.WorkoutRemoteDataSource
import eu.groeller.dsui.domain.model.Workout
import eu.groeller.dsui.domain.model.WorkoutType
import eu.groeller.dsui.domain.repository.IWorkoutRepository
import kotlinx.coroutines.flow.first

/**
 * Implementation of the IWorkoutRepository interface.
 * This acts as a mediator between the domain layer and data sources.
 */
class WorkoutRepositoryImpl(
    private val workoutRemoteDataSource: WorkoutRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : IWorkoutRepository {
    
    /**
     * Get workouts with pagination.
     */
    override suspend fun getWorkouts(page: Int, size: Int): Result<List<Workout>> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val workouts = workoutRemoteDataSource.getWorkouts(userState.user.token, page, size)
                Result.success(workouts.content.map { it.toDomain() })
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get a specific workout by ID.
     */
    override suspend fun getWorkoutById(id: Long): Result<Workout> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val workout = workoutRemoteDataSource.getWorkoutById(userState.user.token, id)
                Result.success(workout.toDomain())
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create a new workout.
     */
    override suspend fun createWorkout(workout: Workout): Result<Workout> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val createWorkoutRequest = workout.toCreateRequest()
                val createdWorkout = workoutRemoteDataSource.createWorkout(userState.user.token, createWorkoutRequest)
                Result.success(createdWorkout.toDomain())
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all available workout types.
     */
    override suspend fun getWorkoutTypes(): Result<List<WorkoutType>> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val workoutTypes = workoutRemoteDataSource.getWorkoutTypes(userState.user.token)
                Result.success(workoutTypes.map { it.toDomain() })
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create a new workout type.
     */
    override suspend fun createWorkoutType(name: String): Result<WorkoutType> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val workoutType = workoutRemoteDataSource.createWorkoutType(userState.user.token, name)
                Result.success(workoutType.toDomain())
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 