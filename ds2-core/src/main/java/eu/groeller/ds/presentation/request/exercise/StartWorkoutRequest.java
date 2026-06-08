package eu.groeller.ds.presentation.request.exercise;

import java.time.OffsetDateTime;

public record StartWorkoutRequest(
        String workoutTypeName,
        OffsetDateTime startTime) {
}
