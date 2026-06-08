package eu.groeller.ds.presentation.request.exercise;

import eu.groeller.ds.domain.exercise.DistanceUnit;

import java.time.OffsetDateTime;

public record CreateExerciseEntryRequest(
        Long exerciseDefinitionId,
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
}
