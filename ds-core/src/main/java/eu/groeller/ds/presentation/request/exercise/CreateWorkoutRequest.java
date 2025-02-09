package eu.groeller.ds.presentation.request.exercise;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateWorkoutRequest(
        List<ExerciseRecordRequest> exercises,
        String type,
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {
}
