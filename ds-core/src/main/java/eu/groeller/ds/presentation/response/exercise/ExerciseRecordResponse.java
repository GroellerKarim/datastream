package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.ExerciseRecord;
import eu.groeller.ds.domain.exercise.ExerciseType;

import java.time.OffsetDateTime;

public record ExerciseRecordResponse(
        Long exerciseRecordId,
        Long exerciseDefinitionId,
        String exerciseName,
        ExerciseType type,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        ExerciseRecordDetailsResponse details,
        Integer orderIndex
) {
    public ExerciseRecordResponse(ExerciseRecord record) {
        this(
                record.getId(),
                record.getExerciseDefinition().getId(),
                record.getExerciseDefinition().getName(),
                record.getExerciseDefinition().getType(),
                record.getStartTime(),
                record.getEndTime(),
                new ExerciseRecordDetailsResponse(record),
                record.getOrderIndex()
        );
    }
}
