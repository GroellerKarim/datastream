package eu.groeller.ds.presentation.request.exercise;

import eu.groeller.ds.domain.exercise.DistanceUnit;

import java.time.OffsetDateTime;

/**
 * Flat on the wire: the client sends whichever measurement fields apply to the exercise type and
 * leaves the rest null. Narrowing to the typed metrics happens in the domain.
 */
public record CreateExerciseSetRequest(
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
