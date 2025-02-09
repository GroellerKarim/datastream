package eu.groeller.ds.presentation.response.exercise;

import eu.groeller.ds.domain.exercise.ExerciseSet;

import java.time.OffsetDateTime;

public record ExerciseSetResponse(
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Boolean failure,
        Integer repetitions,
        Double weightKg
) {
    public ExerciseSetResponse(ExerciseSet set) {
        this(
                set.getStartTime(),
                set.getEndTime(),
                set.getFailure(),
                set.getRepetitions(),
                set.getWeightKg()
        );
    }
}
