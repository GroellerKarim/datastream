package eu.groeller.datastreamserver.presentation.request.exercise;

import java.time.OffsetDateTime;

public record ExerciseSetRequest(
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Boolean failure,
        Integer repetitions,
        Double weightKg,
        Integer order
) {}
