package eu.groeller.dsui.presentation.model

import eu.groeller.dsui.domain.model.ExerciseTypeEnum
import java.time.OffsetDateTime

/**
 * UI model for representing an exercise in the user interface.
 * Includes all data necessary for display and mapping to backend requests.
 */
data class ExerciseUI(
    val id: Long? = null,
    val exerciseDefinitionId: Long,
    val name: String,
    val type: ExerciseTypeEnum,
    val startTime: OffsetDateTime,
    val endTime: OffsetDateTime,
    val details: ExerciseDetailsUI,
    val orderIndex: Int,
    
    // Additional UI-specific properties
    val isExpanded: Boolean = false,
    val isSelected: Boolean = false,
    val isInProgress: Boolean = false
) 