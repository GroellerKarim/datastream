package eu.groeller.dsui.domain.model

/**
 * Represents the type of exercise, which determines how it's tracked.
 * This corresponds to the backend's ExerciseType enum.
 */
enum class ExerciseTypeEnum {
    /**
     * Exercise tracked by sets and repetitions (e.g., bench press, squats)
     */
    SETS_REPS,
    
    /**
     * Exercise tracked by distance (e.g., running, cycling)
     */
    DISTANCE,
    
    /**
     * Exercise tracked by sets and time duration (e.g., planks)
     */
    SETS_TIME
} 