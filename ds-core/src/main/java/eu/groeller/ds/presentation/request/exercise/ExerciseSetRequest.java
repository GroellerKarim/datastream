package eu.groeller.ds.presentation.request.exercise;

import java.time.OffsetDateTime;

public record ExerciseSetRequest(
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Boolean isFailure,
        Integer repetitions,
        Integer partialRepetitions,
        Double weight,
        Integer order
) {
}
