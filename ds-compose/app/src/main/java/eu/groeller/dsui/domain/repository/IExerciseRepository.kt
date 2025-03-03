package eu.groeller.dsui.domain.repository

import eu.groeller.dsui.domain.model.Exercise
import eu.groeller.dsui.domain.model.ExerciseDefinition
import eu.groeller.dsui.domain.model.ExerciseTypeEnum

/**
 * Repository interface for exercise-related operations.
 */
interface IExerciseRepository {
    
    /**
     * Get exercise definitions with optional filtering by type.
     * 
     * @param type Optional exercise type to filter by
     * @return Result containing list of exercise definitions if successful, error otherwise
     */
    suspend fun getExerciseDefinitions(type: ExerciseTypeEnum? = null): Result<List<ExerciseDefinition>>
    
    /**
     * Get a specific exercise definition by ID.
     * 
     * @param id The exercise definition ID
     * @return Result containing the exercise definition if found, error otherwise
     */
    suspend fun getExerciseDefinitionById(id: Long): Result<ExerciseDefinition>
    
    /**
     * Create a new exercise definition.
     * 
     * @param name The name of the exercise
     * @param type The type of the exercise
     * @return Result containing the created exercise definition with server-assigned ID if successful, error otherwise
     */
    suspend fun createExerciseDefinition(name: String, type: ExerciseTypeEnum): Result<ExerciseDefinition>
    
    /**
     * Create a new exercise record.
     * 
     * @param exercise The exercise record to create
     * @return Result containing the created exercise record with server-assigned ID if successful, error otherwise
     */
    suspend fun createExercise(exercise: Exercise): Result<Exercise>
} 