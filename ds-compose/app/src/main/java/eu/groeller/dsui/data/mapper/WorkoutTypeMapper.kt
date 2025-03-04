package eu.groeller.dsui.data.mapper

import eu.groeller.datastreamui.data.model.WorkoutType as DataWorkoutType
import eu.groeller.dsui.domain.model.WorkoutType

/**
 * Mapper for converting between domain WorkoutType models and data layer workout type models.
 */
object WorkoutTypeMapper {
    
    /**
     * Converts a data layer WorkoutType to a domain WorkoutType model.
     */
    fun DataWorkoutType.toDomain(): WorkoutType {
        return WorkoutType(
            id = id,
            name = name
        )
    }
} 