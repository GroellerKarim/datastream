package eu.groeller.datastreamserver.presentation.request.exercise;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateWorkoutRequest(
        OffsetDateTime date,
        List<ExerciseRecordRequest> exercises
) {
}
