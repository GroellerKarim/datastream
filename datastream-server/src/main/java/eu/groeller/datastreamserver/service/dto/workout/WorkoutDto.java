package eu.groeller.datastreamserver.service.dto.workout;

import java.time.OffsetDateTime;
import java.util.List;

public record WorkoutDto(
        int workoutPlanId,
        List<TrackedExerciseDto> exercises,
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {
}
