package eu.groeller.datastreamserver.service.dto.workout;

import java.time.OffsetDateTime;

public record TrackedExerciseDto(
        Long exerciseId,
        Long seconds,
        Float distance,
        Float weight,
        Float bodyweight,
        Short repetitions,
        Short partialRepetitions,

        Boolean toFailure,
        OffsetDateTime entryTime,
        Short position
) {
}
