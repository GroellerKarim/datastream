package eu.groeller.dsui.domain.usecase.exercise

import eu.groeller.dsui.domain.model.ExerciseDefinition
import eu.groeller.dsui.domain.repository.IExerciseRepository

/**
 * Use case for retrieving all exercise definitions.
 */
class GetExerciseDefinitionsUseCase(private val exerciseRepository: IExerciseRepository) {
    
    /**
     * Retrieves all exercise definitions.
     * 
     * @return Result containing a list of exercise definitions or an error.
     */
    suspend operator fun invoke(): Result<List<ExerciseDefinition>> {
        return exerciseRepository.getExerciseDefinitions()
    }
} 