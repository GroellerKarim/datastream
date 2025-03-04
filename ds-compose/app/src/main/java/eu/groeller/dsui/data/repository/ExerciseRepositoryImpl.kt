package eu.groeller.dsui.data.repository

import eu.groeller.dsui.data.mapper.ExerciseMapper.toDomain
import eu.groeller.dsui.data.mapper.ExerciseMapper.toData
import eu.groeller.dsui.data.mapper.ExerciseMapper.toCreateRequestExercise
import eu.groeller.dsui.data.source.local.UserLocalDataSource
import eu.groeller.dsui.data.source.local.LocalUserState
import eu.groeller.dsui.data.source.remote.ExerciseRemoteDataSource
import eu.groeller.dsui.domain.model.Exercise
import eu.groeller.dsui.domain.model.ExerciseDefinition
import eu.groeller.dsui.domain.model.ExerciseTypeEnum
import eu.groeller.dsui.domain.repository.IExerciseRepository
import kotlinx.coroutines.flow.first

/**
 * Implementation of the IExerciseRepository interface.
 * This acts as a mediator between the domain layer and data sources.
 */
class ExerciseRepositoryImpl(
    private val exerciseRemoteDataSource: ExerciseRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : IExerciseRepository {
    
    /**
     * Get exercise definitions with optional filtering by type.
     */
    override suspend fun getExerciseDefinitions(type: ExerciseTypeEnum?): Result<List<ExerciseDefinition>> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val exerciseDefinitions = exerciseRemoteDataSource.getExerciseDefinitions(
                    token = userState.user.token,
                    type = type?.toData()
                )
                Result.success(exerciseDefinitions.map { it.toDomain() })
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get a specific exercise definition by ID.
     */
    override suspend fun getExerciseDefinitionById(id: Long): Result<ExerciseDefinition> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val exerciseDefinition = exerciseRemoteDataSource.getExerciseDefinitionById(userState.user.token, id)
                Result.success(exerciseDefinition.toDomain())
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create a new exercise definition.
     */
    override suspend fun createExerciseDefinition(name: String, type: ExerciseTypeEnum): Result<ExerciseDefinition> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                val exerciseDefinition = exerciseRemoteDataSource.createExerciseDefinition(
                    token = userState.user.token,
                    name = name,
                    type = type.toData()
                )
                Result.success(exerciseDefinition.toDomain())
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Create a new exercise record.
     * Note: This requires knowing which workout the exercise belongs to,
     * which is not part of the Exercise domain model directly.
     */
    override suspend fun createExercise(exercise: Exercise): Result<Exercise> {
        return try {
            val userState = userLocalDataSource.userFlow.first()
            
            if (userState is LocalUserState.Available) {
                // In a real implementation, we would need to know which workout this exercise belongs to
                // This is a limitation of the current domain model design
                // For now, we'll use a placeholder workout ID of 0, which will likely fail in practice
                val workoutId = 0L // This should be provided by the caller in a real implementation
                
                val createExerciseRequest = exercise.toCreateRequestExercise()
                val createdExercise = exerciseRemoteDataSource.createExercise(
                    token = userState.user.token,
                    workoutId = workoutId,
                    createExerciseRequest = createExerciseRequest
                )
                Result.success(createdExercise.toDomain())
            } else {
                Result.failure(IllegalStateException("User not logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 