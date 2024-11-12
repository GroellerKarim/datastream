package eu.groeller.datastreamserver.presentation.response.exercise;

import eu.groeller.datastreamserver.domain.exercise.ExerciseSet;

import java.time.OffsetDateTime;

public record ExerciseSetResponse(
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Boolean failure,
        Integer repetitions
) {
    public ExerciseSetResponse(ExerciseSet set) {
        this(
                set.getStartTime(),
                set.getEndTime(),
                set.getFailure(),
                set.getRepetitions()
        );
    }
}
