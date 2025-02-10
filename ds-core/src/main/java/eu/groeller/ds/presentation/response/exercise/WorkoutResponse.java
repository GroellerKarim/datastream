package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.Workout;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record WorkoutResponse(
        Long workoutId,
        Long durationMs,
        OffsetDateTime date,
        Set<ExerciseRecordResponse> exercises,
        String workoutType
) {
    public WorkoutResponse(Workout workout) {
        this(
                workout.getId(),
                workout.getDuration(),
                workout.getStartTime(),
                workout.getExercises().stream()
                        .map(ExerciseRecordResponse::new)
                        .collect(Collectors.toSet()),
                workout.getWorkoutType().getName()
        );
    }
}
