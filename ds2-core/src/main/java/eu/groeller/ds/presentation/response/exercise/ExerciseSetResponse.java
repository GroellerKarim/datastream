package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.DistanceUnit;
import eu.groeller.ds.domain.exercise.ExerciseSetData;

import java.time.OffsetDateTime;

/**
 * @param restBeforeMs gap since the previous set ended; null for the first set of an entry.
 *                     Derived from the set timestamps, never stored.
 */
public record ExerciseSetResponse(
        Long id,
        Integer orderIndex,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Long restBeforeMs,
        Double weightKg,
        Integer reps,
        Boolean failure,
        Integer partialReps,
        Double distance,
        DistanceUnit distanceUnit,
        Long durationSeconds) {

    public static ExerciseSetResponse from(ExerciseSetData set, Long restBeforeMs) {
        return new ExerciseSetResponse(
                set.getId(),
                set.getOrderIndex(),
                set.getStartTime(),
                set.getEndTime(),
                restBeforeMs,
                set.getWeightKg(),
                set.getReps(),
                set.getFailure(),
                set.getPartialReps(),
                set.getDistance(),
                set.getDistanceUnit(),
                set.getDurationSeconds());
    }
}
