package eu.groeller.dsui.presentation.mapper

import eu.groeller.dsui.domain.model.DistanceUnitEnum
import eu.groeller.dsui.domain.model.Exercise
import eu.groeller.dsui.domain.model.ExerciseDetails
import eu.groeller.dsui.domain.model.ExerciseTypeEnum
import eu.groeller.dsui.presentation.mapper.ExerciseSetUIMapper.toUI
import eu.groeller.dsui.presentation.model.ExerciseDetailsUI
import eu.groeller.dsui.presentation.model.ExerciseUI
import java.time.Duration
import java.time.OffsetDateTime

/**
 * Mapper for converting Exercise domain model to ExerciseUI presentation model.
 */
object ExerciseUIMapper {
    
    /**
     * Converts a domain Exercise to a UI model.
     */
    fun Exercise.toUI(
        isExpanded: Boolean = false,
        isSelected: Boolean = false,
        isInProgress: Boolean = false
    ): ExerciseUI {
        return ExerciseUI(
            id = id,
            exerciseDefinitionId = exerciseDefinitionId,
            name = exerciseName,
            type = type,
            startTime = startTime,
            endTime = endTime,
            details = details.toUI(),
            orderIndex = orderIndex,
            isExpanded = isExpanded,
            isSelected = isSelected,
            isInProgress = isInProgress
        )
    }
    
    /**
     * Converts a list of domain Exercise objects to UI models.
     */
    fun List<Exercise>.toUI(selectedId: Long? = null): List<ExerciseUI> {
        return this.map { 
            it.toUI(isSelected = it.id == selectedId) 
        }.sortedBy { it.orderIndex }
    }
    
    /**
     * Converts domain ExerciseDetails to UI model.
     */
    private fun ExerciseDetails.toUI(): ExerciseDetailsUI {
        // Format distance and pace if this is a distance exercise
        val formattedDistance = if (distance != null && distanceUnit != null) {
            formatDistance(distance, distanceUnit)
        } else null
        
        val formattedPace = if (distance != null && distanceUnit != null) {
            formatPace(distance, distanceUnit, startTime = null, endTime = null)
        } else null
        
        return ExerciseDetailsUI(
            distance = distance,
            distanceUnit = distanceUnit,
            distancePerUnit = null,  // Not in domain model, will be calculated if needed
            sets = sets?.toUI(),
            weightKg = weightKg,
            formattedDistance = formattedDistance,
            formattedPace = formattedPace
        )
    }
    
    /**
     * Format distance value for display based on unit.
     */
    private fun formatDistance(distance: Double, unit: DistanceUnitEnum): String {
        return if (distance % 1.0 == 0.0) {
            "${distance.toInt()} ${unit.abbreviation}"
        } else {
            "%.2f ${unit.abbreviation}".format(distance)
        }
    }
    
    /**
     * Format pace (min/km, min/mile, etc.) based on distance and duration.
     */
    private fun formatPace(
        distance: Double,
        unit: DistanceUnitEnum,
        startTime: OffsetDateTime?,
        endTime: OffsetDateTime?
    ): String? {
        // If either time is null or distance is zero, can't calculate pace
        if (startTime == null || endTime == null || distance <= 0) return null
        
        val durationMinutes = Duration.between(startTime, endTime).toMinutes()
        if (durationMinutes <= 0) return null
        
        val pacePerUnit = durationMinutes / distance
        val minutes = pacePerUnit.toInt()
        val seconds = ((pacePerUnit - minutes) * 60).toInt()
        
        return String.format("%d:%02d min/%s", minutes, seconds, unit.abbreviation)
    }
    
    /**
     * Creates a new ExerciseUI with updated selection state.
     */
    fun ExerciseUI.withSelectionState(isSelected: Boolean): ExerciseUI {
        return this.copy(isSelected = isSelected)
    }
    
    /**
     * Creates a new ExerciseUI with updated expansion state.
     */
    fun ExerciseUI.withExpansionState(isExpanded: Boolean): ExerciseUI {
        return this.copy(isExpanded = isExpanded)
    }
    
    /**
     * Creates a new ExerciseUI with updated progress state.
     */
    fun ExerciseUI.withProgressState(isInProgress: Boolean): ExerciseUI {
        return this.copy(isInProgress = isInProgress)
    }
    
    /**
     * Creates a new ExerciseUI with updated end time.
     */
    fun ExerciseUI.withEndTime(endTime: OffsetDateTime): ExerciseUI {
        return this.copy(endTime = endTime)
    }
    
    /**
     * Creates a new ExerciseUI with updated details.
     */
    fun ExerciseUI.withDetails(details: ExerciseDetailsUI): ExerciseUI {
        return this.copy(details = details)
    }
} 