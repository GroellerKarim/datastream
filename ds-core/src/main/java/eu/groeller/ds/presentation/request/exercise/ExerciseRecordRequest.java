package eu.groeller.ds.presentation.request.exercise;

import java.time.OffsetDateTime;

public record ExerciseRecordRequest(
        Long exerciseDefinitionId,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        ExerciseRecordDetailsRequest details,
        Integer order
) {
}
