package eu.groeller.dsui.presentation.mapper

import eu.groeller.dsui.domain.model.WorkoutType
import eu.groeller.dsui.presentation.model.WorkoutTypeUI

/**
 * Mapper for converting WorkoutType domain model to WorkoutTypeUI presentation model.
 */
object WorkoutTypeUIMapper {
    
    /**
     * Converts a domain WorkoutType to a UI model.
     */
    fun WorkoutType.toUI(isSelected: Boolean = false, count: Int? = null): WorkoutTypeUI {
        return WorkoutTypeUI(
            id = id,
            name = name,
            isSelected = isSelected,
            count = count
        )
    }
    
    /**
     * Converts a list of domain WorkoutType objects to UI models.
     */
    fun List<WorkoutType>.toUI(selectedId: Long? = null): List<WorkoutTypeUI> {
        return this.map { 
            it.toUI(isSelected = it.id == selectedId) 
        }
    }
    
    /**
     * Creates a new WorkoutTypeUI with updated selection state.
     */
    fun WorkoutTypeUI.withSelectionState(isSelected: Boolean): WorkoutTypeUI {
        return this.copy(isSelected = isSelected)
    }
} 