package eu.groeller.ds.presentation.request.exercise;

import java.time.OffsetDateTime;

public record CompleteWorkoutRequest(
        OffsetDateTime endTime) {
}
