package eu.groeller.dsui.presentation.mapper

import eu.groeller.dsui.domain.model.Workout
import eu.groeller.dsui.presentation.mapper.ExerciseUIMapper.toUI
import eu.groeller.dsui.presentation.model.WorkoutUI
import java.time.Duration
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Mapper for converting Workout domain model to WorkoutUI presentation model.
 */
object WorkoutUIMapper {
    
    // Formatters for display
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    /**
     * Converts a domain Workout to a UI model.
     */
    fun Workout.toUI(
        isExpanded: Boolean = false,
        isSelected: Boolean = false
    ): WorkoutUI {
        val durationDisplay = formatDuration(durationMs)
        
        return WorkoutUI(
            id = id,
            workoutType = workoutType,
            startTime = startTime,
            endTime = endTime,
            durationDisplay = durationDisplay,
            exercises = exercises.toList().toUI(),
            isExpanded = isExpanded,
            isSelected = isSelected
        )
    }
    
    /**
     * Converts a list of domain Workout objects to UI models.
     */
    fun List<Workout>.toUI(selectedId: Long? = null): List<WorkoutUI> {
        return this.map { 
            it.toUI(isSelected = it.id == selectedId) 
        }
    }
    
    /**
     * Format duration in milliseconds to human-readable format.
     */
    private fun formatDuration(durationMs: Long): String {
        val seconds = durationMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> String.format("%dh %02dm", hours, minutes % 60)
            minutes > 0 -> String.format("%dm %02ds", minutes, seconds % 60)
            else -> String.format("%ds", seconds)
        }
    }
    
    /**
     * Format date for display.
     */
    fun formatDate(dateTime: OffsetDateTime): String {
        return dateTime.format(dateFormatter)
    }
    
    /**
     * Format time for display.
     */
    fun formatTime(dateTime: OffsetDateTime): String {
        return dateTime.format(timeFormatter)
    }
    
    /**
     * Creates a new WorkoutUI with updated selection state.
     */
    fun WorkoutUI.withSelectionState(isSelected: Boolean): WorkoutUI {
        return this.copy(isSelected = isSelected)
    }
    
    /**
     * Creates a new WorkoutUI with updated expansion state.
     */
    fun WorkoutUI.withExpansionState(isExpanded: Boolean): WorkoutUI {
        return this.copy(isExpanded = isExpanded)
    }
    
    /**
     * Creates a new WorkoutUI with updated end time and recalculated duration.
     */
    fun WorkoutUI.withEndTime(endTime: OffsetDateTime): WorkoutUI {
        val durationMs = Duration.between(startTime, endTime).toMillis()
        return this.copy(
            endTime = endTime,
            durationDisplay = formatDuration(durationMs)
        )
    }
    
    /**
     * Creates a new WorkoutUI with updated exercises.
     */
    fun WorkoutUI.withExercises(exercises: List<eu.groeller.dsui.presentation.model.ExerciseUI>): WorkoutUI {
        return this.copy(exercises = exercises)
    }
    
    /**
     * Create a workout UI object from its components (useful for creating new workouts).
     */
    fun createWorkoutUI(
        workoutType: String,
        startTime: OffsetDateTime = OffsetDateTime.now(),
        exercises: List<eu.groeller.dsui.presentation.model.ExerciseUI> = emptyList()
    ): WorkoutUI {
        // For a new workout, set end time equal to start time initially
        val endTime = startTime
        
        return WorkoutUI(
            id = null, // No ID for a new workout
            workoutType = workoutType,
            startTime = startTime,
            endTime = endTime,
            durationDisplay = "0s", // No duration initially
            exercises = exercises,
            isExpanded = true, // New workout is expanded by default
            isSelected = true  // New workout is selected by default
        )
    }
} 