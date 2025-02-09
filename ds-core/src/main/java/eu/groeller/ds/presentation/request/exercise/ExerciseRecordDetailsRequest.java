package eu.groeller.ds.presentation.request.exercise;

import eu.groeller.ds.domain.exercise.DistanceUnit;

import java.util.List;

public record ExerciseRecordDetailsRequest(
        // For DistanceExerciseRecord
        Double distance,
        DistanceUnit distanceUnit,
        Double distancePerUnit,

        // For SetBasedExerciseRecord
        List<ExerciseSetRequest> sets,

        // Common for both
        Double weightKg
) {
}
