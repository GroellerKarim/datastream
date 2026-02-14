package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.DistanceUnit;
import eu.groeller.ds.domain.exercise.ExerciseEntry;
import eu.groeller.ds.domain.exercise.ExerciseType;

import java.time.OffsetDateTime;

public record ExerciseEntryResponse(
        Long id,
        String exerciseName,
        ExerciseType exerciseType,
        Integer orderIndex,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Double weightKg,
        Integer reps,
        Boolean failure,
        Integer partialReps,
        Double distance,
        DistanceUnit distanceUnit,
        Long durationSeconds) {
    public static ExerciseEntryResponse from(ExerciseEntry entry) {
        return new ExerciseEntryResponse(
                entry.getId(),
                entry.getExerciseDefinition().getName(),
                entry.getExerciseDefinition().getType(),
                entry.getOrderIndex(),
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getWeightKg(),
                entry.getReps(),
                entry.getFailure(),
                entry.getPartialReps(),
                entry.getDistance(),
                entry.getDistanceUnit(),
                entry.getDurationSeconds());
    }
}
