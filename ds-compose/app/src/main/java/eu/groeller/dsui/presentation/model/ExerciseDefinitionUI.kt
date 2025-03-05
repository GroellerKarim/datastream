package eu.groeller.dsui.presentation.model

import eu.groeller.dsui.domain.model.ExerciseTypeEnum

/**
 * UI model for representing an exercise definition in the user interface.
 */
data class ExerciseDefinitionUI(
    val id: Long,
    val name: String,
    val type: ExerciseTypeEnum,
    
    // Additional UI-specific properties
    val isSelected: Boolean = false,
    val isFavorite: Boolean = false,
    val usageCount: Int? = null  // How many times this exercise has been used
) 