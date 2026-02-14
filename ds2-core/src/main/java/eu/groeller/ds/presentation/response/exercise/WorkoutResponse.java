package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.Workout;
import eu.groeller.ds.domain.exercise.WorkoutStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record WorkoutResponse(
        Long id,
        String workoutType,
        WorkoutStatus status,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Long durationMs,
        List<ExerciseEntryResponse> entries) {
    public static WorkoutResponse from(Workout workout) {
        return new WorkoutResponse(
                workout.getId(),
                workout.getWorkoutType().getName(),
                workout.getStatus(),
                workout.getStartTime(),
                workout.getEndTime(),
                workout.getDurationMs(),
                workout.getEntries().stream()
                        .map(ExerciseEntryResponse::from)
                        .toList());
    }
}
