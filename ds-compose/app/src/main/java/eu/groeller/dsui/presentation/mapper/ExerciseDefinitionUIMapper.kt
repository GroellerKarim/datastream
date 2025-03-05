package eu.groeller.dsui.presentation.mapper

import eu.groeller.dsui.domain.model.ExerciseDefinition
import eu.groeller.dsui.presentation.model.ExerciseDefinitionUI

/**
 * Mapper for converting ExerciseDefinition domain model to ExerciseDefinitionUI presentation model.
 */
object ExerciseDefinitionUIMapper {
    
    /**
     * Converts a domain ExerciseDefinition to a UI model.
     */
    fun ExerciseDefinition.toUI(
        isSelected: Boolean = false,
        isFavorite: Boolean = false,
        usageCount: Int? = null
    ): ExerciseDefinitionUI {
        return ExerciseDefinitionUI(
            id = id,
            name = name,
            type = type,
            isSelected = isSelected,
            isFavorite = isFavorite,
            usageCount = usageCount
        )
    }
    
    /**
     * Converts a list of domain ExerciseDefinition objects to UI models.
     */
    fun List<ExerciseDefinition>.toUI(selectedId: Long? = null): List<ExerciseDefinitionUI> {
        return this.map { 
            it.toUI(isSelected = it.id == selectedId) 
        }
    }
    
    /**
     * Creates a new ExerciseDefinitionUI with updated selection state.
     */
    fun ExerciseDefinitionUI.withSelectionState(isSelected: Boolean): ExerciseDefinitionUI {
        return this.copy(isSelected = isSelected)
    }
    
    /**
     * Creates a new ExerciseDefinitionUI with updated favorite state.
     */
    fun ExerciseDefinitionUI.withFavoriteState(isFavorite: Boolean): ExerciseDefinitionUI {
        return this.copy(isFavorite = isFavorite)
    }
} 