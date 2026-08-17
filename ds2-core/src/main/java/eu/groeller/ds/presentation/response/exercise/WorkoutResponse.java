package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.WorkoutData;
import eu.groeller.ds.domain.exercise.WorkoutStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WorkoutResponse(
        UUID id,
        String workoutType,
        WorkoutStatus status,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Long durationMs,
        List<ExerciseEntryResponse> entries) {
    public static WorkoutResponse from(WorkoutData workout) {
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
