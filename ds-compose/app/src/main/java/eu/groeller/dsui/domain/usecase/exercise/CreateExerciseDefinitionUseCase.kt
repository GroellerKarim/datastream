package eu.groeller.dsui.domain.usecase.exercise

import eu.groeller.dsui.domain.model.ExerciseDefinition
import eu.groeller.dsui.domain.model.ExerciseTypeEnum
import eu.groeller.dsui.domain.repository.IExerciseRepository

/**
 * Use case for creating a new exercise definition.
 */
class CreateExerciseDefinitionUseCase(private val exerciseRepository: IExerciseRepository) {
    
    /**
     * Creates a new exercise definition with the given name and type.
     * 
     * @param name The name of the exercise definition.
     * @param type The type of the exercise definition.
     * @return Result containing the created exercise definition or an error.
     */
    suspend operator fun invoke(name: String, type: ExerciseTypeEnum): Result<ExerciseDefinition> {
        return exerciseRepository.createExerciseDefinition(name, type)
    }
} 