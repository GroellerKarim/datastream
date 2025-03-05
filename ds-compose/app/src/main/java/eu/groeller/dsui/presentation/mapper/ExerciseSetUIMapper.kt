package eu.groeller.dsui.presentation.mapper

import eu.groeller.dsui.domain.model.ExerciseSet
import eu.groeller.dsui.presentation.model.ExerciseSetUI
import java.time.Duration
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

/**
 * Mapper for converting ExerciseSet domain model to ExerciseSetUI presentation model.
 */
object ExerciseSetUIMapper {
    
    /**
     * Converts a domain ExerciseSet to a UI model.
     */
    fun ExerciseSet.toUI(
        orderIndex: Int,
        isSelected: Boolean = false,
        isInProgress: Boolean = false
    ): ExerciseSetUI {
        val formattedWeight = weightKg?.let { formatWeight(it) }
        val formattedDuration = formatDuration(startTime, endTime)
        
        return ExerciseSetUI(
            startTime = startTime,
            endTime = endTime,
            isFailure = failure,
            repetitions = repetitions,
            partialRepetitions = null,  // Not in domain model, set to null
            weightKg = weightKg,
            orderIndex = orderIndex,
            isSelected = isSelected,
            isInProgress = isInProgress,
            formattedWeight = formattedWeight,
            formattedDuration = formattedDuration
        )
    }
    
    /**
     * Converts a list of domain ExerciseSet objects to UI models.
     */
    fun List<ExerciseSet>.toUI(
        selectedIndex: Int? = null
    ): List<ExerciseSetUI> {
        return this.mapIndexed { index, set ->
            set.toUI(
                orderIndex = index,
                isSelected = selectedIndex == index
            )
        }
    }
    
    /**
     * Format weight value for display.
     */
    private fun formatWeight(weight: Double): String {
        return if (weight % 1.0 == 0.0) {
            "${weight.toInt()} kg"
        } else {
            "%.1f kg".format(weight)
        }
    }
    
    /**
     * Format duration between start and end times.
     */
    private fun formatDuration(startTime: OffsetDateTime, endTime: OffsetDateTime): String? {
        val durationMs = Duration.between(startTime, endTime).toMillis()
        
        if (durationMs <= 0) return null
        
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        
        return when {
            minutes > 0 -> "${minutes}:${seconds % 60}"
            else -> "${seconds}s"
        }
    }
    
    /**
     * Creates a new ExerciseSetUI with updated selection state.
     */
    fun ExerciseSetUI.withSelectionState(isSelected: Boolean): ExerciseSetUI {
        return this.copy(isSelected = isSelected)
    }
    
    /**
     * Creates a new ExerciseSetUI with updated progress state.
     */
    fun ExerciseSetUI.withProgressState(isInProgress: Boolean): ExerciseSetUI {
        return this.copy(isInProgress = isInProgress)
    }
    
    /**
     * Creates a new ExerciseSetUI with updated end time.
     */
    fun ExerciseSetUI.withEndTime(endTime: OffsetDateTime): ExerciseSetUI {
        val newSet = this.copy(endTime = endTime)
        return newSet.copy(
            formattedDuration = formatDuration(this.startTime, endTime)
        )
    }
    
    /**
     * Creates a new ExerciseSetUI with updated repetitions.
     */
    fun ExerciseSetUI.withRepetitions(repetitions: Int): ExerciseSetUI {
        return this.copy(repetitions = repetitions)
    }
    
    /**
     * Creates a new ExerciseSetUI with updated weight.
     */
    fun ExerciseSetUI.withWeight(weightKg: Double): ExerciseSetUI {
        return this.copy(
            weightKg = weightKg,
            formattedWeight = formatWeight(weightKg)
        )
    }
    
    /**
     * Creates a new ExerciseSetUI with failure state.
     */
    fun ExerciseSetUI.withFailureState(isFailure: Boolean): ExerciseSetUI {
        return this.copy(isFailure = isFailure)
    }
} 